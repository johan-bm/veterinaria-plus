package com.veterinariaplus.app.repositorio;

import com.veterinariaplus.app.modelo.Cita;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

/**
 * Repositorio para la gestión de citas veterinarias.
 *
 * Incluye la consulta clave para RF-03 (agendar citas validando
 * disponibilidad de horario): existeSolapamiento detecta si un
 * veterinario ya tiene una cita activa que se cruza con el horario
 * propuesto.
 */
@Repository
public interface CitaRepository extends JpaRepository<Cita, Long> {

    List<Cita> findByMascotaId(Long mascotaId);

    List<Cita> findByVeterinarioId(Long veterinarioId);

    List<Cita> findByFechaCitaBetween(LocalDate fechaInicio, LocalDate fechaFin);

    List<Cita> findByVeterinarioIdAndFechaCita(Long veterinarioId, LocalDate fechaCita);

    /**
     * Determina si el veterinario indicado ya tiene una cita (en estado
     * AGENDADA o CONFIRMADA) cuyo horario se solapa con el rango propuesto,
     * en la fecha indicada.
     *
     * Dos rangos horarios [inicioA, finA) y [inicioB, finB) se solapan
     * si: inicioA < finB AND inicioB < finA.
     *
     * @param idCitaExcluir permite excluir la propia cita al validar una
     *                       edición (puede ser null al crear una cita nueva).
     */
    @Query("SELECT CASE WHEN COUNT(c) > 0 THEN true ELSE false END FROM Cita c " +
           "WHERE c.veterinario.id = :veterinarioId " +
           "AND c.fechaCita = :fecha " +
           "AND c.estado IN ('AGENDADA', 'CONFIRMADA') " +
           "AND (:idCitaExcluir IS NULL OR c.id <> :idCitaExcluir) " +
           "AND c.horaInicio < :horaFin " +
           "AND :horaInicio < c.horaFin")
    boolean existeSolapamiento(@Param("veterinarioId") Long veterinarioId,
                                @Param("fecha") LocalDate fecha,
                                @Param("horaInicio") LocalTime horaInicio,
                                @Param("horaFin") LocalTime horaFin,
                                @Param("idCitaExcluir") Long idCitaExcluir);
}
