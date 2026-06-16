package com.veterinariaplus.app.repositorio;

import com.veterinariaplus.app.modelo.Veterinario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repositorio para la gestión de veterinarios.
 */
@Repository
public interface VeterinarioRepository extends JpaRepository<Veterinario, Long> {

    Optional<Veterinario> findByCedulaProfesional(String cedulaProfesional);

    boolean existsByCedulaProfesional(String cedulaProfesional);

    List<Veterinario> findByActivoTrue();

    List<Veterinario> findByEspecialidadId(Long especialidadId);
}
