package es.iesjandula.reaktor.images_cloner_server.dto;

import lombok.Data;

/**
 * DTO para la configuración del clonador
 */
@Data
public class ConfiguracionClonadorDto
{
    /** Si está a true es que se debe mostrar el menú del clonador */
    private boolean menuActivo ;

    /** Si está a true es que se debe activar la imagen */
    private boolean activarImagen ;

    /** Nombre de la imagen que se debe activar */
    private String nombreImagen ;

    /**
     * Creamos el constructor por defecto
     */
    public ConfiguracionClonadorDto()
    {
        this.menuActivo    = true ;
        this.activarImagen = false ;
        this.nombreImagen  = null ;
    }
}
