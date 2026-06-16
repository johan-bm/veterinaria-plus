package com.veterinariaplus.app.servicio.impl;

import com.veterinariaplus.app.dto.CitaDTO;
import com.veterinariaplus.app.evento.CambioEstadoCitaEvent;
import com.veterinariaplus.app.excepcion.RecursoNoEncontradoException;
import com.veterinariaplus.app.excepcion.ReglaDeNegocioException;
import com.veterinariaplus.app.modelo.Cita;
import com.veterinariaplus.app.modelo.Especialidad;
import com.veterinariaplus.app.modelo.Especie;
import com.veterinariaplus.app.modelo.Mascota;
import com.veterinariaplus.app.modelo.Propietario;
import com.veterinariaplus.app.modelo.Veterinario;
import com.veterinariaplus.app.repositorio.CitaRepository;
import com.veterinariaplus.app.repositorio.MascotaRepository;
import com.veterinariaplus.app.repositorio.VeterinarioRepository;
import com.veterinariaplus.app.servicio.disponibilidad.EstrategiaDisponibilidad;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Pruebas unitarias de CitaServiceImpl, la lógica de negocio más
 * importante del sistema (validación de disponibilidad de horario,
 * ciclo de vida de la cita, publicación de eventos).
 *
 * Se usan mocks (Mockito) para los repositorios y la estrategia de
 * disponibilidad, de forma que estas pruebas no dependen de una
 * base de datos real y se ejecutan rápidamente.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("CitaServiceImpl")
class CitaServiceImplTest {

    @Mock
    private CitaRepository citaRepository;

    @Mock
    private MascotaRepository mascotaRepository;

    @Mock
    private VeterinarioRepository veterinarioRepository;

    @Mock
    private EstrategiaDisponibilidad estrategiaDisponibilidad;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    @InjectMocks
    private CitaServiceImpl citaService;

    private Mascota mascota;
    private Veterinario veterinario;
    private CitaDTO citaDto;

    @BeforeEach
    void setUp() {
        Propietario propietario = new Propietario("Ana", "Pérez", "5551234567", "ana@example.com", "Calle 1");
        propietario.setId(1L);

        Especie especie = new Especie("Canino", "Perro doméstico");
        especie.setId(1L);

        mascota = new Mascota("Firulais", especie, "Labrador", LocalDate.of(2020, 1, 1),
                Mascota.Sexo.MACHO, propietario);
        mascota.setId(1L);

        Especialidad especialidad = new Especialidad("Medicina General", "Consultas generales");
        especialidad.setId(1L);

        veterinario = new Veterinario("Carlos", "Gómez", "VET-001", especialidad);
        veterinario.setId(1L);

        citaDto = new CitaDTO();
        citaDto.setMascotaId(1L);
        citaDto.setVeterinarioId(1L);
        citaDto.setFechaCita(LocalDate.now().plusDays(1));
        citaDto.setHoraInicio(LocalTime.of(10, 0));
        citaDto.setHoraFin(LocalTime.of(10, 30));
        citaDto.setMotivo("Consulta de rutina");
    }

    @Nested
    @DisplayName("al agendar una cita")
    class Agendar {

        @Test
        @DisplayName("agenda correctamente cuando el veterinario está disponible")
        void agendaCuandoHayDisponibilidad() {
            when(mascotaRepository.findById(1L)).thenReturn(Optional.of(mascota));
            when(veterinarioRepository.findById(1L)).thenReturn(Optional.of(veterinario));
            when(estrategiaDisponibilidad.estaDisponible(any(), any(), any(), any(), any()))
                    .thenReturn(true);
            when(citaRepository.save(any(Cita.class))).thenAnswer(invocacion -> {
                Cita cita = invocacion.getArgument(0);
                cita.setId(100L);
                return cita;
            });

            Cita resultado = citaService.agendar(citaDto);

            assertThat(resultado.getId()).isEqualTo(100L);
            assertThat(resultado.getEstado()).isEqualTo(Cita.EstadoCita.AGENDADA);
            assertThat(resultado.getMascota()).isEqualTo(mascota);
            assertThat(resultado.getVeterinario()).isEqualTo(veterinario);

            verify(citaRepository, times(1)).save(any(Cita.class));
        }

        @Test
        @DisplayName("rechaza la cita cuando el veterinario NO está disponible (solapamiento)")
        void rechazaCuandoHaySolapamiento() {
            when(mascotaRepository.findById(1L)).thenReturn(Optional.of(mascota));
            when(veterinarioRepository.findById(1L)).thenReturn(Optional.of(veterinario));
            when(estrategiaDisponibilidad.estaDisponible(any(), any(), any(), any(), any()))
                    .thenReturn(false);

            assertThatThrownBy(() -> citaService.agendar(citaDto))
                    .isInstanceOf(ReglaDeNegocioException.class)
                    .hasMessageContaining("ya tiene una cita agendada");

            verify(citaRepository, never()).save(any(Cita.class));
        }

        @Test
        @DisplayName("rechaza la cita cuando la hora de inicio no es anterior a la hora de fin")
        void rechazaCuandoElRangoHorarioEsInvalido() {
            citaDto.setHoraInicio(LocalTime.of(11, 0));
            citaDto.setHoraFin(LocalTime.of(10, 0));

            when(mascotaRepository.findById(1L)).thenReturn(Optional.of(mascota));
            when(veterinarioRepository.findById(1L)).thenReturn(Optional.of(veterinario));
            when(estrategiaDisponibilidad.estaDisponible(any(), any(), any(), any(), any()))
                    .thenReturn(true);

            assertThatThrownBy(() -> citaService.agendar(citaDto))
                    .isInstanceOf(ReglaDeNegocioException.class)
                    .hasMessageContaining("hora de inicio debe ser anterior");
        }

        @Test
        @DisplayName("lanza excepción cuando la mascota no existe")
        void lanzaExcepcionCuandoMascotaNoExiste() {
            when(mascotaRepository.findById(1L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> citaService.agendar(citaDto))
                    .isInstanceOf(RecursoNoEncontradoException.class);

            verify(citaRepository, never()).save(any(Cita.class));
        }

        @Test
        @DisplayName("publica un evento de cambio de estado al agendar")
        void publicaEventoAlAgendar() {
            when(mascotaRepository.findById(1L)).thenReturn(Optional.of(mascota));
            when(veterinarioRepository.findById(1L)).thenReturn(Optional.of(veterinario));
            when(estrategiaDisponibilidad.estaDisponible(any(), any(), any(), any(), any()))
                    .thenReturn(true);
            when(citaRepository.save(any(Cita.class))).thenAnswer(invocacion -> invocacion.getArgument(0));

            citaService.agendar(citaDto);

            ArgumentCaptor<CambioEstadoCitaEvent> captor = ArgumentCaptor.forClass(CambioEstadoCitaEvent.class);
            verify(eventPublisher, times(1)).publishEvent(captor.capture());
            assertThat(captor.getValue().getEstadoNuevo()).isEqualTo(Cita.EstadoCita.AGENDADA);
        }
    }

    @Nested
    @DisplayName("al cambiar el estado de una cita")
    class CambioDeEstado {

        @Test
        @DisplayName("confirma una cita agendada correctamente")
        void confirmaCitaAgendada() {
            Cita cita = new Cita(mascota, veterinario, LocalDate.now().plusDays(1),
                    LocalTime.of(9, 0), LocalTime.of(9, 30), "Vacunación");
            cita.setId(5L);
            cita.setEstado(Cita.EstadoCita.AGENDADA);

            when(citaRepository.findById(5L)).thenReturn(Optional.of(cita));
            when(citaRepository.save(any(Cita.class))).thenAnswer(inv -> inv.getArgument(0));

            Cita resultado = citaService.confirmar(5L);

            assertThat(resultado.getEstado()).isEqualTo(Cita.EstadoCita.CONFIRMADA);
        }

        @Test
        @DisplayName("no permite modificar una cita ya cancelada")
        void noPermiteModificarCitaCancelada() {
            Cita cita = new Cita(mascota, veterinario, LocalDate.now().plusDays(1),
                    LocalTime.of(9, 0), LocalTime.of(9, 30), "Vacunación");
            cita.setId(5L);
            cita.setEstado(Cita.EstadoCita.CANCELADA);

            when(citaRepository.findById(5L)).thenReturn(Optional.of(cita));

            assertThatThrownBy(() -> citaService.confirmar(5L))
                    .isInstanceOf(ReglaDeNegocioException.class)
                    .hasMessageContaining("cancelada");

            verify(citaRepository, never()).save(any(Cita.class));
        }

        @Test
        @DisplayName("no permite cancelar una cita ya completada")
        void noPermiteCancelarCitaCompletada() {
            Cita cita = new Cita(mascota, veterinario, LocalDate.now().plusDays(1),
                    LocalTime.of(9, 0), LocalTime.of(9, 30), "Vacunación");
            cita.setId(5L);
            cita.setEstado(Cita.EstadoCita.COMPLETADA);

            when(citaRepository.findById(5L)).thenReturn(Optional.of(cita));

            assertThatThrownBy(() -> citaService.cancelar(5L, "Ya no se requiere"))
                    .isInstanceOf(ReglaDeNegocioException.class)
                    .hasMessageContaining("completada");
        }

        @Test
        @DisplayName("cancela una cita agendada y registra el motivo")
        void cancelaCitaConMotivo() {
            Cita cita = new Cita(mascota, veterinario, LocalDate.now().plusDays(1),
                    LocalTime.of(9, 0), LocalTime.of(9, 30), "Vacunación");
            cita.setId(5L);
            cita.setEstado(Cita.EstadoCita.AGENDADA);

            when(citaRepository.findById(5L)).thenReturn(Optional.of(cita));
            when(citaRepository.save(any(Cita.class))).thenAnswer(inv -> inv.getArgument(0));

            Cita resultado = citaService.cancelar(5L, "El propietario reprogramará");

            assertThat(resultado.getEstado()).isEqualTo(Cita.EstadoCita.CANCELADA);
            assertThat(resultado.getMotivo()).contains("El propietario reprogramará");
        }
    }

    @Test
    @DisplayName("buscarPorRangoDeFechas delega correctamente al repositorio")
    void buscarPorRangoDeFechasDelegaAlRepositorio() {
        LocalDate inicio = LocalDate.now();
        LocalDate fin = inicio.plusDays(7);

        citaService.buscarPorRangoDeFechas(inicio, fin);

        verify(citaRepository, times(1)).findByFechaCitaBetween(inicio, fin);
    }
}
