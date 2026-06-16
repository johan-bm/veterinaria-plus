package com.veterinariaplus.app.modelo;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.HashSet;
import java.util.Set;

/**
 * Tratamiento prescrito dentro de un historial clinico. Puede incluir
 * uno o varios medicamentos (relacion N:M).
 *
 * Implementa el patron de diseno Builder, ya que un Tratamiento tiene
 * varios campos opcionales (medicamentos, duracion, indicaciones) y
 * construirlo con un constructor tradicional resultaria en muchas
 * sobrecargas o un constructor con demasiados parametros.
 *
 * Ejemplo de uso:
 * <pre>
 *   Tratamiento tratamiento = new Tratamiento.Builder()
 *       .historialClinico(historial)
 *       .descripcion("Antibiotico cada 12 horas")
 *       .duracionDias(7)
 *       .agregarMedicamento(amoxicilina)
 *       .build();
 * </pre>
 */
@Entity
@Table(name = "tratamiento")
public class Tratamiento extends EntidadBase {

    @NotNull(message = "{tratamiento.historialClinico.requerido}")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "historial_id", nullable = false)
    private HistorialClinico historialClinico;

    @NotBlank(message = "{tratamiento.descripcion.requerido}")
    @Column(name = "descripcion", nullable = false, length = 255)
    private String descripcion;

    @Min(value = 1, message = "{tratamiento.duracionDias.minimo}")
    @Column(name = "duracion_dias")
    private Integer duracionDias;

    @Column(name = "indicaciones", length = 500)
    private String indicaciones;

    @ManyToMany(cascade = { CascadeType.PERSIST, CascadeType.MERGE })
    @JoinTable(
        name = "tratamiento_medicamento",
        joinColumns = @JoinColumn(name = "tratamiento_id"),
        inverseJoinColumns = @JoinColumn(name = "medicamento_id")
    )
    private Set<Medicamento> medicamentos = new HashSet<>();

    /** Constructor protegido: la construccion publica se hace via Builder. */
    protected Tratamiento() {
    }

    private Tratamiento(Builder builder) {
        this.historialClinico = builder.historialClinico;
        this.descripcion = builder.descripcion;
        this.duracionDias = builder.duracionDias;
        this.indicaciones = builder.indicaciones;
        this.medicamentos = builder.medicamentos;
    }

    public HistorialClinico getHistorialClinico() {
        return historialClinico;
    }

    public void setHistorialClinico(HistorialClinico historialClinico) {
        this.historialClinico = historialClinico;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public Integer getDuracionDias() {
        return duracionDias;
    }

    public void setDuracionDias(Integer duracionDias) {
        this.duracionDias = duracionDias;
    }

    public String getIndicaciones() {
        return indicaciones;
    }

    public void setIndicaciones(String indicaciones) {
        this.indicaciones = indicaciones;
    }

    public Set<Medicamento> getMedicamentos() {
        return medicamentos;
    }

    public void setMedicamentos(Set<Medicamento> medicamentos) {
        this.medicamentos = medicamentos;
    }

    /**
     * Builder para construir instancias de Tratamiento de forma legible,
     * evitando constructores con muchos parametros opcionales.
     */
    public static class Builder {
        private HistorialClinico historialClinico;
        private String descripcion;
        private Integer duracionDias;
        private String indicaciones;
        private Set<Medicamento> medicamentos = new HashSet<>();

        public Builder historialClinico(HistorialClinico historialClinico) {
            this.historialClinico = historialClinico;
            return this;
        }

        public Builder descripcion(String descripcion) {
            this.descripcion = descripcion;
            return this;
        }

        public Builder duracionDias(Integer duracionDias) {
            this.duracionDias = duracionDias;
            return this;
        }

        public Builder indicaciones(String indicaciones) {
            this.indicaciones = indicaciones;
            return this;
        }

        public Builder agregarMedicamento(Medicamento medicamento) {
            this.medicamentos.add(medicamento);
            return this;
        }

        public Builder medicamentos(Set<Medicamento> medicamentos) {
            this.medicamentos = medicamentos;
            return this;
        }

        public Tratamiento build() {
            if (descripcion == null || descripcion.isBlank()) {
                throw new IllegalStateException("La descripcion del tratamiento es obligatoria");
            }
            return new Tratamiento(this);
        }
    }
}
