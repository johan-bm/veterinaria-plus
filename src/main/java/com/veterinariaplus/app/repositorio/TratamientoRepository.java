package com.veterinariaplus.app.repositorio;

import com.veterinariaplus.app.modelo.Tratamiento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repositorio para los tratamientos prescritos.
 */
@Repository
public interface TratamientoRepository extends JpaRepository<Tratamiento, Long> {

    List<Tratamiento> findByHistorialClinicoId(Long historialId);
}
