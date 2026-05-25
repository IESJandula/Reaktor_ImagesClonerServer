package es.iesjandula.reaktor.internal_components_server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

/**
 * Servidor de componentes internos (imágenes Clonezilla por defecto).
 */
@SpringBootApplication
@ComponentScan(basePackages = {"es.iesjandula"})
public class ReaktorInternalComponentsServerApplication
{
	public static void main(String[] args)
	{
		SpringApplication.run(ReaktorInternalComponentsServerApplication.class, args);
	}
}
