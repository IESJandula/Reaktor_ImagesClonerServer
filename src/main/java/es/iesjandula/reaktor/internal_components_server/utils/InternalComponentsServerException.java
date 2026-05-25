package es.iesjandula.reaktor.internal_components_server.utils;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.exception.ExceptionUtils;

/**
 * Excepción de negocio del servidor de componentes internos.
 */
public class InternalComponentsServerException extends Exception
{
	private static final long serialVersionUID = 1L ;

	private int code ;
	private String message ;
	private Exception exception ;

	public InternalComponentsServerException(int code, String message)
	{
		super(message) ;
		this.code    = code ;
		this.message = message ;
	}

	public InternalComponentsServerException(int code, String message, Exception exception)
	{
		super(message, exception) ;
		this.code      = code ;
		this.message   = message ;
		this.exception = exception ;
	}

	public Map<String, String> getBodyExceptionMessage()
	{
		Map<String, String> messageMap = new HashMap<>() ;
		messageMap.put("code", String.valueOf(this.code)) ;
		messageMap.put("message", this.message) ;
		if (this.exception != null)
		{
			messageMap.put("exception", ExceptionUtils.getStackTrace(this.exception)) ;
		}
		return messageMap ;
	}
}
