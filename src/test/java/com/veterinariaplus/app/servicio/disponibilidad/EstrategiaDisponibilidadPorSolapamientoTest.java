package com.veterinariaplus.app.servicio.disponibilidad;

import com.veterinariaplus.app.modelo.Especialidad;
import com.veterinariaplus.app.modelo.Veterinario;
import com.veterinariaplus.app.repositorio.CitaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

/**
 * Pruebas unitarias de EstrategiaDisponibilidadPorSolapamiento
 * (implementación del patrón Strategy usada por CitaServiceImpl).
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("EstrategiaDisponibilidadPorSolapamiento")
class EstrategiaDisponibilidadPorSolapamientoTest {

    @Mock
    private CitaRepository citaRepository;

    @InjectMocks
    private EstrategiaDisponibilidadPorSolapamiento estrategia;

    private Veterinario veterinario;

    @BeforeEach
    void setUp() {
        Especialidad especialidad = new Especialidad("Cirugía", "Procedimientos quirúrgicos");
        especialidad.setId(1L);
        veterinario = new Veterinario("Laura", "Méndez", "VET-002", especialidad);
        veterinario.setId(10L);
    }

    @Test
    @DisplayName("retorna true (disponible) cuando el repositorio no encuentra solapamiento")
    void disponibleCuandoNoHaySolapamiento() {
        LocalDate fecha = LocalDate.now().plusDays(1);
        LocalTime inicio = LocalTime.of(10, 0);
        LocalTime fin = LocalTime.of(10, 30);

        when(citaRepository.existeSolapamiento(eq(10L), eq(fecha), eq(inicio), eq(fin), any()))
                .thenReturn(false);

        boolean disponible = estrategia.estaDisponible(veterinario, fecha, inicio, fin, null);

        assertThat(disponible).isTrue();
    }

    @Test
    @DisplayName("retorna false (no disponible) cuando el repositorio encuentra solapamiento")
    void noDisponibleCuandoHaySolapamiento() {
        LocalDate fecha = LocalDate.now().plusDays(1);
        LocalTime inicio = LocalTime.of(10, 0);
        LocalTime fin = LocalTime.of(10, 30);

        when(citaRepository.existeSolapamiento(eq(10L), eq(fecha), eq(inicio), eq(fin), any()))
                .thenReturn(true);

        boolean disponible = estrategia.estaDisponible(veterinario, fecha, inicio, fin, null);

        assertThat(disponible).isFalse();
    }
}
