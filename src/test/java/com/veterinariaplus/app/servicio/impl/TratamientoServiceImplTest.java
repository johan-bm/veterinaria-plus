package com.veterinariaplus.app.servicio.impl;

import com.veterinariaplus.app.excepcion.RecursoNoEncontradoException;
import com.veterinariaplus.app.modelo.Cita;
import com.veterinariaplus.app.modelo.HistorialClinico;
import com.veterinariaplus.app.modelo.Medicamento;
import com.veterinariaplus.app.modelo.Tratamiento;
import com.veterinariaplus.app.repositorio.HistorialClinicoRepository;
import com.veterinariaplus.app.repositorio.MedicamentoRepository;
import com.veterinariaplus.app.repositorio.TratamientoRepository;
import com.veterinariaplus.app.servicio.MedicamentoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Pruebas unitarias de TratamientoServiceImpl, enfocadas en el uso
 * del patrón Builder y en el descuento automático de stock de cada
 * medicamento prescrito.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("TratamientoServiceImpl")
class TratamientoServiceImplTest {

    @Mock
    private TratamientoRepository tratamientoRepository;

    @Mock
    private HistorialClinicoRepository historialClinicoRepository;

    @Mock
    private MedicamentoRepository medicamentoRepository;

    @Mock
    private MedicamentoService medicamentoService;

    @InjectMocks
    private TratamientoServiceImpl tratamientoService;

    private HistorialClinico historial;
    private Medicamento medicamento;

    @BeforeEach
    void setUp() {
        Cita cita = new Cita();
        historial = new HistorialClinico(cita, "Infección", "Infección bacteriana", null);
        historial.setId(1L);

        medicamento = new Medicamento("Amoxicilina", "Amoxicilina trihidratada", 20, 5);
        medicamento.setId(1L);
    }

    @Test
    @DisplayName("prescribe un tratamiento sin medicamentos correctamente")
    void prescribeTratamientoSinMedicamentos() {
        when(historialClinicoRepository.findById(1L)).thenReturn(Optional.of(historial));
        when(tratamientoRepository.save(any(Tratamiento.class))).thenAnswer(inv -> inv.getArgument(0));

        Tratamiento resultado = tratamientoService.prescribir(
                1L, "Observación, sin medicación", null, "Reposo", null);

        assertThat(resultado.getDescripcion()).isEqualTo("Observación, sin medicación");
        assertThat(resultado.getMedicamentos()).isEmpty();
        verify(medicamentoService, never()).descontarStock(anyLong(), anyInt());
    }

    @Test
    @DisplayName("prescribe un tratamiento con medicamentos y descuenta el stock de cada uno")
    void prescribeTratamientoConMedicamentosYDescuentaStock() {
        when(historialClinicoRepository.findById(1L)).thenReturn(Optional.of(historial));
        when(medicamentoRepository.findById(1L)).thenReturn(Optional.of(medicamento));
        when(tratamientoRepository.save(any(Tratamiento.class))).thenAnswer(inv -> inv.getArgument(0));

        Tratamiento resultado = tratamientoService.prescribir(
                1L, "Antibiótico cada 12 horas", 7, "Administrar con alimento", Set.of(1L));

        assertThat(resultado.getMedicamentos()).hasSize(1).contains(medicamento);
        verify(medicamentoService, times(1)).descontarStock(eq(1L), eq(1));
    }

    @Test
    @DisplayName("lanza excepción cuando el historial clínico no existe")
    void lanzaExcepcionCuandoHistorialNoExiste() {
        when(historialClinicoRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> tratamientoService.prescribir(
                99L, "Descripción", null, null, null))
                .isInstanceOf(RecursoNoEncontradoException.class);

        verify(tratamientoRepository, never()).save(any());
    }

    @Test
    @DisplayName("lanza excepción cuando un medicamento prescrito no existe")
    void lanzaExcepcionCuandoMedicamentoNoExiste() {
        when(historialClinicoRepository.findById(1L)).thenReturn(Optional.of(historial));
        when(medicamentoRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> tratamientoService.prescribir(
                1L, "Descripción", null, null, Set.of(99L)))
                .isInstanceOf(RecursoNoEncontradoException.class);

        verify(tratamientoRepository, never()).save(any());
    }

    @Test
    @DisplayName("buscarPorHistorial delega en el repositorio")
    void buscarPorHistorialDelegaEnRepositorio() {
        Tratamiento tratamiento = new Tratamiento.Builder()
                .historialClinico(historial)
                .descripcion("Tratamiento de prueba")
                .build();
        when(tratamientoRepository.findByHistorialClinicoId(1L)).thenReturn(List.of(tratamiento));

        List<Tratamiento> resultado = tratamientoService.buscarPorHistorial(1L);

        assertThat(resultado).containsExactly(tratamiento);
    }
}
