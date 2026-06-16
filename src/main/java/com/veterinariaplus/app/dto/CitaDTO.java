package com.veterinariaplus.app.dto;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 * DTO (Data Transfer Object) utilizado para crear o editar una cita
 * desde la capa de vista, sin exponer directamente la entidad JPA Cita.
 *
 * Aplica el patrón DTO: desacopla la representación que usa el formulario
 * HTML (Thymeleaf) de la estructura interna de persistencia.
 */
public class CitaDTO {

    private Long id;

    @NotNull(message = "{cita.mascota.requerido}")
    private Long mascotaId;

    @NotNull(message = "{cita.veterinario.requerido}")
    private Long veterinarioId;

    @NotNull(message = "{cita.fecha.requerido}")
    @FutureOrPresent(message = "{cita.fecha.futuro}")
    private LocalDate fechaCita;

    @NotNull(message = "{cita.horaInicio.requerido}")
    private LocalTime horaInicio;

    @NotNull(message = "{cita.horaFin.requerido}")
    private LocalTime horaFin;

    @Size(max = 255, message = "{cita.motivo.tamano}")
    private String motivo;

    public CitaDTO() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getMascotaId() {
        return mascotaId;
    }

    public void setMascotaId(Long mascotaId) {
        this.mascotaId = mascotaId;
    }

    public Long getVeterinarioId() {
        return veterinarioId;
    }

    public void setVeterinarioId(Long veterinarioId) {
        this.veterinarioId = veterinarioId;
    }

    public LocalDate getFechaCita() {
        return fechaCita;
    }

    public void setFechaCita(LocalDate fechaCita) {
        this.fechaCita = fechaCita;
    }

    public LocalTime getHoraInicio() {
        return horaInicio;
    }

    public void setHoraInicio(LocalTime horaInicio) {
        this.horaInicio = horaInicio;
    }

    public LocalTime getHoraFin() {
        return horaFin;
    }

    public void setHoraFin(LocalTime horaFin) {
        this.horaFin = horaFin;
    }

    public String getMotivo() {
        return motivo;
    }

    public void setMotivo(String motivo) {
        this.motivo = motivo;
    }
}
