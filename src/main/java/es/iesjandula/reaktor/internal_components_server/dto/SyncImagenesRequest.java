package es.iesjandula.reaktor.internal_components_server.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Lista de nombres de carpetas-imagen detectadas en disco por el cliente PXE.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SyncImagenesRequest
{
	private List<String> nombresImagen ;
}
