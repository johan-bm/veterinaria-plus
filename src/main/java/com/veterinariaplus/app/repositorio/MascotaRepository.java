package com.veterinariaplus.app.repositorio;

import com.veterinariaplus.app.modelo.Mascota;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repositorio para la gestión de mascotas.
 *
 * Cubre el requerimiento RF-06: búsqueda de mascotas por nombre,
 * especie o propietario.
 */
@Repository
public interface MascotaRepository extends JpaRepository<Mascota, Long> {

    List<Mascota> findByPropietarioId(Long propietarioId);

    List<Mascota> findByEspecieId(Long especieId);

    /**
     * Búsqueda combinada por nombre de la mascota, nombre de la especie
     * o nombre/apellido del propietario (RF-06).
     */
    @Query("SELECT m FROM Mascota m " +
           "JOIN m.propietario p " +
           "JOIN m.especie e " +
           "WHERE LOWER(m.nombre) LIKE LOWER(CONCAT('%', :texto, '%')) " +
           "OR LOWER(e.nombre) LIKE LOWER(CONCAT('%', :texto, '%')) " +
           "OR LOWER(p.nombre) LIKE LOWER(CONCAT('%', :texto, '%')) " +
           "OR LOWER(p.apellido) LIKE LOWER(CONCAT('%', :texto, '%'))")
    List<Mascota> buscar(@Param("texto") String texto);
}
