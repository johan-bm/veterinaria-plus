package com.veterinariaplus.app.servicio.impl;

import com.veterinariaplus.app.excepcion.RecursoNoEncontradoException;
import com.veterinariaplus.app.excepcion.ReglaDeNegocioException;
import com.veterinariaplus.app.modelo.Cita;
import com.veterinariaplus.app.modelo.Especialidad;
import com.veterinariaplus.app.modelo.Especie;
import com.veterinariaplus.app.modelo.HistorialClinico;
import com.veterinariaplus.app.modelo.Mascota;
import com.veterinariaplus.app.modelo.Propietario;
import com.veterinariaplus.app.modelo.Veterinario;
import com.veterinariaplus.app.repositorio.CitaRepository;
import com.veterinariaplus.app.repositorio.HistorialClinicoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Pruebas unitarias de HistorialClinicoServiceImpl, enfocadas en la
 * regla de negocio de un historial por cita y en el efecto colateral
 * de marcar la cita como completada al registrar el historial.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("HistorialClinicoServiceImpl")
class HistorialClinicoServiceImplTest {

    @Mock
    private HistorialClinicoRepository historialClinicoRepository;

    @Mock
    private CitaRepository citaRepository;

    @InjectMocks
    private HistorialClinicoServiceImpl historialClinicoService;

    private Cita cita;

    @BeforeEach
    void setUp() {
        Especie especie = new Especie("Felino", "Gato doméstico");
        especie.setId(1L);
        Propietario propietario = new Propietario("Marta", "Ruiz", "5550001111", "marta@example.com", "Calle 1");
        propietario.setId(1L);
        Mascota mascota = new Mascota("Michi", especie, "Siamés", LocalDate.of(2019, 5, 10),
                Mascota.Sexo.HEMBRA, propietario);
        mascota.setId(1L);

        Especialidad especialidad = new Especialidad("Medicina General", "Consultas generales");
        especialidad.setId(1L);
        Veterinario veterinario = new Veterinario("Pedro", "Salas", "VET-100", especialidad);
        veterinario.setId(1L);

        cita = new Cita(mascota, veterinario, LocalDate.now().plusDays(1),
                LocalTime.of(10, 0), LocalTime.of(10, 30), "Consulta de control");
        cita.setId(10L);
        cita.setEstado(Cita.EstadoCita.CONFIRMADA);
    }

    @Test
    @DisplayName("registra el historial y marca la cita como completada")
    void registraHistorialYCompletaLaCita() {
        when(citaRepository.findById(10L)).thenReturn(Optional.of(cita));
        when(historialClinicoRepository.findByCitaId(10L)).thenReturn(Optional.empty());
        when(historialClinicoRepository.save(any(HistorialClinico.class)))
                .thenAnswer(inv -> inv.getArgument(0));
        when(citaRepository.save(any(Cita.class))).thenAnswer(inv -> inv.getArgument(0));

        HistorialClinico resultado = historialClinicoService.registrar(
                10L, "Tos persistente", "Bronquitis leve", "Sin notas adicionales");

        assertThat(resultado.getSintomas()).isEqualTo("Tos persistente");
        assertThat(resultado.getDiagnostico()).isEqualTo("Bronquitis leve");
        assertThat(cita.getEstado()).isEqualTo(Cita.EstadoCita.COMPLETADA);

        ArgumentCaptor<Cita> captor = ArgumentCaptor.forClass(Cita.class);
        verify(citaRepository, times(1)).save(captor.capture());
        assertThat(captor.getValue().getEstado()).isEqualTo(Cita.EstadoCita.COMPLETADA);
    }

    @Test
    @DisplayName("lanza excepción cuando la cita no existe")
    void lanzaExcepcionCuandoCitaNoExiste() {
        when(citaRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> historialClinicoService.registrar(99L, "Síntomas", "Diagnóstico", null))
                .isInstanceOf(RecursoNoEncontradoException.class);

        verify(historialClinicoRepository, never()).save(any());
    }

    @Test
    @DisplayName("rechaza registrar un segundo historial para la misma cita")
    void rechazaSegundoHistorialParaLaMismaCita() {
        HistorialClinico historialExistente = new HistorialClinico(cita, "Síntomas previos", "Diagnóstico previo", null);

        when(citaRepository.findById(10L)).thenReturn(Optional.of(cita));
        when(historialClinicoRepository.findByCitaId(10L)).thenReturn(Optional.of(historialExistente));

        assertThatThrownBy(() -> historialClinicoService.registrar(10L, "Síntomas nuevos", "Diagnóstico nuevo", null))
                .isInstanceOf(ReglaDeNegocioException.class)
                .hasMessageContaining("ya tiene un historial clínico");

        verify(historialClinicoRepository, never()).save(any());
        verify(citaRepository, never()).save(any());
    }

    @Test
    @DisplayName("buscarPorCita delega en el repositorio")
    void buscarPorCitaDelegaEnRepositorio() {
        HistorialClinico historial = new HistorialClinico(cita, "Síntomas", "Diagnóstico", null);
        when(historialClinicoRepository.findByCitaId(10L)).thenReturn(Optional.of(historial));

        Optional<HistorialClinico> resultado = historialClinicoService.buscarPorCita(10L);

        assertThat(resultado).contains(historial);
    }

    @Test
    @DisplayName("buscarPorMascota delega en el repositorio")
    void buscarPorMascotaDelegaEnRepositorio() {
        HistorialClinico historial = new HistorialClinico(cita, "Síntomas", "Diagnóstico", null);
        when(historialClinicoRepository.findHistorialPorMascota(1L)).thenReturn(List.of(historial));

        List<HistorialClinico> resultado = historialClinicoService.buscarPorMascota(1L);

        assertThat(resultado).containsExactly(historial);
    }

    @Test
    @DisplayName("buscarPorId delega en el repositorio")
    void buscarPorIdDelegaEnRepositorio() {
        HistorialClinico historial = new HistorialClinico(cita, "Síntomas", "Diagnóstico", null);
        historial.setId(5L);
        when(historialClinicoRepository.findById(5L)).thenReturn(Optional.of(historial));

        Optional<HistorialClinico> resultado = historialClinicoService.buscarPorId(5L);

        assertThat(resultado).contains(historial);
    }
}
