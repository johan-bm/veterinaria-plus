package com.veterinariaplus.app.servicio;

import com.veterinariaplus.app.modelo.HistorialClinico;

import java.util.List;
import java.util.Optional;

/**
 * Operaciones de negocio disponibles para la gestión de historiales clínicos.
 *
 * Cubre RF-04 (registrar historial por consulta) y CU-06 (ver historial
 * completo de una mascota).
 */
public interface HistorialClinicoService {

    HistorialClinico registrar(Long citaId, String sintomas, String diagnostico, String notas);

    Optional<HistorialClinico> buscarPorId(Long id);

    Optional<HistorialClinico> buscarPorCita(Long citaId);

    /**
     * Historial clínico completo de una mascota, del más reciente al
     * más antiguo.
     */
    List<HistorialClinico> buscarPorMascota(Long mascotaId);
}
