package es.iesjandula.reaktor.internal_components_server.models;

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
	@Id
	@Column(name = "nombre_imagen")
	private String nombreImagen ;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private EstadoImagen estado ;

	@Column(nullable = false)
	private String accion ;
}
