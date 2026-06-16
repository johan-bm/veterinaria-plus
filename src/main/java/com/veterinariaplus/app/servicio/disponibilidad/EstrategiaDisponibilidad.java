package com.veterinariaplus.app.servicio.disponibilidad;

import com.veterinariaplus.app.modelo.Veterinario;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 * Define el contrato del patrón de diseño Strategy para calcular si un
 * veterinario está disponible en un horario determinado.
 *
 * Permite intercambiar el algoritmo de verificación de disponibilidad
 * sin modificar el código que lo invoca (CitaServiceImpl). Por ejemplo,
 * una clínica podría usar una estrategia simple (solo verificar
 * solapamiento de citas) o una más avanzada que considere horarios
 * de atención por especialidad, días libres, etc.
 */
public interface EstrategiaDisponibilidad {

    /**
     * Verifica si el veterinario indicado está disponible en el
     * rango de horario propuesto.
     *
     * @param veterinario   veterinario a validar
     * @param fecha         fecha de la cita propuesta
     * @param horaInicio    hora de inicio propuesta
     * @param horaFin       hora de fin propuesta
     * @param idCitaExcluir id de cita a excluir de la validación (para ediciones), o null
     * @return true si el veterinario está disponible, false si hay conflicto
     */
    boolean estaDisponible(Veterinario veterinario, LocalDate fecha,
                            LocalTime horaInicio, LocalTime horaFin, Long idCitaExcluir);
}
