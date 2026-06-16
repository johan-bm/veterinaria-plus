package com.veterinariaplus.app.servicio.impl;

import com.veterinariaplus.app.excepcion.RecursoNoEncontradoException;
import com.veterinariaplus.app.modelo.HistorialClinico;
import com.veterinariaplus.app.modelo.Medicamento;
import com.veterinariaplus.app.modelo.Tratamiento;
import com.veterinariaplus.app.repositorio.HistorialClinicoRepository;
import com.veterinariaplus.app.repositorio.MedicamentoRepository;
import com.veterinariaplus.app.repositorio.TratamientoRepository;
import com.veterinariaplus.app.servicio.MedicamentoService;
import com.veterinariaplus.app.servicio.TratamientoService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

/**
 * Implementación del servicio de gestión de tratamientos.
 *
 * Utiliza el patrón Builder de la entidad Tratamiento para construir
 * el objeto de forma legible, y descuenta automáticamente el stock
 * de cada medicamento prescrito a través de MedicamentoService.
 */
@Service
public class TratamientoServiceImpl implements TratamientoService {

    private final TratamientoRepository tratamientoRepository;
    private final HistorialClinicoRepository historialClinicoRepository;
    private final MedicamentoRepository medicamentoRepository;
    private final MedicamentoService medicamentoService;

    public TratamientoServiceImpl(TratamientoRepository tratamientoRepository,
                                   HistorialClinicoRepository historialClinicoRepository,
                                   MedicamentoRepository medicamentoRepository,
                                   MedicamentoService medicamentoService) {
        this.tratamientoRepository = tratamientoRepository;
        this.historialClinicoRepository = historialClinicoRepository;
        this.medicamentoRepository = medicamentoRepository;
        this.medicamentoService = medicamentoService;
    }

    @Override
    @Transactional
    public Tratamiento prescribir(Long historialClinicoId, String descripcion, Integer duracionDias,
                                   String indicaciones, Set<Long> idsMedicamentos) {
        HistorialClinico historial = historialClinicoRepository.findById(historialClinicoId)
                .orElseThrow(() -> new RecursoNoEncontradoException("HistorialClinico", historialClinicoId));

        Tratamiento.Builder builder = new Tratamiento.Builder()
                .historialClinico(historial)
                .descripcion(descripcion)
                .duracionDias(duracionDias)
                .indicaciones(indicaciones);

        if (idsMedicamentos != null) {
            for (Long idMedicamento : idsMedicamentos) {
                Medicamento medicamento = medicamentoRepository.findById(idMedicamento)
                        .orElseThrow(() -> new RecursoNoEncontradoException("Medicamento", idMedicamento));
                builder.agregarMedicamento(medicamento);
                // Cada medicamento prescrito descuenta una unidad del stock.
                medicamentoService.descontarStock(idMedicamento, 1);
            }
        }

        Tratamiento tratamiento = builder.build();
        return tratamientoRepository.save(tratamiento);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Tratamiento> buscarPorHistorial(Long historialClinicoId) {
        return tratamientoRepository.findByHistorialClinicoId(historialClinicoId);
    }
}
