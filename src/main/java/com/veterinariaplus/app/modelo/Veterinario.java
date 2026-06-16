package com.veterinariaplus.app.modelo;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * Veterinario (medico) que atiende consultas en la clinica.
 */
@Entity
@Table(name = "veterinario")
public class Veterinario extends EntidadBase {

    @NotBlank(message = "{veterinario.nombre.requerido}")
    @Size(max = 80, message = "{veterinario.nombre.tamano}")
    @Column(name = "nombre", nullable = false, length = 80)
    private String nombre;

    @NotBlank(message = "{veterinario.apellido.requerido}")
    @Size(max = 80, message = "{veterinario.apellido.tamano}")
    @Column(name = "apellido", nullable = false, length = 80)
    private String apellido;

    @NotBlank(message = "{veterinario.cedulaProfesional.requerido}")
    @Size(max = 30, message = "{veterinario.cedulaProfesional.tamano}")
    @Column(name = "cedula_profesional", nullable = false, length = 30, unique = true)
    private String cedulaProfesional;

    @NotNull(message = "{veterinario.especialidad.requerido}")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "especialidad_id", nullable = false)
    private Especialidad especialidad;

    @Column(name = "activo", nullable = false)
    private boolean activo = true;

    public Veterinario() {
    }

    public Veterinario(String nombre, String apellido, String cedulaProfesional, Especialidad especialidad) {
        this.nombre = nombre;
        this.apellido = apellido;
        this.cedulaProfesional = cedulaProfesional;
        this.especialidad = especialidad;
        this.activo = true;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellido() {
        return apellido;
    }

    public void setApellido(String apellido) {
        this.apellido = apellido;
    }

    public String getCedulaProfesional() {
        return cedulaProfesional;
    }

    public void setCedulaProfesional(String cedulaProfesional) {
        this.cedulaProfesional = cedulaProfesional;
    }

    public Especialidad getEspecialidad() {
        return especialidad;
    }

    public void setEspecialidad(Especialidad especialidad) {
        this.especialidad = especialidad;
    }

    public boolean isActivo() {
        return activo;
    }

    public void setActivo(boolean activo) {
        this.activo = activo;
    }

    public String getNombreCompleto() {
        return "Dr(a). " + nombre + " " + apellido;
    }

    @Override
    public String toString() {
        return getNombreCompleto();
    }
}
