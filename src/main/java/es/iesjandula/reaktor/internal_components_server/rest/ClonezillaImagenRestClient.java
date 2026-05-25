package es.iesjandula.reaktor.internal_components_server.rest;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import es.iesjandula.reaktor.base.utils.BaseConstants;
import es.iesjandula.reaktor.internal_components_server.dto.ActivationPollResponse;
import es.iesjandula.reaktor.internal_components_server.dto.SetImagenDefaultRequest;
import es.iesjandula.reaktor.internal_components_server.dto.SyncImagenesRequest;
import es.iesjandula.reaktor.internal_components_server.service.ImagenClonezillaService;
import es.iesjandula.reaktor.internal_components_server.utils.Constants;
import es.iesjandula.reaktor.internal_components_server.utils.InternalComponentsServerException;
import lombok.extern.slf4j.Slf4j;

/**
 * API del cliente PXE/Clonezilla en el servidor de imágenes.
 */
@RestController
@RequestMapping("/clonezilla/client")
@Slf4j
public class ClonezillaImagenRestClient
{
	@Autowired
	private ImagenClonezillaService imagenService ;

	/**
	 * POST /clonezilla/client/images/default
	 * Mismo contrato que el endpoint admin: pendiente la nueva, desactivar la activa previa.
	 */
	@PreAuthorize("hasRole('" + Constants.ROLE_CLIENTE_CLONEZILLA + "')")
	@PostMapping(value = "/images/default", consumes = "application/json")
	public ResponseEntity<?> registrarImagenDefaultCliente(@RequestBody SetImagenDefaultRequest request)
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
					BaseConstants.ERR_GENERIC_EXCEPTION_MSG + "registrarImagenDefaultCliente",
					exception) ;
			log.error(wrapped.getMessage(), wrapped) ;
			return ResponseEntity.status(500).body(wrapped.getBodyExceptionMessage()) ;
		}
	}

	/**
	 * POST /clonezilla/client/images/sync
	 * Body: { "nombresImagen": ["img-a", "img-b"] }
	 */
	@PreAuthorize("hasRole('" + Constants.ROLE_CLIENTE_CLONEZILLA + "')")
	@PostMapping(value = "/images/sync", consumes = "application/json")
	public ResponseEntity<?> sincronizarImagenes(@RequestBody SyncImagenesRequest request)
	{
		try
		{
			this.imagenService.sincronizarImagenesLocales(request.getNombresImagen()) ;
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
					BaseConstants.ERR_GENERIC_EXCEPTION_MSG + "sincronizarImagenes",
					exception) ;
			log.error(wrapped.getMessage(), wrapped) ;
			return ResponseEntity.status(500).body(wrapped.getBodyExceptionMessage()) ;
		}
	}

	/**
	 * GET /clonezilla/client/images/activation/poll
	 * Si hay fila PENDIENTE: 200 + { nombreImagen, accion } y transición atómica PENDIENTE→ACTIVADA.
	 * Si no hay pendiente: 204 No Content.
	 */
	@PreAuthorize("hasRole('" + Constants.ROLE_CLIENTE_CLONEZILLA + "')")
	@GetMapping("/images/activation/poll")
	public ResponseEntity<ActivationPollResponse> pollActivacion()
	{
		try
		{
			Optional<ActivationPollResponse> outcome = this.imagenService.pollActivacion() ;
			if (outcome.isPresent())
			{
				return ResponseEntity.ok(outcome.get()) ;
			}
			return ResponseEntity.noContent().build() ;
		}
		catch (Exception exception)
		{
			InternalComponentsServerException wrapped = new InternalComponentsServerException(
					BaseConstants.ERR_GENERIC_EXCEPTION_CODE,
					BaseConstants.ERR_GENERIC_EXCEPTION_MSG + "pollActivacion",
					exception) ;
			log.error(wrapped.getMessage(), wrapped) ;
			return ResponseEntity.status(500).build() ;
		}
	}
}
