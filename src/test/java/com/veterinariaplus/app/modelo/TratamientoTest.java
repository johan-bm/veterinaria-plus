package com.veterinariaplus.app.modelo;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Pruebas unitarias del patrón Builder implementado en Tratamiento.
 */
@DisplayName("Tratamiento.Builder")
class TratamientoTest {

    @Test
    @DisplayName("construye un tratamiento completo con todos los campos opcionales")
    void construyeTratamientoCompleto() {
        Cita cita = new Cita();
        HistorialClinico historial = new HistorialClinico(cita, "Tos persistente", "Bronquitis leve", "Sin notas");

        Medicamento medicamento = new Medicamento("Amoxicilina", "Amoxicilina trihidratada", 20, 5);

        Tratamiento tratamiento = new Tratamiento.Builder()
                .historialClinico(historial)
                .descripcion("Antibiótico cada 12 horas")
                .duracionDias(7)
                .indicaciones("Administrar con alimento")
                .agregarMedicamento(medicamento)
                .build();

        assertThat(tratamiento.getDescripcion()).isEqualTo("Antibiótico cada 12 horas");
        assertThat(tratamiento.getDuracionDias()).isEqualTo(7);
        assertThat(tratamiento.getIndicaciones()).isEqualTo("Administrar con alimento");
        assertThat(tratamiento.getMedicamentos()).hasSize(1).contains(medicamento);
        assertThat(tratamiento.getHistorialClinico()).isEqualTo(historial);
    }

    @Test
    @DisplayName("construye un tratamiento mínimo sin los campos opcionales")
    void construyeTratamientoMinimoSinCamposOpcionales() {
        Cita cita = new Cita();
        HistorialClinico historial = new HistorialClinico(cita, "Revisión general", "Sin hallazgos", null);

        Tratamiento tratamiento = new Tratamiento.Builder()
                .historialClinico(historial)
                .descripcion("Observación, sin medicación")
                .build();

        assertThat(tratamiento.getDescripcion()).isEqualTo("Observación, sin medicación");
        assertThat(tratamiento.getDuracionDias()).isNull();
        assertThat(tratamiento.getMedicamentos()).isEmpty();
    }

    @Test
    @DisplayName("lanza excepción si se intenta construir sin descripción")
    void lanzaExcepcionSinDescripcion() {
        Cita cita = new Cita();
        HistorialClinico historial = new HistorialClinico(cita, "Síntomas", "Diagnóstico", null);

        assertThatThrownBy(() -> new Tratamiento.Builder()
                .historialClinico(historial)
                .build())
                .isInstanceOf(IllegalStateException.class);
    }

    @Test
    @DisplayName("permite agregar múltiples medicamentos al tratamiento")
    void permiteMultiplesMedicamentos() {
        Cita cita = new Cita();
        HistorialClinico historial = new HistorialClinico(cita, "Infección", "Infección bacteriana", null);
        Medicamento medicamento1 = new Medicamento("Amoxicilina", "Amoxicilina", 20, 5);
        Medicamento medicamento2 = new Medicamento("Ibuprofeno", "Ibuprofeno", 30, 10);

        Tratamiento tratamiento = new Tratamiento.Builder()
                .historialClinico(historial)
                .descripcion("Tratamiento combinado")
                .agregarMedicamento(medicamento1)
                .agregarMedicamento(medicamento2)
                .build();

        assertThat(tratamiento.getMedicamentos()).hasSize(2);
    }
}
