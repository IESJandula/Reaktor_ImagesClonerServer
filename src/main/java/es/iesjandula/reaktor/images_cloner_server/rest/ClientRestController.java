package es.iesjandula.reaktor.images_cloner_server.rest;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import es.iesjandula.reaktor.images_cloner_server.dto.ConfiguracionClonadorDto;
import es.iesjandula.reaktor.images_cloner_server.models.ImagenClonezilla;
import es.iesjandula.reaktor.base.utils.BaseConstants;
import es.iesjandula.reaktor.images_cloner_server.repository.IImagenClonezillaRepository;
import es.iesjandula.reaktor.images_cloner_server.utils.Constants;
import es.iesjandula.reaktor.images_cloner_server.utils.ImagesClonerServerException;
import lombok.extern.slf4j.Slf4j;

/**
 * Cliente PXE / host Clonezilla: sincronización y activación de imágenes.
 */
@Slf4j
@RestController
@RequestMapping("/images_cloner/client")
public class ClientRestController
{
	/**
	 * Repositorio de imágenes Clonezilla.
	 */
	@Autowired
	private IImagenClonezillaRepository imagenRepository ;

	/**
	 * Actualiza las imágenes actuales.
	 * @param nombreImagenes Lista de nombres de imágenes.
	 * @return ResponseEntity con la lista de imágenes.
	 * @throws ImagesClonerServerException Excepción de servidor.
	 */	
	@PreAuthorize("hasRole('" + BaseConstants.ROLE_CLIENTE_CLONADOR_IMAGENES + "')")
	@PostMapping(value = "/", consumes = "application/json")
	@Transactional
	public ResponseEntity<?> actualizarImagenesActuales(@RequestBody List<String> nombreImagenes)
	{
		try
		{
			// Validamos el nombre de la imagen
			if (nombreImagenes == null)
			{
				// Logueamos la excepción
				log.error(Constants.ERR_LISTA_NOMBRES_IMAGENES_VACIA_DESC) ;

				// Devolvemos la excepción de servidor
				throw new ImagesClonerServerException(Constants.ERR_LISTA_NOMBRES_IMAGENES_VACIA_CODE, Constants.ERR_LISTA_NOMBRES_IMAGENES_VACIA_DESC) ;
			}

			// Actualizamos las imágenes actuales y devolvemos la configuración del clonador
			ConfiguracionClonadorDto configuracionClonadorDto = this.actualizarImagenesActualesObtenerConfiguradorClonador(nombreImagenes) ;

			// Aprovechamos para devolver la configuración del clonador
			return ResponseEntity.ok(configuracionClonadorDto) ;
		}
		catch (ImagesClonerServerException exception)
		{
			return ResponseEntity.status(400).body(exception.getBodyExceptionMessage()) ;
		}
		catch (Exception exception)
		{
			// Creamos la excepción de servidor
			ImagesClonerServerException serverException = 
				new ImagesClonerServerException(BaseConstants.ERR_GENERIC_EXCEPTION_CODE, BaseConstants.ERR_GENERIC_EXCEPTION_MSG + "actualizarImagenesActuales", exception) ;
			
			// Logueamos la excepción
			log.error(serverException.getMessage(), exception) ;
		
			// Devolvemos la excepción de servidor
			return ResponseEntity.status(500).body(serverException.getBodyExceptionMessage()) ;
		}
	}

	/**
	 * Actualiza las imágenes actuales.
	 * @param nombreImagenes Lista de nombres de imágenes.
	 * @return Imagen Clonezilla que está pendiente de ser activada o null si no hay ninguna imagen pendiente.
	 * @throws ImagesClonerServerException Excepción de servidor.
	 */
	private ConfiguracionClonadorDto actualizarImagenesActualesObtenerConfiguradorClonador(List<String> nombreImagenes) throws ImagesClonerServerException
	{
		// Creamos una variable para la configuración del clonador
		ConfiguracionClonadorDto configuracionClonadorDto = new ConfiguracionClonadorDto() ;

		// Eliminamos de BBDD las imágenes que no están en la lista de imágenes
		this.imagenRepository.eliminarImagenesNoEnLista(nombreImagenes) ;

		// Hacemos un bucle sobre todas las imágenes
		for (String nombreImagen : nombreImagenes)
		{
			// Validamos el nombre de la imagen
			if (nombreImagen == null || nombreImagen.isBlank())
			{
				// Logueamos la excepción
				log.error(Constants.ERR_NOMBRE_IMAGEN_VACIO_DESC) ;

				// Devolvemos la excepción de servidor
				throw new ImagesClonerServerException(Constants.ERR_NOMBRE_IMAGEN_VACIO_CODE, Constants.ERR_NOMBRE_IMAGEN_VACIO_DESC) ;
			}

			this.actualizarImagenesActualesObtenerConfiguradorClonadorInternal(configuracionClonadorDto, nombreImagen) ;
		}

		// Devolvemos la configuración del clonador
		return configuracionClonadorDto ;
	}

	/**
	 * Actualiza las imágenes actuales.
	 * @param configuracionClonadorDto Configuración del clonador.
	 * @param nombreImagen Nombre de la imagen.
	 * @throws ImagesClonerServerException Excepción de servidor.
	 */
	private void actualizarImagenesActualesObtenerConfiguradorClonadorInternal(ConfiguracionClonadorDto configuracionClonadorDto, String nombreImagen) throws ImagesClonerServerException
	{
		// Buscamos la imagen en la base de datos
		Optional<ImagenClonezilla> optionalImagenClonezilla = this.imagenRepository.findById(nombreImagen) ;

		// Si la imagen no existe, la creamos y la añadimos a la lista
		if (!optionalImagenClonezilla.isPresent())
		{
			// Creamos una variable para la imagen
			ImagenClonezilla temp = new ImagenClonezilla(nombreImagen, Constants.ESTADO_DESACTIVADA, Constants.ACCION_POWEROFF) ;

			// Actualizamos la BBDD
	 		this.imagenRepository.saveAndFlush(temp) ;
        }
        else 
        {
	        // Obtenemos la imagen Clonezilla
	        ImagenClonezilla imagenClonezilla = optionalImagenClonezilla.get() ;

	        // Si la imagen está pendiente de ser activada, asignamos la configuración del clonador
			if (imagenClonezilla.getEstado().equals(Constants.ESTADO_PENDIENTE))
			{
				// Desactivamos el menú del clonador
				configuracionClonadorDto.setMenuActivo(false) ;

				// Activamos la imagen
				configuracionClonadorDto.setActivarImagen(true) ;

				// Asignamos el nombre de la imagen
				configuracionClonadorDto.setNombreImagen(imagenClonezilla.getNombreImagen()) ;
			}
		}
	}

	@PreAuthorize("hasRole('" + BaseConstants.ROLE_CLIENTE_CLONADOR_IMAGENES + "')")
	@PutMapping(value = "/")
	public ResponseEntity<?> actualizarEstadoActivada(@RequestHeader(value = "nombreImagen") String nombreImagen)
	{
		try
		{
			// Validamos el nombre de la imagen
			if (nombreImagen == null || nombreImagen.isBlank())
			{
				// Logueamos la excepción
				log.error(Constants.ERR_NOMBRE_IMAGEN_VACIO_DESC) ;

				// Devolvemos la excepción de servidor
				throw new ImagesClonerServerException(Constants.ERR_NOMBRE_IMAGEN_VACIO_CODE, Constants.ERR_NOMBRE_IMAGEN_VACIO_DESC) ;
			}

			// Actualizamos el estado de la imagen a activada
			this.imagenRepository.ponerImagenActivada(nombreImagen) ;

			// Devolvemos la respuesta
			return ResponseEntity.ok().build() ;
		}
		catch (ImagesClonerServerException exception)
		{
			return ResponseEntity.status(400).body(exception.getBodyExceptionMessage()) ;
		}
		catch (Exception exception)
		{
			// Creamos la excepción de servidor
			ImagesClonerServerException serverException = 
				new ImagesClonerServerException(BaseConstants.ERR_GENERIC_EXCEPTION_CODE, BaseConstants.ERR_GENERIC_EXCEPTION_MSG + "actualizarEstadoActivada", exception) ;
			
			// Logueamos la excepción
			log.error(serverException.getMessage(), exception) ;
		
			// Devolvemos la excepción de servidor
			return ResponseEntity.status(500).body(serverException.getBodyExceptionMessage()) ;
		}
	}
}