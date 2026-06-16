package com.veterinariaplus.app.servicio;

import com.veterinariaplus.app.dto.CitaDTO;
import com.veterinariaplus.app.modelo.Cita;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Operaciones de negocio disponibles para la gestión de citas veterinarias.
 *
 * Cubre RF-03 (agendar validando disponibilidad), y los casos de uso
 * CU-02 (agendar), CU-03 (cancelar) y el ciclo de vida de la cita.
 */
public interface CitaService {

    /**
     * Agenda una nueva cita validando que el veterinario esté disponible
     * en el horario solicitado.
     *
     * @throws com.veterinariaplus.app.excepcion.ReglaDeNegocioException
     *         si el horario solicitado se solapa con otra cita del veterinario
     */
    Cita agendar(CitaDTO dto);

    /**
     * Actualiza los datos de una cita existente, validando nuevamente
     * la disponibilidad de horario (excluyendo la propia cita).
     */
    Cita actualizar(CitaDTO dto);

    Optional<Cita> buscarPorId(Long id);

    List<Cita> buscarTodas();

    List<Cita> buscarPorRangoDeFechas(LocalDate fechaInicio, LocalDate fechaFin);

    Cita confirmar(Long id);

    Cita cancelar(Long id, String motivoCancelacion);

    Cita completar(Long id);
}
