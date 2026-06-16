package com.veterinariaplus.app.dto;

import com.veterinariaplus.app.modelo.Cita;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 * DTO de solo lectura para mostrar una cita en listados y reportes,
 * con los nombres ya resueltos (evita que la vista necesite navegar
 * relaciones JPA perezosas).
 */
public class CitaResumenDTO {

    private final Long id;
    private final String nombreMascota;
    private final String nombreVeterinario;
    private final LocalDate fechaCita;
    private final LocalTime horaInicio;
    private final LocalTime horaFin;
    private final String estado;
    private final String motivo;

    public CitaResumenDTO(Cita cita) {
        this.id = cita.getId();
        this.nombreMascota = cita.getMascota().getNombre();
        this.nombreVeterinario = cita.getVeterinario().getNombreCompleto();
        this.fechaCita = cita.getFechaCita();
        this.horaInicio = cita.getHoraInicio();
        this.horaFin = cita.getHoraFin();
        this.estado = cita.getEstado().name();
        this.motivo = cita.getMotivo();
    }

    public Long getId() {
        return id;
    }

    public String getNombreMascota() {
        return nombreMascota;
    }

    public String getNombreVeterinario() {
        return nombreVeterinario;
    }

    public LocalDate getFechaCita() {
        return fechaCita;
    }

    public LocalTime getHoraInicio() {
        return horaInicio;
    }

    public LocalTime getHoraFin() {
        return horaFin;
    }

    public String getEstado() {
        return estado;
    }

    public String getMotivo() {
        return motivo;
    }
}
