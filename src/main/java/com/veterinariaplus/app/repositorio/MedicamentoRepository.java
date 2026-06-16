package com.veterinariaplus.app.repositorio;

import com.veterinariaplus.app.modelo.Medicamento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repositorio para el catálogo de medicamentos.
 *
 * Incluye la consulta para RF-12: alertar cuando el stock esté
 * por debajo del mínimo configurado.
 */
@Repository
public interface MedicamentoRepository extends JpaRepository<Medicamento, Long> {

    Optional<Medicamento> findByNombreIgnoreCase(String nombre);

    /**
     * Devuelve los medicamentos cuyo stock actual está por debajo
     * de su stock mínimo configurado (RF-12).
     */
    @Query("SELECT m FROM Medicamento m WHERE m.stock < m.stockMinimo")
    List<Medicamento> findConStockBajo();
}
