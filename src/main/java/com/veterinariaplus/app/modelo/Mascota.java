package com.veterinariaplus.app.modelo;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

/**
 * Mascota (paciente) registrada en la clinica veterinaria.
 */
@Entity
@Table(name = "mascota")
public class Mascota extends EntidadBase {

    @NotBlank(message = "{mascota.nombre.requerido}")
    @Size(max = 60, message = "{mascota.nombre.tamano}")
    @Column(name = "nombre", nullable = false, length = 60)
    private String nombre;

    @NotNull(message = "{mascota.especie.requerido}")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "especie_id", nullable = false)
    private Especie especie;

    @Size(max = 60, message = "{mascota.raza.tamano}")
    @Column(name = "raza", length = 60)
    private String raza;

    @Past(message = "{mascota.fechaNacimiento.pasado}")
    @Column(name = "fecha_nacimiento")
    private LocalDate fechaNacimiento;

    @Enumerated(EnumType.STRING)
    @Column(name = "sexo", length = 10)
    private Sexo sexo;

    @NotNull(message = "{mascota.propietario.requerido}")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "propietario_id", nullable = false)
    private Propietario propietario;

    public Mascota() {
    }

    public Mascota(String nombre, Especie especie, String raza, LocalDate fechaNacimiento,
                    Sexo sexo, Propietario propietario) {
        this.nombre = nombre;
        this.especie = especie;
        this.raza = raza;
        this.fechaNacimiento = fechaNacimiento;
        this.sexo = sexo;
        this.propietario = propietario;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public Especie getEspecie() {
        return especie;
    }

    public void setEspecie(Especie especie) {
        this.especie = especie;
    }

    public String getRaza() {
        return raza;
    }

    public void setRaza(String raza) {
        this.raza = raza;
    }

    public LocalDate getFechaNacimiento() {
        return fechaNacimiento;
    }

    public void setFechaNacimiento(LocalDate fechaNacimiento) {
        this.fechaNacimiento = fechaNacimiento;
    }

    public Sexo getSexo() {
        return sexo;
    }

    public void setSexo(Sexo sexo) {
        this.sexo = sexo;
    }

    public Propietario getPropietario() {
        return propietario;
    }

    public void setPropietario(Propietario propietario) {
        this.propietario = propietario;
    }

    @Override
    public String toString() {
        return nombre;
    }

    /**
     * Sexo biologico de la mascota.
     */
    public enum Sexo {
        MACHO, HEMBRA, DESCONOCIDO
    }
}
