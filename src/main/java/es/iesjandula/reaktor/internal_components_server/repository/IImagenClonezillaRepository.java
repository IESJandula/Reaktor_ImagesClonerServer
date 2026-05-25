package es.iesjandula.reaktor.internal_components_server.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;

import es.iesjandula.reaktor.internal_components_server.models.EstadoImagen;
import es.iesjandula.reaktor.internal_components_server.models.ImagenClonezilla;
import jakarta.persistence.LockModeType;

/**
 * Repositorio de imágenes Clonezilla.
 */
public interface IImagenClonezillaRepository extends JpaRepository<ImagenClonezilla, String>
{
	List<ImagenClonezilla> findByEstado(EstadoImagen estado) ;

	@Lock(LockModeType.PESSIMISTIC_WRITE)
	@Query("SELECT i FROM ImagenClonezilla i WHERE i.estado = :estado ORDER BY i.nombreImagen ASC")
	List<ImagenClonezilla> findByEstadoForUpdate(EstadoImagen estado) ;

	Optional<ImagenClonezilla> findFirstByEstadoOrderByNombreImagenAsc(EstadoImagen estado) ;
}
