package com.veterinariaplus.app.servicio.disponibilidad;

import com.veterinariaplus.app.modelo.Veterinario;
import com.veterinariaplus.app.repositorio.CitaRepository;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 * Estrategia concreta (patrón Strategy) que determina la disponibilidad
 * de un veterinario únicamente verificando que no exista solapamiento
 * de horario con otra cita activa (AGENDADA o CONFIRMADA) en la misma fecha.
 *
 * Esta es la estrategia por defecto de Veterinaria+. Si en el futuro se
 * requiere una validación más compleja (por ejemplo, restringir
 * disponibilidad según especialidad o jornada laboral), basta con crear
 * otra implementación de EstrategiaDisponibilidad e inyectarla en su lugar,
 * sin modificar CitaServiceImpl.
 */
@Component
public class EstrategiaDisponibilidadPorSolapamiento implements EstrategiaDisponibilidad {

    private final CitaRepository citaRepository;

    public EstrategiaDisponibilidadPorSolapamiento(CitaRepository citaRepository) {
        this.citaRepository = citaRepository;
    }

    @Override
    public boolean estaDisponible(Veterinario veterinario, LocalDate fecha,
                                   LocalTime horaInicio, LocalTime horaFin, Long idCitaExcluir) {
        boolean haySolapamiento = citaRepository.existeSolapamiento(
                veterinario.getId(), fecha, horaInicio, horaFin, idCitaExcluir);
        return !haySolapamiento;
    }
}
