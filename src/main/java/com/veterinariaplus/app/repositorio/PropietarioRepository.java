package com.veterinariaplus.app.repositorio;

import com.veterinariaplus.app.modelo.Propietario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repositorio para la gestión de propietarios.
 */
@Repository
public interface PropietarioRepository extends JpaRepository<Propietario, Long> {

    Optional<Propietario> findByEmailIgnoreCase(String email);

    boolean existsByEmailIgnoreCase(String email);

    /**
     * Busca propietarios cuyo nombre o apellido contenga el texto indicado
     * (búsqueda parcial, sin distinguir mayúsculas/minúsculas).
     */
    @Query("SELECT p FROM Propietario p WHERE " +
           "LOWER(p.nombre) LIKE LOWER(CONCAT('%', :texto, '%')) OR " +
           "LOWER(p.apellido) LIKE LOWER(CONCAT('%', :texto, '%'))")
    List<Propietario> buscarPorNombreOApellido(@Param("texto") String texto);
}
