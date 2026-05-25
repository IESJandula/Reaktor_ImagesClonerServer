package es.iesjandula.reaktor.images_cloner_server.utils;

/**
 * Constantes del servidor Images Cloner (Clonezilla).
 */
public final class Constants
{
    /************************************************/
	/************** Mensajes de error ***************/
	/************************************************/
	
	/** Constante - Error - Lista de nombres de imágenes vacía */
	public static final String ERR_LISTA_NOMBRES_IMAGENES_VACIA = "La lista de nombres de imágenes es obligatoria" ;
	/** Constante - Error - Nombre de imagen vacío */
	public static final String ERR_NOMBRE_IMAGEN_VACIO          = "El nombre de la imagen es obligatorio" ;
	/** Constante - Error - Imagen no pendiente */
	public static final String ERR_IMAGEN_NO_PENDIENTE          = "La imagen no está en estado PENDIENTE" ;
	/** Constante - Error - Imagen no encontrada */
	public static final String ERR_IMAGEN_NO_ENCONTRADA         = "Imagen no encontrada" ;

	/************************************************/
	/************** Acción - Poweroff ***************/
	/************************************************/

	/** Constante - Acción - Poweroff */
	public static final String ACCION_POWEROFF = "poweroff" ;
	/** Constante - Acción - Reboot */
	public static final String ACCION_REBOOT   = "reboot" ;

	/************************************************/
	/************** Estados de la imagen ************/
	/************************************************/

	/** Constante - Estado - Pendiente */
	public static final String ESTADO_PENDIENTE   = "PENDIENTE" ;
	/** Constante - Estado - Activada */
	public static final String ESTADO_ACTIVADA    = "ACTIVADA" ;
	/** Constante - Estado - Desactivada */
	public static final String ESTADO_DESACTIVADA = "DESACTIVADA" ;
}
