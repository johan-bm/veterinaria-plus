package com.veterinariaplus.app.servicio;

import com.veterinariaplus.app.modelo.Medicamento;

import java.util.List;

/**
 * Operaciones de negocio disponibles para la gestión de medicamentos.
 *
 * Cubre RF-05 (catálogo y stock) y RF-12 (alertas de stock bajo).
 */
public interface MedicamentoService extends ServicioCrud<Medicamento, Long> {

    /**
     * Devuelve los medicamentos cuyo stock actual está por debajo
     * del mínimo configurado (RF-12).
     */
    List<Medicamento> buscarConStockBajo();

    /**
     * Descuenta unidades del stock de un medicamento, por ejemplo al
     * prescribirlo en un tratamiento.
     *
     * @throws com.veterinariaplus.app.excepcion.ReglaDeNegocioException
     *         si no hay suficiente stock disponible
     */
    Medicamento descontarStock(Long medicamentoId, int cantidad);

    /**
     * Incrementa el stock de un medicamento, por ejemplo al recibir
     * una nueva compra del proveedor.
     */
    Medicamento incrementarStock(Long medicamentoId, int cantidad);
}
