package es.iesjandula.reaktor.images_cloner_server.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import es.iesjandula.reaktor.base.utils.BaseConstants;
import es.iesjandula.reaktor.images_cloner_server.service.ImagenClonezillaService;
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
	@Autowired
	private ImagenClonezillaService imagenClonezillaService ;

	@PreAuthorize("hasRole('" + BaseConstants.ROLE_CLIENTE_CLONADOR_IMAGENES + "')")
	@PostMapping(value = "/", consumes = "application/json")
	@Transactional
	public ResponseEntity<?> actualizarImagenesActuales(@RequestBody(required = true) List<String> nombreImagenes)
	{
		try
		{
			// Validamos el nombre de la imagen
			if (nombreImagenes == null)
			{
				// Logueamos la excepción
				log.error(Constants.ERR_LISTA_NOMBRES_IMAGENES_VACIA) ;

				// Devolvemos la excepción de servidor
				throw new ImagesClonerServerException(Constants.ERR_VALIDACION_CODE, Constants.ERR_LISTA_NOMBRES_IMAGENES_VACIA) ;
			}

			// Actualizamos las imágenes actuales
			List<ImagenClonezilla> imagenesClonezillaList = this.actualizarImagenesActualesInternal(nombreImagenes) ;

			// Aprovechamos para devolver la lista de imágenes
			return ResponseEntity.ok(imagenesClonezillaList) ;
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
	 * @throws ImagesClonerServerException Excepción de servidor.
	 */
	private List<ImagenClonezilla> actualizarImagenesActualesInternal(List<String> nombreImagenes) throws ImagesClonerServerException
	{
        // Creamos una lista con todas las imágenes
        List<ImagenClonezilla> imagenesClonezillaList = new ArrayList<ImagenClonezilla>() ;

		// Eliminamos de BBDD las imágenes que no están en la lista de imágenes
		this.imagenRepository.eliminarImagenesNoEnLista(nombreImagenes) ;

		// Hacemos un bucle sobre todas las imágenes
		for (String nombreImagen : nombreImagenes)
		{
			// Validamos el nombre de la imagen
			if (nombreImagen == null || nombreImagen.isBlank())
			{
				// Logueamos la excepción
				log.error(Constants.ERR_NOMBRE_IMAGEN_VACIO) ;

				// Devolvemos la excepción de servidor
				throw new ImagesClonerServerException(Constants.ERR_VALIDACION_CODE, Constants.ERR_NOMBRE_IMAGEN_VACIO) ;
			}

			// Creamos una variable para la imagen
			ImagenClonezilla imagenClonezilla = null ;

			// Buscamos la imagen en la base de datos
			Optional<ImagenClonezilla> optionalImagenClonezilla = this.imagenRepository.findById(nombreImagen) ;

			// Si la imagen no existe, la creamos
			if (optionalImagenClonezilla.isEmpty())
			{
				// Creamos la imagen
				imagenClonezilla = new ImagenClonezilla(nombreImagen, EstadoImagen.DESACTIVADA, Constants.ACCION_POR_DEFECTO) ;

				// Guardamos la imagen en la base de datos
				this.imagenRepository.save(imagenClonezilla) ;
			}
			else
			{
				// Obtenemos la imagen
				imagenClonezilla = optionalImagenClonezilla.get() ;
			}

			// Añadimos la imagen a la lista
			imagenesClonezillaList.add(imagenClonezilla) ;
		}

		// Devolvemos la lista de imágenes
		return imagenesClonezillaList ;
	}

	@PreAuthorize("hasRole('" + BaseConstants.ROLE_CLIENTE_CLONADOR_IMAGENES + "')")
	@PutMapping("/")
	public ResponseEntity<?> actualizarEstadoActivada(@RequestHeader(value = "nombreImagen", required = true) String nombreImagen)
	{
		try
		{
			// Validamos el nombre de la imagen
			if (nombreImagen == null || nombreImagen.isBlank())
			{
				// Logueamos la excepción
				log.error(Constants.ERR_NOMBRE_IMAGEN_VACIO) ;

				// Devolvemos la excepción de servidor
				throw new ImagesClonerServerException(Constants.ERR_VALIDACION_CODE, Constants.ERR_NOMBRE_IMAGEN_VACIO) ;
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