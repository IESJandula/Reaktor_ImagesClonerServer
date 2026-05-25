package es.iesjandula.reaktor.internal_components_server.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Respuesta del poll de activación: imagen a restaurar y acción post-restore.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ActivationPollResponse
{
	private String nombreImagen ;
	private String accion ;
}
