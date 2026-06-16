package com.veterinariaplus.app.modelo;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * Historial clinico generado a partir de una cita: documenta sintomas,
 * diagnostico y notas de la consulta. Cada cita completada tiene a lo
 * sumo un historial clinico asociado (relacion 1:1).
 */
@Entity
@Table(name = "historial_clinico")
public class HistorialClinico extends EntidadBase {

    @NotNull(message = "{historial.cita.requerido}")
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cita_id", nullable = false, unique = true)
    private Cita cita;

    @NotBlank(message = "{historial.sintomas.requerido}")
    @Column(name = "sintomas", nullable = false, columnDefinition = "TEXT")
    private String sintomas;

    @NotBlank(message = "{historial.diagnostico.requerido}")
    @Column(name = "diagnostico", nullable = false, columnDefinition = "TEXT")
    private String diagnostico;

    @Column(name = "notas", columnDefinition = "TEXT")
    private String notas;

    @OneToMany(mappedBy = "historialClinico", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Tratamiento> tratamientos = new ArrayList<>();

    public HistorialClinico() {
    }

    public HistorialClinico(Cita cita, String sintomas, String diagnostico, String notas) {
        this.cita = cita;
        this.sintomas = sintomas;
        this.diagnostico = diagnostico;
        this.notas = notas;
    }

    public Cita getCita() {
        return cita;
    }

    public void setCita(Cita cita) {
        this.cita = cita;
    }

    public String getSintomas() {
        return sintomas;
    }

    public void setSintomas(String sintomas) {
        this.sintomas = sintomas;
    }

    public String getDiagnostico() {
        return diagnostico;
    }

    public void setDiagnostico(String diagnostico) {
        this.diagnostico = diagnostico;
    }

    public String getNotas() {
        return notas;
    }

    public void setNotas(String notas) {
        this.notas = notas;
    }

    public List<Tratamiento> getTratamientos() {
        return tratamientos;
    }

    public void setTratamientos(List<Tratamiento> tratamientos) {
        this.tratamientos = tratamientos;
    }
}
