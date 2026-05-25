package es.iesjandula.reaktor.images_cloner_server.utils;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.exception.ExceptionUtils;

/**
 * Excepción de negocio del servidor Images Cloner.
 */
public class ImagesClonerServerException extends Exception
{
	/**
	 * Serial version UID.
	 */
	private static final long serialVersionUID = 1L ;

	/**
	 * Código de la excepción.
	 */
	private final Integer codigo ;

	/**
	 * Constructor de la excepción.
	 * @param codigo Código de la excepción.
	 * @param mensaje Mensaje de la excepción.
	 */
	public ImagesClonerServerException(Integer codigo, String mensaje)
	{
		super(mensaje) ;
		this.codigo = codigo ;
	}

	/**
	 * Constructor de la excepción.
	 * @param codigo Código de la excepción.
	 * @param mensaje Mensaje de la excepción.
	 * @param excepcion Excepción.
	 */
	public ImagesClonerServerException(Integer codigo, String mensaje, Exception excepcion)
	{
		super(mensaje, excepcion) ;
		this.codigo = codigo ;
	}

	/**
	 * Obtiene el código de la excepción.
	 * @return Código de la excepción.
	 */
	public Integer getCodigo()
	{
		return this.codigo ;
	}

	/**
	 * Obtiene el cuerpo de la excepción.
	 * @return Cuerpo de la excepción.
	 */
	public Map<String, Object> getBodyExceptionMessage()
	{
		// Creamos el cuerpo de la excepción
		Map<String, Object> body = new HashMap<>() ;

		// Añadimos el código de la excepción
		body.put("codigo", this.codigo) ;

		// Añadimos el mensaje de la excepción
		body.put("mensaje", this.getMessage()) ;

		// Si hay una causa, añadimos el detalle de la excepción
		if (this.getCause() != null)
		{
			// Añadimos el detalle de la excepción
			body.put("excepcion", ExceptionUtils.getRootCauseMessage(this)) ;
		}

		// Devolvemos el cuerpo de la excepción
		return body ;
	}
}
