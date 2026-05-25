package es.iesjandula.reaktor.images_cloner_server.rest;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import es.iesjandula.reaktor.base.utils.BaseConstants;
import es.iesjandula.reaktor.images_cloner_server.dto.ImagenClonezillaDto;
import es.iesjandula.reaktor.images_cloner_server.repository.IImagenClonezillaRepository;
import es.iesjandula.reaktor.images_cloner_server.utils.Constants;
import es.iesjandula.reaktor.images_cloner_server.utils.ImagesClonerServerException;
import lombok.extern.slf4j.Slf4j;

/**
 * Panel web SomosJandula: gestión de imágenes Clonezilla por defecto.
 */
@Slf4j
@RestController
@RequestMapping("/images_cloner/admin")
public class AdminRestController
{
	/**
	 * Servicio de imágenes Clonezilla
	 */
	@Autowired
	private IImagenClonezillaRepository imagenRepository ;

	/**
	 * Lista todas las imágenes Clonezilla.
	 * @return Lista de imágenes Clonezilla.
	 */
	@PreAuthorize("hasAnyRole('" + BaseConstants.ROLE_ADMINISTRADOR + "', '" + BaseConstants.ROLE_DIRECCION + "')")
	@GetMapping("/")
	public ResponseEntity<?> listarImagenes()
	{
		try
		{
			// Llamamos al repositorio para obtener todas las imágenes Clonezilla
			List<ImagenClonezillaDto> imagenes = this.imagenRepository.buscarTodasLasImagenes() ;
			
			// Devolvemos la lista de imágenes Clonezilla
			return ResponseEntity.ok(imagenes) ;
		}
		catch (Exception exception)
		{
			// Creamos la excepción de servidor
			ImagesClonerServerException serverException = 
				new ImagesClonerServerException(BaseConstants.ERR_GENERIC_EXCEPTION_CODE, BaseConstants.ERR_GENERIC_EXCEPTION_MSG + "listarImagenes", exception) ;
			
			// Logueamos la excepción
			log.error(serverException.getMessage(), exception) ;
		
			// Devolvemos la excepción de servidor
			return ResponseEntity.status(500).body(serverException.getBodyExceptionMessage()) ;
		}
	}

	/**
	 * Establece la imagen por defecto.
	 * @param request Request con el nombre de la imagen y la acción.
	 * @return ResponseEntity con el resultado.
	 */
	@PreAuthorize("hasAnyRole('" + BaseConstants.ROLE_ADMINISTRADOR + "', '" + BaseConstants.ROLE_DIRECCION + "')")
	@PostMapping(value = "/")
	public ResponseEntity<?> establecerImagenPorDefecto(@RequestHeader(value = "nombreImagen") String nombreImagen, 
	                                                    @RequestHeader(value = "accion") String accion)
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

			// Normalizamos la acción
			String accionNormalizada = this.normalizarAccion(accion) ;

			// Desactivamos todas las imágenes
			this.imagenRepository.desactivarTodasLasImagenes() ;

			// Validamos si existe una fila con el nombre de la imagen
			if (this.imagenRepository.findById(nombreImagen).isEmpty())
			{
				// Creamos un mensaje de error
				String mensajeError = "La imagen no existe: " + nombreImagen ;

				// Logueamos la excepción
				log.error(mensajeError) ;

				// Devolvemos la excepción de servidor
				throw new ImagesClonerServerException(Constants.ERR_IMAGEN_NO_ENCONTRADA_CODE, mensajeError) ;
			}

			// Ponemos a pendiente la imagen que ha pasado por parámetro
			this.imagenRepository.ponerImagenAPendiente(nombreImagen, accionNormalizada) ;

			// Devolvemos la lista de imágenes
			return ResponseEntity.ok(this.imagenRepository.buscarTodasLasImagenes()) ;
		}
		catch (ImagesClonerServerException exception)
		{
			log.warn("establecerImagenPorDefecto: {}", exception.getMessage()) ;
			return ResponseEntity.status(400).body(exception.getBodyExceptionMessage()) ;
		}
		catch (Exception exception)
		{
			// Creamos la excepción de servidor
			ImagesClonerServerException serverException = 
				new ImagesClonerServerException(BaseConstants.ERR_GENERIC_EXCEPTION_CODE, BaseConstants.ERR_GENERIC_EXCEPTION_MSG + "establecerImagenPorDefecto", exception) ;
			
			// Logueamos la excepción
			log.error(serverException.getMessage(), exception) ;
		
			// Devolvemos la excepción de servidor
			return ResponseEntity.status(500).body(serverException.getBodyExceptionMessage()) ;
		}
	}

	/**
	 * Normaliza la acción.
	 * @param accion Acción a normalizar.
	 * @return Acción normalizada.
	 */
	private String normalizarAccion(String accion) throws ImagesClonerServerException
	{
		// Inicializamos la variable de salida con la acción por defecto que es poweroff
		String outcome = Constants.ACCION_POWEROFF ;

		// Si la acción es nula o vacía, devolvemos la acción por defecto
		if (accion != null && !accion.isBlank())
		{
			// Normalizamos la acción
			accion = accion.trim().toLowerCase() ;

			// Si la acción no es poweroff, reboot o true, devolvemos la excepción de servidor
			if (!Constants.ACCION_POWEROFF.equalsIgnoreCase(accion) && !Constants.ACCION_REBOOT.equalsIgnoreCase(accion))
			{
				// Creamos una variable con el mensaje de error
				String mensajeError = "La acción debe ser poweroff o reboot: " + accion ;

				// Logueamos la excepción
				log.error(mensajeError) ;

				// Devolvemos la excepción de servidor
				throw new ImagesClonerServerException(Constants.ERR_ACCION_INVALIDA_CODE, mensajeError) ;
			}

			// Devolvemos la acción normalizada
			outcome = accion ;
		}

		// Devolvemos la acción normalizada
		return outcome ;
	}

	@PreAuthorize("hasAnyRole('" + BaseConstants.ROLE_ADMINISTRADOR + "', '" + BaseConstants.ROLE_DIRECCION + "')")
	@DeleteMapping("/")
	public ResponseEntity<?> habilitarMenuCompletoTodasLasImagenes()
	{
		try
		{
			// Desactivamos todas las imágenes
			this.imagenRepository.desactivarTodasLasImagenes() ;

			// Devolvemos la lista de imágenes
			return ResponseEntity.ok(this.imagenRepository.buscarTodasLasImagenes()) ;
		}
		catch (Exception exception)
		{
			// Creamos la excepción de servidor
			ImagesClonerServerException serverException = 
				new ImagesClonerServerException(BaseConstants.ERR_GENERIC_EXCEPTION_CODE, BaseConstants.ERR_GENERIC_EXCEPTION_MSG + "eliminarImagen", exception) ;
			
			// Logueamos la excepción
			log.error(serverException.getMessage(), exception) ;

			// Devolvemos la excepción de servidor
			return ResponseEntity.status(500).body(serverException.getBodyExceptionMessage()) ;
		}
	}
}
