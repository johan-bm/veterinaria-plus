package com.veterinariaplus.app.servicio.impl;

import com.veterinariaplus.app.dto.CitaResumenDTO;
import com.veterinariaplus.app.modelo.Cita;
import com.veterinariaplus.app.repositorio.CitaRepository;
import com.veterinariaplus.app.servicio.ReporteService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Implementación del servicio de generación de reportes.
 */
@Service
public class ReporteServiceImpl implements ReporteService {

    private final CitaRepository citaRepository;

    public ReporteServiceImpl(CitaRepository citaRepository) {
        this.citaRepository = citaRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<CitaResumenDTO> citasPorPeriodo(LocalDate fechaInicio, LocalDate fechaFin) {
        return citaRepository.findByFechaCitaBetween(fechaInicio, fechaFin)
                .stream()
                .map(CitaResumenDTO::new)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Map<String, Long> estadisticasPorEstado(LocalDate fechaInicio, LocalDate fechaFin) {
        List<Cita> citas = citaRepository.findByFechaCitaBetween(fechaInicio, fechaFin);
        return citas.stream()
                .collect(Collectors.groupingBy(
                        cita -> cita.getEstado().name(),
                        Collectors.counting()));
    }
}
