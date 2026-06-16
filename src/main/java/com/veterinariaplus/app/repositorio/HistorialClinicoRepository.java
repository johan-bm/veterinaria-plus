package com.veterinariaplus.app.repositorio;

import com.veterinariaplus.app.modelo.HistorialClinico;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repositorio para los historiales clínicos.
 */
@Repository
public interface HistorialClinicoRepository extends JpaRepository<HistorialClinico, Long> {

    Optional<HistorialClinico> findByCitaId(Long citaId);

    /**
     * Obtiene el historial clínico completo de una mascota, ordenado
     * del más reciente al más antiguo (RF-04 / CU-06).
     */
    @Query("SELECT h FROM HistorialClinico h " +
           "JOIN h.cita c " +
           "WHERE c.mascota.id = :mascotaId " +
           "ORDER BY c.fechaCita DESC, c.horaInicio DESC")
    List<HistorialClinico> findHistorialPorMascota(@Param("mascotaId") Long mascotaId);
}
