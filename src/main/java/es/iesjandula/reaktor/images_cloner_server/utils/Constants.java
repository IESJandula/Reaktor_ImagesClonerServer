package es.iesjandula.reaktor.images_cloner_server.utils;

/**
 * Constantes del servidor Images Cloner (Clonezilla).
 */
public final class Constants
{
    /************************************************/
	/************** Mensajes de error ***************/
	/************************************************/
	
	/** Constante - Error - Lista de nombres de imágenes vacía - Código */
	public static final int ERR_LISTA_NOMBRES_IMAGENES_VACIA_CODE    = 9401 ;
	/** Constante - Error - Lista de nombres de imágenes vacía - Descripción */
	public static final String ERR_LISTA_NOMBRES_IMAGENES_VACIA_DESC = "La lista de nombres de imágenes es obligatoria" ;

	/** Constante - Error - Nombre de imagen vacío - Código */
	public static final int ERR_NOMBRE_IMAGEN_VACIO_CODE             = 9402 ;
	/** Constante - Error - Nombre de imagen vacío - Descripción */
	public static final String ERR_NOMBRE_IMAGEN_VACIO_DESC          = "El nombre de la imagen es obligatorio" ;

	/** Constante - Error - Acción inválida - Código */
	public static final int ERR_ACCION_INVALIDA_CODE                 = 9403 ;
	/** Constante - Error - Acción inválida - Descripción */
	public static final String ERR_ACCION_INVALIDA_DESC              = "La acción debe ser poweroff o reboot" ;

	/** Constante - Error - Imagen no encontrada - Código */
	public static final int ERR_IMAGEN_NO_ENCONTRADA_CODE             = 9404 ;
	/** Constante - Error - Imagen no encontrada - Descripción */
	public static final String ERR_IMAGEN_NO_ENCONTRADA_DESC         = "Imagen no encontrada" ;

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
