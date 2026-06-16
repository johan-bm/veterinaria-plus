package com.veterinariaplus.app.servicio.impl;

import com.veterinariaplus.app.dto.CitaResumenDTO;
import com.veterinariaplus.app.modelo.Cita;
import com.veterinariaplus.app.modelo.Especialidad;
import com.veterinariaplus.app.modelo.Especie;
import com.veterinariaplus.app.modelo.Mascota;
import com.veterinariaplus.app.modelo.Propietario;
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
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

/**
 * Pruebas unitarias de ReporteServiceImpl, enfocadas en la
 * transformación de Cita a CitaResumenDTO y en el agrupamiento
 * de estadísticas por estado.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("ReporteServiceImpl")
class ReporteServiceImplTest {

    @Mock
    private CitaRepository citaRepository;

    @InjectMocks
    private ReporteServiceImpl reporteService;

    private LocalDate inicio;
    private LocalDate fin;

    @BeforeEach
    void setUp() {
        inicio = LocalDate.now();
        fin = inicio.plusDays(7);
    }

    private Cita crearCita(Cita.EstadoCita estado) {
        Especie especie = new Especie("Canino", "Perro");
        especie.setId(1L);
        Propietario propietario = new Propietario("Ana", "Pérez", "5551234567", "ana@example.com", "Calle 1");
        propietario.setId(1L);
        Mascota mascota = new Mascota("Firulais", especie, "Labrador", LocalDate.of(2020, 1, 1),
                Mascota.Sexo.MACHO, propietario);
        mascota.setId(1L);

        Especialidad especialidad = new Especialidad("Medicina General", "Consultas generales");
        especialidad.setId(1L);
        Veterinario veterinario = new Veterinario("Carlos", "Gómez", "VET-001", especialidad);
        veterinario.setId(1L);

        Cita cita = new Cita(mascota, veterinario, LocalDate.now().plusDays(1),
                LocalTime.of(10, 0), LocalTime.of(10, 30), "Consulta");
        cita.setEstado(estado);
        return cita;
    }

    @Test
    @DisplayName("citasPorPeriodo transforma correctamente las citas en DTOs de resumen")
    void citasPorPeriodoTransformaEnDtos() {
        Cita cita1 = crearCita(Cita.EstadoCita.AGENDADA);
        Cita cita2 = crearCita(Cita.EstadoCita.CONFIRMADA);
        when(citaRepository.findByFechaCitaBetween(inicio, fin)).thenReturn(List.of(cita1, cita2));

        List<CitaResumenDTO> resultado = reporteService.citasPorPeriodo(inicio, fin);

        assertThat(resultado).hasSize(2);
    }

    @Test
    @DisplayName("citasPorPeriodo devuelve lista vacía cuando no hay citas en el rango")
    void citasPorPeriodoDevuelveListaVaciaSinCitas() {
        when(citaRepository.findByFechaCitaBetween(inicio, fin)).thenReturn(List.of());

        List<CitaResumenDTO> resultado = reporteService.citasPorPeriodo(inicio, fin);

        assertThat(resultado).isEmpty();
    }

    @Test
    @DisplayName("estadisticasPorEstado agrupa correctamente las citas por su estado")
    void estadisticasPorEstadoAgrupaCorrectamente() {
        Cita agendada1 = crearCita(Cita.EstadoCita.AGENDADA);
        Cita agendada2 = crearCita(Cita.EstadoCita.AGENDADA);
        Cita confirmada = crearCita(Cita.EstadoCita.CONFIRMADA);
        when(citaRepository.findByFechaCitaBetween(inicio, fin))
                .thenReturn(List.of(agendada1, agendada2, confirmada));

        Map<String, Long> resultado = reporteService.estadisticasPorEstado(inicio, fin);

        assertThat(resultado).containsEntry("AGENDADA", 2L);
        assertThat(resultado).containsEntry("CONFIRMADA", 1L);
        assertThat(resultado).doesNotContainKey("CANCELADA");
    }

    @Test
    @DisplayName("estadisticasPorEstado devuelve mapa vacío cuando no hay citas")
    void estadisticasPorEstadoDevuelveMapaVacioSinCitas() {
        when(citaRepository.findByFechaCitaBetween(inicio, fin)).thenReturn(List.of());

        Map<String, Long> resultado = reporteService.estadisticasPorEstado(inicio, fin);

        assertThat(resultado).isEmpty();
    }
}
