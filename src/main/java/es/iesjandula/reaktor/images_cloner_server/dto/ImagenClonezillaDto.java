package es.iesjandula.reaktor.images_cloner_server.dto;

import es.iesjandula.reaktor.images_cloner_server.models.EstadoImagen;
import es.iesjandula.reaktor.images_cloner_server.models.ImagenClonezilla;
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
	private String nombreImagen ;
	private String estado ;
	private String accion ;

	public static ImagenClonezillaDto fromEntity(ImagenClonezilla entity)
	{
		return new ImagenClonezillaDto(
				entity.getNombreImagen(),
				entity.getEstado().name(),
				entity.getAccion()) ;
	}
}
