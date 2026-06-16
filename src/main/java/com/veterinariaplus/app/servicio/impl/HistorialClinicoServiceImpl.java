package com.veterinariaplus.app.servicio.impl;

import com.veterinariaplus.app.excepcion.RecursoNoEncontradoException;
import com.veterinariaplus.app.excepcion.ReglaDeNegocioException;
import com.veterinariaplus.app.modelo.Cita;
import com.veterinariaplus.app.modelo.HistorialClinico;
import com.veterinariaplus.app.repositorio.CitaRepository;
import com.veterinariaplus.app.repositorio.HistorialClinicoRepository;
import com.veterinariaplus.app.servicio.HistorialClinicoService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Implementación del servicio de gestión de historiales clínicos.
 */
@Service
public class HistorialClinicoServiceImpl implements HistorialClinicoService {

    private final HistorialClinicoRepository historialClinicoRepository;
    private final CitaRepository citaRepository;

    public HistorialClinicoServiceImpl(HistorialClinicoRepository historialClinicoRepository,
                                        CitaRepository citaRepository) {
        this.historialClinicoRepository = historialClinicoRepository;
        this.citaRepository = citaRepository;
    }

    @Override
    @Transactional
    public HistorialClinico registrar(Long citaId, String sintomas, String diagnostico, String notas) {
        Cita cita = citaRepository.findById(citaId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Cita", citaId));

        if (historialClinicoRepository.findByCitaId(citaId).isPresent()) {
            throw new ReglaDeNegocioException(
                    "La cita #" + citaId + " ya tiene un historial clínico registrado");
        }

        HistorialClinico historial = new HistorialClinico(cita, sintomas, diagnostico, notas);
        HistorialClinico historialGuardado = historialClinicoRepository.save(historial);

        // Al registrar el historial, la cita se considera completada.
        cita.setEstado(Cita.EstadoCita.COMPLETADA);
        citaRepository.save(cita);

        return historialGuardado;
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<HistorialClinico> buscarPorId(Long id) {
        return historialClinicoRepository.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<HistorialClinico> buscarPorCita(Long citaId) {
        return historialClinicoRepository.findByCitaId(citaId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<HistorialClinico> buscarPorMascota(Long mascotaId) {
        return historialClinicoRepository.findHistorialPorMascota(mascotaId);
    }
}
