package com.veterinariaplus.app.modelo;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Catalogo de especies de mascotas atendidas en la clinica
 * (ej. Canino, Felino, Ave, Roedor, etc).
 */
@Entity
@Table(name = "especie")
public class Especie extends EntidadBase {

    @NotBlank(message = "{especie.nombre.requerido}")
    @Size(max = 60, message = "{especie.nombre.tamano}")
    @Column(name = "nombre", nullable = false, length = 60, unique = true)
    private String nombre;

    @Size(max = 255, message = "{especie.descripcion.tamano}")
    @Column(name = "descripcion", length = 255)
    private String descripcion;

    public Especie() {
    }

    public Especie(String nombre, String descripcion) {
        this.nombre = nombre;
        this.descripcion = descripcion;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    @Override
    public String toString() {
        return nombre;
    }
}
