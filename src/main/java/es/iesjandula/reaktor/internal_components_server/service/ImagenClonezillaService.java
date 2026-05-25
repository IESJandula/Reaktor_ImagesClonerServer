package es.iesjandula.reaktor.internal_components_server.service;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import es.iesjandula.reaktor.internal_components_server.dto.ActivationPollResponse;
import es.iesjandula.reaktor.internal_components_server.dto.ImagenClonezillaDto;
import es.iesjandula.reaktor.internal_components_server.models.EstadoImagen;
import es.iesjandula.reaktor.internal_components_server.models.ImagenClonezilla;
import es.iesjandula.reaktor.internal_components_server.repository.IImagenClonezillaRepository;
import es.iesjandula.reaktor.internal_components_server.utils.Constants;
import es.iesjandula.reaktor.internal_components_server.utils.InternalComponentsServerException;

/**
 * Lógica de negocio de imágenes Clonezilla (una sola ACTIVADA a la vez).
 */
@Service
public class ImagenClonezillaService
{
	@Autowired
	private IImagenClonezillaRepository imagenRepository ;

	public List<ImagenClonezillaDto> listarTodas()
	{
		return this.imagenRepository.findAll().stream()
				.map(ImagenClonezillaDto::fromEntity)
				.collect(Collectors.toList()) ;
	}

	/**
	 * Registra la imagen por defecto solicitada: desactiva la ACTIVADA previa y deja la nueva en PENDIENTE.
	 */
	@Transactional
	public void registrarImagenDefault(String nombreImagen, String accion) throws InternalComponentsServerException
	{
		this.validarNombreYAccion(nombreImagen, accion) ;

		this.desactivarTodasActivadas() ;

		ImagenClonezilla imagen = this.imagenRepository.findById(nombreImagen)
				.orElse(new ImagenClonezilla(nombreImagen, EstadoImagen.DESACTIVADA, accion)) ;

		imagen.setEstado(EstadoImagen.PENDIENTE) ;
		imagen.setAccion(accion) ;
		this.imagenRepository.saveAndFlush(imagen) ;
	}

	/**
	 * Sincroniza carpetas locales del cliente: crea DESACTIVADA las nuevas; no altera ACTIVADA ni PENDIENTE.
	 * Las filas que ya no están en disco y no son ACTIVADA/PENDIENTE pasan a DESACTIVADA.
	 */
	@Transactional
	public void sincronizarImagenesLocales(List<String> nombresEnDisco) throws InternalComponentsServerException
	{
		if (nombresEnDisco == null)
		{
			throw new InternalComponentsServerException(Constants.ERR_NOMBRE_IMAGEN_REQUERIDO,
					"La lista nombresImagen es obligatoria") ;
		}

		Set<String> nombresSet = nombresEnDisco.stream()
				.filter(StringUtils::hasText)
				.map(String::trim)
				.collect(Collectors.toSet()) ;

		for (String nombre : nombresSet)
		{
			Optional<ImagenClonezilla> existente = this.imagenRepository.findById(nombre) ;
			if (existente.isEmpty())
			{
				this.imagenRepository.saveAndFlush(
						new ImagenClonezilla(nombre, EstadoImagen.DESACTIVADA, Constants.ACCION_POWEROFF)) ;
			}
		}

		for (ImagenClonezilla imagen : this.imagenRepository.findAll())
		{
			if (nombresSet.contains(imagen.getNombreImagen()))
			{
				continue ;
			}
			if (imagen.getEstado() == EstadoImagen.ACTIVADA || imagen.getEstado() == EstadoImagen.PENDIENTE)
			{
				continue ;
			}
			imagen.setEstado(EstadoImagen.DESACTIVADA) ;
			this.imagenRepository.saveAndFlush(imagen) ;
		}
	}

	/**
	 * Poll atómico: si hay PENDIENTE, devuelve nombre+acción y marca esa fila ACTIVADA (desactivando la ACTIVADA previa).
	 */
	@Transactional
	public Optional<ActivationPollResponse> pollActivacion()
	{
		List<ImagenClonezilla> pendientes = this.imagenRepository.findByEstadoForUpdate(EstadoImagen.PENDIENTE) ;
		if (pendientes.isEmpty())
		{
			return Optional.empty() ;
		}

		ImagenClonezilla siguiente = pendientes.get(0) ;

		this.desactivarTodasActivadas() ;

		siguiente.setEstado(EstadoImagen.ACTIVADA) ;
		this.imagenRepository.saveAndFlush(siguiente) ;

		return Optional.of(new ActivationPollResponse(siguiente.getNombreImagen(), siguiente.getAccion())) ;
	}

	private void desactivarTodasActivadas()
	{
		for (ImagenClonezilla activa : this.imagenRepository.findByEstadoForUpdate(EstadoImagen.ACTIVADA))
		{
			activa.setEstado(EstadoImagen.DESACTIVADA) ;
			this.imagenRepository.saveAndFlush(activa) ;
		}
	}

	private void validarNombreYAccion(String nombreImagen, String accion) throws InternalComponentsServerException
	{
		if (!StringUtils.hasText(nombreImagen))
		{
			throw new InternalComponentsServerException(Constants.ERR_NOMBRE_IMAGEN_REQUERIDO,
					"nombreImagen es obligatorio") ;
		}
		if (!StringUtils.hasText(accion) || !Constants.ACCIONES_VALIDAS.contains(accion.trim()))
		{
			throw new InternalComponentsServerException(Constants.ERR_ACCION_INVALIDA,
					"accion debe ser: poweroff, reboot o true") ;
		}
	}
}
