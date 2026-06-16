package com.veterinariaplus.app.modelo;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * Medicamento del catalogo de la clinica, con control de existencias (stock).
 */
@Entity
@Table(name = "medicamento")
public class Medicamento extends EntidadBase {

    @NotBlank(message = "{medicamento.nombre.requerido}")
    @Size(max = 100, message = "{medicamento.nombre.tamano}")
    @Column(name = "nombre", nullable = false, length = 100)
    private String nombre;

    @NotBlank(message = "{medicamento.principioActivo.requerido}")
    @Size(max = 100, message = "{medicamento.principioActivo.tamano}")
    @Column(name = "principio_activo", nullable = false, length = 100)
    private String principioActivo;

    @NotNull(message = "{medicamento.stock.requerido}")
    @Min(value = 0, message = "{medicamento.stock.minimo}")
    @Column(name = "stock", nullable = false)
    private Integer stock;

    @NotNull(message = "{medicamento.stockMinimo.requerido}")
    @Min(value = 0, message = "{medicamento.stockMinimo.minimo}")
    @Column(name = "stock_minimo", nullable = false)
    private Integer stockMinimo;

    public Medicamento() {
    }

    public Medicamento(String nombre, String principioActivo, Integer stock, Integer stockMinimo) {
        this.nombre = nombre;
        this.principioActivo = principioActivo;
        this.stock = stock;
        this.stockMinimo = stockMinimo;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getPrincipioActivo() {
        return principioActivo;
    }

    public void setPrincipioActivo(String principioActivo) {
        this.principioActivo = principioActivo;
    }

    public Integer getStock() {
        return stock;
    }

    public void setStock(Integer stock) {
        this.stock = stock;
    }

    public Integer getStockMinimo() {
        return stockMinimo;
    }

    public void setStockMinimo(Integer stockMinimo) {
        this.stockMinimo = stockMinimo;
    }

    /**
     * Indica si el stock actual esta por debajo del minimo configurado.
     * Utilizado para generar alertas (RF-12 de la propuesta de proyecto).
     */
    public boolean isStockBajo() {
        return stock != null && stockMinimo != null && stock < stockMinimo;
    }

    @Override
    public String toString() {
        return nombre;
    }
}
