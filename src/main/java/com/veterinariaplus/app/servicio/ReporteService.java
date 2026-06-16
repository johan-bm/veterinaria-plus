package com.veterinariaplus.app.servicio;

import com.veterinariaplus.app.dto.CitaResumenDTO;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * Operaciones de generación de reportes (RF-09).
 */
public interface ReporteService {

    /**
     * Reporte de citas dentro de un rango de fechas, con los datos
     * ya resueltos para presentación (CitaResumenDTO).
     */
    List<CitaResumenDTO> citasPorPeriodo(LocalDate fechaInicio, LocalDate fechaFin);

    /**
     * Conteo de citas agrupadas por estado dentro de un rango de fechas,
     * útil para mostrar estadísticas rápidas (ej. en un gráfico).
     */
    Map<String, Long> estadisticasPorEstado(LocalDate fechaInicio, LocalDate fechaFin);
}
