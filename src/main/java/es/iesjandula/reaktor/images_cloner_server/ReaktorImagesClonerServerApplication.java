package es.iesjandula.reaktor.images_cloner_server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

/**
 * Servidor de componentes internos (imágenes Clonezilla por defecto).
 */
@SpringBootApplication
@ComponentScan(basePackages = {"es.iesjandula"})
public class ReaktorImagesClonerServerApplication
{
	public static void main(String[] args)
	{
		SpringApplication.run(ReaktorImagesClonerServerApplication.class, args);
	}
}
