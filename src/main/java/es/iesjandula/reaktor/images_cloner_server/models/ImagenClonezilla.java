package es.iesjandula.reaktor.images_cloner_server.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Imagen Clonezilla (carpeta en disco del servidor PXE).
 */
@Entity
@Table(name = "imagen_clonezilla")
@NoArgsConstructor
@AllArgsConstructor
@Data
public class ImagenClonezilla
{
	/**
	 * Nombre de la imagen.
	 */
	@Id
	@Column
	private String nombreImagen ;

	/**
	 * Estado de la imagen.
	 */
	@Column(nullable = false)
	private String estado ;

	/**
	 * Acción a realizar sobre la imagen.
	 */
	@Column(nullable = false)
	private String accion ;
}
