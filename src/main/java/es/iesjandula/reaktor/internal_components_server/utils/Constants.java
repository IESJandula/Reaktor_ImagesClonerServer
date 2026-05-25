package es.iesjandula.reaktor.internal_components_server.utils;

import java.util.Arrays;
import java.util.List;

/**
 * Constantes del servidor de imágenes Clonezilla.
 */
public class Constants
{
	/** Acciones post-restore permitidas */
	public static final String ACCION_POWEROFF = "poweroff" ;
	public static final String ACCION_REBOOT   = "reboot" ;
	public static final String ACCION_TRUE     = "true" ;

	public static final List<String> ACCIONES_VALIDAS = Arrays.asList(
			ACCION_POWEROFF, ACCION_REBOOT, ACCION_TRUE) ;

	/** Rol JWT del cliente PXE/Clonezilla (debe existir en el ecosistema Reaktor) */
	public static final String ROLE_CLIENTE_CLONEZILLA = "CLIENTE_CLONEZILLA" ;

	public static final int ERR_ACCION_INVALIDA           = 201 ;
	public static final int ERR_NOMBRE_IMAGEN_REQUERIDO   = 202 ;
	public static final int ERR_SIN_IMAGEN_PENDIENTE      = 203 ;
}
