package com.veterinariaplus.app.modelo;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 * Cita medica veterinaria agendada para una mascota con un veterinario.
 *
 * El estado de la cita sigue un ciclo de vida controlado (patron State implicito):
 * AGENDADA -> CONFIRMADA -> COMPLETADA
 *                        -> CANCELADA
 */
@Entity
@Table(name = "cita")
public class Cita extends EntidadBase {

    @NotNull(message = "{cita.mascota.requerido}")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mascota_id", nullable = false)
    private Mascota mascota;

    @NotNull(message = "{cita.veterinario.requerido}")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "veterinario_id", nullable = false)
    private Veterinario veterinario;

    @NotNull(message = "{cita.fecha.requerido}")
    @FutureOrPresent(message = "{cita.fecha.futuro}")
    @Column(name = "fecha_cita", nullable = false)
    private LocalDate fechaCita;

    @NotNull(message = "{cita.horaInicio.requerido}")
    @Column(name = "hora_inicio", nullable = false)
    private LocalTime horaInicio;

    @NotNull(message = "{cita.horaFin.requerido}")
    @Column(name = "hora_fin", nullable = false)
    private LocalTime horaFin;

    @NotNull(message = "{cita.estado.requerido}")
    @Enumerated(EnumType.STRING)
    @Column(name = "estado", nullable = false, length = 20)
    private EstadoCita estado;

    @Size(max = 255, message = "{cita.motivo.tamano}")
    @Column(name = "motivo", length = 255)
    private String motivo;

    public Cita() {
        this.estado = EstadoCita.AGENDADA;
    }

    public Cita(Mascota mascota, Veterinario veterinario, LocalDate fechaCita,
                LocalTime horaInicio, LocalTime horaFin, String motivo) {
        this.mascota = mascota;
        this.veterinario = veterinario;
        this.fechaCita = fechaCita;
        this.horaInicio = horaInicio;
        this.horaFin = horaFin;
        this.motivo = motivo;
        this.estado = EstadoCita.AGENDADA;
    }

    public Mascota getMascota() {
        return mascota;
    }

    public void setMascota(Mascota mascota) {
        this.mascota = mascota;
    }

    public Veterinario getVeterinario() {
        return veterinario;
    }

    public void setVeterinario(Veterinario veterinario) {
        this.veterinario = veterinario;
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

    public EstadoCita getEstado() {
        return estado;
    }

    public void setEstado(EstadoCita estado) {
        this.estado = estado;
    }

    public String getMotivo() {
        return motivo;
    }

    public void setMotivo(String motivo) {
        this.motivo = motivo;
    }

    /**
     * Estados posibles del ciclo de vida de una cita.
     */
    public enum EstadoCita {
        AGENDADA,
        CONFIRMADA,
        CANCELADA,
        COMPLETADA
    }
}
