package com.veterinariaplus.app.servicio.impl;

import com.veterinariaplus.app.excepcion.RecursoNoEncontradoException;
import com.veterinariaplus.app.excepcion.ReglaDeNegocioException;
import com.veterinariaplus.app.modelo.Especialidad;
import com.veterinariaplus.app.modelo.Veterinario;
import com.veterinariaplus.app.repositorio.VeterinarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

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
 * Pruebas unitarias de VeterinarioServiceImpl, enfocadas en la
 * validación de cédula profesional única y en la baja lógica
 * (en lugar de eliminación física) usada para preservar el
 * historial de citas.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("VeterinarioServiceImpl")
class VeterinarioServiceImplTest {

    @Mock
    private VeterinarioRepository veterinarioRepository;

    @InjectMocks
    private VeterinarioServiceImpl veterinarioService;

    private Veterinario veterinario;

    @BeforeEach
    void setUp() {
        Especialidad especialidad = new Especialidad("Cirugía", "Procedimientos quirúrgicos");
        especialidad.setId(1L);
        veterinario = new Veterinario("Carlos", "Gómez", "VET-001", especialidad);
        veterinario.setId(1L);
    }

    @Test
    @DisplayName("guarda correctamente un veterinario con cédula única")
    void guardaVeterinarioConCedulaUnica() {
        when(veterinarioRepository.findByCedulaProfesional("VET-001")).thenReturn(Optional.empty());
        when(veterinarioRepository.save(any(Veterinario.class))).thenReturn(veterinario);

        Veterinario resultado = veterinarioService.guardar(veterinario);

        assertThat(resultado).isEqualTo(veterinario);
    }

    @Test
    @DisplayName("rechaza el guardado cuando la cédula ya está registrada por otro veterinario")
    void rechazaCedulaDuplicada() {
        Especialidad especialidad = new Especialidad("Dermatología", "Piel y pelaje");
        especialidad.setId(2L);
        Veterinario otroExistente = new Veterinario("Laura", "Méndez", "VET-001", especialidad);
        otroExistente.setId(2L);

        when(veterinarioRepository.findByCedulaProfesional("VET-001"))
                .thenReturn(Optional.of(otroExistente));

        assertThatThrownBy(() -> veterinarioService.guardar(veterinario))
                .isInstanceOf(ReglaDeNegocioException.class)
                .hasMessageContaining("Ya existe un veterinario");

        verify(veterinarioRepository, never()).save(any());
    }

    @Test
    @DisplayName("permite actualizar el propio veterinario sin disparar conflicto de cédula")
    void permiteActualizarElMismoVeterinario() {
        when(veterinarioRepository.findByCedulaProfesional("VET-001")).thenReturn(Optional.of(veterinario));
        when(veterinarioRepository.save(any(Veterinario.class))).thenReturn(veterinario);

        Veterinario resultado = veterinarioService.guardar(veterinario);

        assertThat(resultado).isEqualTo(veterinario);
    }

    @Test
    @DisplayName("buscarActivos delega en el repositorio")
    void buscarActivosDelegaEnRepositorio() {
        when(veterinarioRepository.findByActivoTrue()).thenReturn(List.of(veterinario));

        List<Veterinario> resultado = veterinarioService.buscarActivos();

        assertThat(resultado).containsExactly(veterinario);
    }

    @Test
    @DisplayName("buscarTodos delega en el repositorio")
    void buscarTodosDelegaEnRepositorio() {
        when(veterinarioRepository.findAll()).thenReturn(List.of(veterinario));

        List<Veterinario> resultado = veterinarioService.buscarTodos();

        assertThat(resultado).containsExactly(veterinario);
    }

    @Test
    @DisplayName("buscarPorId delega en el repositorio")
    void buscarPorIdDelegaEnRepositorio() {
        when(veterinarioRepository.findById(1L)).thenReturn(Optional.of(veterinario));

        Optional<Veterinario> resultado = veterinarioService.buscarPorId(1L);

        assertThat(resultado).contains(veterinario);
    }

    @Test
    @DisplayName("eliminar realiza baja lógica en lugar de eliminación física")
    void eliminarRealizaBajaLogica() {
        veterinario.setActivo(true);
        when(veterinarioRepository.findById(1L)).thenReturn(Optional.of(veterinario));
        when(veterinarioRepository.save(any(Veterinario.class))).thenAnswer(inv -> inv.getArgument(0));

        veterinarioService.eliminar(1L);

        assertThat(veterinario.isActivo()).isFalse();
        verify(veterinarioRepository, times(1)).save(veterinario);
        verify(veterinarioRepository, never()).deleteById(any());
    }

    @Test
    @DisplayName("lanza excepción al eliminar un veterinario que no existe")
    void lanzaExcepcionAlEliminarVeterinarioInexistente() {
        when(veterinarioRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> veterinarioService.eliminar(99L))
                .isInstanceOf(RecursoNoEncontradoException.class);
    }
}
