package es.iesjandula.reaktor.images_cloner_server.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.Modifying;

import es.iesjandula.reaktor.images_cloner_server.dto.ImagenClonezillaDto;
import es.iesjandula.reaktor.images_cloner_server.models.ImagenClonezilla;
import es.iesjandula.reaktor.images_cloner_server.utils.Constants;
import jakarta.transaction.Transactional;

/**
 * Repositorio de imágenes Clonezilla.
 */
public interface IImagenClonezillaRepository extends JpaRepository<ImagenClonezilla, String>
{
	/**
	 * Busca todas las imágenes Clonezilla ordenadas por nombre de imagen.
	 * @return Lista de imágenes Clonezilla
	 */
	@Query("""
		SELECT new es.iesjandula.reaktor.images_cloner_server.dto.ImagenClonezillaDto(i.nombreImagen, i.estado)
		FROM ImagenClonezilla i
		ORDER BY i.nombreImagen ASC
	""")
	List<ImagenClonezillaDto> buscarTodasLasImagenes() ;

	/**
	 * Desactiva todas las imágenes Clonezilla.
	 */
	@Modifying
	@Transactional
	@Query("UPDATE ImagenClonezilla i SET i.estado = '" + Constants.ESTADO_DESACTIVADA + "'")
	void desactivarTodasLasImagenes() ;

	/**
	 * Actualiza el estado y la acción de las imágenes Clonezilla.
	 * @param nombreImagen Nombre de la imagen
	 * @param accion Acción a realizar
	 */
	@Modifying
	@Transactional
	@Query("UPDATE ImagenClonezilla i " +
		   "SET i.estado = '" + Constants.ESTADO_PENDIENTE + "', i.accion = :accion " +
		   "WHERE i.nombreImagen = :nombreImagen")
	void ponerImagenAPendiente(String nombreImagen, String accion) ;

	/**
	 * Elimina todas las imágenes Clonezilla que no están en la lista de nombres de imágenes.
	 * @param nombreImagenes Lista de nombres de imágenes
	 */
	@Modifying
	@Transactional
	@Query("DELETE FROM ImagenClonezilla i WHERE i.nombreImagen NOT IN :nombreImagenes")
	void eliminarImagenesNoEnLista(List<String> nombreImagenes) ;

	/**
	 * Pone la imagen a activada.
	 * @param nombreImagen Nombre de la imagen
	 */
	@Modifying
	@Transactional
	@Query("UPDATE ImagenClonezilla i SET i.estado = '" + Constants.ESTADO_ACTIVADA + "' WHERE i.nombreImagen = :nombreImagen")
	void ponerImagenActivada(String nombreImagen) ;
}
