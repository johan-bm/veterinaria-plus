package com.veterinariaplus.app.servicio.impl;

import com.veterinariaplus.app.dto.CitaDTO;
import com.veterinariaplus.app.evento.CambioEstadoCitaEvent;
import com.veterinariaplus.app.excepcion.RecursoNoEncontradoException;
import com.veterinariaplus.app.excepcion.ReglaDeNegocioException;
import com.veterinariaplus.app.modelo.Cita;
import com.veterinariaplus.app.modelo.Mascota;
import com.veterinariaplus.app.modelo.Veterinario;
import com.veterinariaplus.app.repositorio.CitaRepository;
import com.veterinariaplus.app.repositorio.MascotaRepository;
import com.veterinariaplus.app.repositorio.VeterinarioRepository;
import com.veterinariaplus.app.servicio.CitaService;
import com.veterinariaplus.app.servicio.disponibilidad.EstrategiaDisponibilidad;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Implementación del servicio de gestión de citas veterinarias.
 *
 * Concentra la lógica de negocio mas importante del sistema:
 * - Validación de disponibilidad de horario (vía EstrategiaDisponibilidad, patrón Strategy)
 * - Control del ciclo de vida de la cita (AGENDADA -> CONFIRMADA/CANCELADA -> COMPLETADA)
 * - Publicación de eventos de cambio de estado (patrón Observer, vía Spring Events)
 */
@Service
public class CitaServiceImpl implements CitaService {

    private final CitaRepository citaRepository;
    private final MascotaRepository mascotaRepository;
    private final VeterinarioRepository veterinarioRepository;
    private final EstrategiaDisponibilidad estrategiaDisponibilidad;
    private final ApplicationEventPublisher eventPublisher;

    public CitaServiceImpl(CitaRepository citaRepository,
                            MascotaRepository mascotaRepository,
                            VeterinarioRepository veterinarioRepository,
                            EstrategiaDisponibilidad estrategiaDisponibilidad,
                            ApplicationEventPublisher eventPublisher) {
        this.citaRepository = citaRepository;
        this.mascotaRepository = mascotaRepository;
        this.veterinarioRepository = veterinarioRepository;
        this.estrategiaDisponibilidad = estrategiaDisponibilidad;
        this.eventPublisher = eventPublisher;
    }

    @Override
    @Transactional
    public Cita agendar(CitaDTO dto) {
        Mascota mascota = obtenerMascota(dto.getMascotaId());
        Veterinario veterinario = obtenerVeterinario(dto.getVeterinarioId());

        validarDisponibilidad(veterinario, dto, null);
        validarRangoHorario(dto);

        Cita cita = new Cita(mascota, veterinario, dto.getFechaCita(),
                dto.getHoraInicio(), dto.getHoraFin(), dto.getMotivo());
        cita.setEstado(Cita.EstadoCita.AGENDADA);

        Cita citaGuardada = citaRepository.save(cita);

        eventPublisher.publishEvent(
                new CambioEstadoCitaEvent(citaGuardada, null, Cita.EstadoCita.AGENDADA));

        return citaGuardada;
    }

    @Override
    @Transactional
    public Cita actualizar(CitaDTO dto) {
        if (dto.getId() == null) {
            throw new IllegalArgumentException("El id de la cita es obligatorio para actualizar");
        }
        Cita cita = citaRepository.findById(dto.getId())
                .orElseThrow(() -> new RecursoNoEncontradoException("Cita", dto.getId()));

        Mascota mascota = obtenerMascota(dto.getMascotaId());
        Veterinario veterinario = obtenerVeterinario(dto.getVeterinarioId());

        validarDisponibilidad(veterinario, dto, dto.getId());
        validarRangoHorario(dto);

        cita.setMascota(mascota);
        cita.setVeterinario(veterinario);
        cita.setFechaCita(dto.getFechaCita());
        cita.setHoraInicio(dto.getHoraInicio());
        cita.setHoraFin(dto.getHoraFin());
        cita.setMotivo(dto.getMotivo());

        return citaRepository.save(cita);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Cita> buscarPorId(Long id) {
        return citaRepository.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Cita> buscarTodas() {
        return citaRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Cita> buscarPorRangoDeFechas(LocalDate fechaInicio, LocalDate fechaFin) {
        return citaRepository.findByFechaCitaBetween(fechaInicio, fechaFin);
    }

    @Override
    @Transactional
    public Cita confirmar(Long id) {
        return cambiarEstado(id, Cita.EstadoCita.CONFIRMADA);
    }

    @Override
    @Transactional
    public Cita cancelar(Long id, String motivoCancelacion) {
        Cita cita = citaRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Cita", id));

        if (cita.getEstado() == Cita.EstadoCita.COMPLETADA) {
            throw new ReglaDeNegocioException("No es posible cancelar una cita ya completada");
        }

        Cita.EstadoCita estadoAnterior = cita.getEstado();
        cita.setEstado(Cita.EstadoCita.CANCELADA);
        if (motivoCancelacion != null && !motivoCancelacion.isBlank()) {
            cita.setMotivo(cita.getMotivo() == null
                    ? "Cancelada: " + motivoCancelacion
                    : cita.getMotivo() + " | Cancelada: " + motivoCancelacion);
        }

        Cita citaActualizada = citaRepository.save(cita);
        eventPublisher.publishEvent(
                new CambioEstadoCitaEvent(citaActualizada, estadoAnterior, Cita.EstadoCita.CANCELADA));
        return citaActualizada;
    }

    @Override
    @Transactional
    public Cita completar(Long id) {
        return cambiarEstado(id, Cita.EstadoCita.COMPLETADA);
    }

    // ---------------------------------------------------------------
    // Métodos privados de soporte
    // ---------------------------------------------------------------

    private Cita cambiarEstado(Long id, Cita.EstadoCita nuevoEstado) {
        Cita cita = citaRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Cita", id));

        if (cita.getEstado() == Cita.EstadoCita.CANCELADA) {
            throw new ReglaDeNegocioException("No es posible modificar una cita cancelada");
        }

        Cita.EstadoCita estadoAnterior = cita.getEstado();
        cita.setEstado(nuevoEstado);
        Cita citaActualizada = citaRepository.save(cita);

        eventPublisher.publishEvent(
                new CambioEstadoCitaEvent(citaActualizada, estadoAnterior, nuevoEstado));

        return citaActualizada;
    }

    private void validarDisponibilidad(Veterinario veterinario, CitaDTO dto, Long idCitaExcluir) {
        boolean disponible = estrategiaDisponibilidad.estaDisponible(
                veterinario, dto.getFechaCita(), dto.getHoraInicio(), dto.getHoraFin(), idCitaExcluir);
        if (!disponible) {
            throw new ReglaDeNegocioException(
                    "El veterinario ya tiene una cita agendada en ese horario");
        }
    }

    private void validarRangoHorario(CitaDTO dto) {
        if (!dto.getHoraInicio().isBefore(dto.getHoraFin())) {
            throw new ReglaDeNegocioException(
                    "La hora de inicio debe ser anterior a la hora de fin");
        }
    }

    private Mascota obtenerMascota(Long id) {
        return mascotaRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Mascota", id));
    }

    private Veterinario obtenerVeterinario(Long id) {
        return veterinarioRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Veterinario", id));
    }
}
