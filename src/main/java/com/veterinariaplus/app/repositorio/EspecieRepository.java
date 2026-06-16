package com.veterinariaplus.app.repositorio;

import com.veterinariaplus.app.modelo.Especie;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repositorio para el catálogo de especies.
 *
 * Aplica el patrón Repository: abstrae el acceso a datos de la entidad
 * Especie sin que las capas superiores conozcan los detalles de JPA/Hibernate.
 */
@Repository
public interface EspecieRepository extends JpaRepository<Especie, Long> {

    Optional<Especie> findByNombreIgnoreCase(String nombre);

    boolean existsByNombreIgnoreCase(String nombre);
}
