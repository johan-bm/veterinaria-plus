package com.veterinariaplus.app.dto;

import com.veterinariaplus.app.modelo.Mascota;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

/**
 * DTO utilizado para crear o editar una mascota desde la vista.
 */
public class MascotaDTO {

    private Long id;

    @NotBlank(message = "{mascota.nombre.requerido}")
    @Size(max = 60, message = "{mascota.nombre.tamano}")
    private String nombre;

    @NotNull(message = "{mascota.especie.requerido}")
    private Long especieId;

    @Size(max = 60, message = "{mascota.raza.tamano}")
    private String raza;

    @Past(message = "{mascota.fechaNacimiento.pasado}")
    private LocalDate fechaNacimiento;

    private Mascota.Sexo sexo;

    @NotNull(message = "{mascota.propietario.requerido}")
    private Long propietarioId;

    public MascotaDTO() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public Long getEspecieId() {
        return especieId;
    }

    public void setEspecieId(Long especieId) {
        this.especieId = especieId;
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

    public Mascota.Sexo getSexo() {
        return sexo;
    }

    public void setSexo(Mascota.Sexo sexo) {
        this.sexo = sexo;
    }

    public Long getPropietarioId() {
        return propietarioId;
    }

    public void setPropietarioId(Long propietarioId) {
        this.propietarioId = propietarioId;
    }
}
