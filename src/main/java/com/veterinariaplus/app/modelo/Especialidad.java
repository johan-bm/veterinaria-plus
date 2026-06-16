package com.veterinariaplus.app.modelo;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Catalogo de especialidades veterinarias
 * (ej. Medicina General, Cirugia, Dermatologia, Odontologia).
 */
@Entity
@Table(name = "especialidad")
public class Especialidad extends EntidadBase {

    @NotBlank(message = "{especialidad.nombre.requerido}")
    @Size(max = 80, message = "{especialidad.nombre.tamano}")
    @Column(name = "nombre", nullable = false, length = 80, unique = true)
    private String nombre;

    @Size(max = 255, message = "{especialidad.descripcion.tamano}")
    @Column(name = "descripcion", length = 255)
    private String descripcion;

    public Especialidad() {
    }

    public Especialidad(String nombre, String descripcion) {
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
