package es.iesjandula.reaktor.internal_components_server.rest;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import es.iesjandula.reaktor.base.utils.BaseConstants;
import es.iesjandula.reaktor.internal_components_server.dto.ImagenClonezillaDto;
import es.iesjandula.reaktor.internal_components_server.dto.SetImagenDefaultRequest;
import es.iesjandula.reaktor.internal_components_server.service.ImagenClonezillaService;
import es.iesjandula.reaktor.internal_components_server.utils.InternalComponentsServerException;
import lombok.extern.slf4j.Slf4j;

/**
 * API web/administración: registro de imagen por defecto y listado.
 */
@RestController
@RequestMapping("/clonezilla/images")
@Slf4j
public class ClonezillaImagenRestAdmin
{
	@Autowired
	private ImagenClonezillaService imagenService ;

	/**
	 * POST /clonezilla/images/default
	 * Body JSON: { "nombreImagen": "win10-aula1", "accion": "reboot" }
	 * Desactiva la ACTIVADA previa; la imagen solicitada queda PENDIENTE con la acción indicada.
	 */
	@PreAuthorize("hasAnyRole('" + BaseConstants.ROLE_ADMINISTRADOR + "', '" + BaseConstants.ROLE_DIRECCION + "')")
	@PostMapping(value = "/default", consumes = "application/json")
	public ResponseEntity<?> registrarImagenDefault(@RequestBody SetImagenDefaultRequest request)
	{
		try
		{
			this.imagenService.registrarImagenDefault(request.getNombreImagen(), request.getAccion()) ;
			return ResponseEntity.ok().build() ;
		}
		catch (InternalComponentsServerException businessException)
		{
			log.warn(businessException.getMessage()) ;
			return ResponseEntity.badRequest().body(businessException.getBodyExceptionMessage()) ;
		}
		catch (Exception exception)
		{
			InternalComponentsServerException wrapped = new InternalComponentsServerException(
					BaseConstants.ERR_GENERIC_EXCEPTION_CODE,
					BaseConstants.ERR_GENERIC_EXCEPTION_MSG + "registrarImagenDefault",
					exception) ;
			log.error(wrapped.getMessage(), wrapped) ;
			return ResponseEntity.status(500).body(wrapped.getBodyExceptionMessage()) ;
		}
	}

	/**
	 * GET /clonezilla/images — listado completo nombre, estado, acción.
	 */
	@PreAuthorize("hasAnyRole('" + BaseConstants.ROLE_ADMINISTRADOR + "', '" + BaseConstants.ROLE_DIRECCION + "')")
	@GetMapping
	public ResponseEntity<List<ImagenClonezillaDto>> listarImagenes()
	{
		return ResponseEntity.ok(this.imagenService.listarTodas()) ;
	}
}
