package com.veterinariaplus.app.servicio;

import com.veterinariaplus.app.modelo.Tratamiento;

import java.util.List;
import java.util.Set;

/**
 * Operaciones de negocio disponibles para la gestión de tratamientos.
 *
 * Cubre RF-04 (tratamientos asociados al historial clínico) y
 * CU-07 (prescribir medicamento).
 */
public interface TratamientoService {

    /**
     * Prescribe un nuevo tratamiento dentro de un historial clínico,
     * descontando automáticamente el stock de los medicamentos
     * involucrados.
     */
    Tratamiento prescribir(Long historialClinicoId, String descripcion, Integer duracionDias,
                            String indicaciones, Set<Long> idsMedicamentos);

    List<Tratamiento> buscarPorHistorial(Long historialClinicoId);
}
