package es.iesjandula.reaktor.images_cloner_server.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO de imagen Clonezilla para listados REST.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ImagenClonezillaDto
{
	/** Nombre de la imagen */
	private String nombreImagen ;

	/** Estado de la imagen */
	private String estado ;

	/** Acción a realizar */
	private String accion ;
}
