package es.iesjandula.reaktor.internal_components_server.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Solicitud para registrar imagen por defecto (pendiente de activación en el host).
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SetImagenDefaultRequest
{
	private String nombreImagen ;
	private String accion ;
}
