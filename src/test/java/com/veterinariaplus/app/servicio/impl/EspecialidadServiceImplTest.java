package com.veterinariaplus.app.servicio.impl;

import com.veterinariaplus.app.excepcion.RecursoNoEncontradoException;
import com.veterinariaplus.app.modelo.Especialidad;
import com.veterinariaplus.app.repositorio.EspecialidadRepository;
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
 * Pruebas unitarias de EspecialidadServiceImpl, el servicio CRUD
 * básico del catálogo de especialidades veterinarias.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("EspecialidadServiceImpl")
class EspecialidadServiceImplTest {

    @Mock
    private EspecialidadRepository especialidadRepository;

    @InjectMocks
    private EspecialidadServiceImpl especialidadService;

    private Especialidad especialidad;

    @BeforeEach
    void setUp() {
        especialidad = new Especialidad("Cirugía", "Procedimientos quirúrgicos");
        especialidad.setId(1L);
    }

    @Test
    @DisplayName("guarda correctamente una especialidad")
    void guardaEspecialidad() {
        when(especialidadRepository.save(any(Especialidad.class))).thenReturn(especialidad);

        Especialidad resultado = especialidadService.guardar(especialidad);

        assertThat(resultado).isEqualTo(especialidad);
        verify(especialidadRepository, times(1)).save(especialidad);
    }

    @Test
    @DisplayName("buscarPorId delega en el repositorio")
    void buscarPorIdDelegaEnRepositorio() {
        when(especialidadRepository.findById(1L)).thenReturn(Optional.of(especialidad));

        Optional<Especialidad> resultado = especialidadService.buscarPorId(1L);

        assertThat(resultado).contains(especialidad);
    }

    @Test
    @DisplayName("buscarTodos delega en el repositorio")
    void buscarTodosDelegaEnRepositorio() {
        when(especialidadRepository.findAll()).thenReturn(List.of(especialidad));

        List<Especialidad> resultado = especialidadService.buscarTodos();

        assertThat(resultado).containsExactly(especialidad);
    }

    @Test
    @DisplayName("elimina correctamente una especialidad existente")
    void eliminaEspecialidadExistente() {
        when(especialidadRepository.existsById(1L)).thenReturn(true);

        especialidadService.eliminar(1L);

        verify(especialidadRepository, times(1)).deleteById(1L);
    }

    @Test
    @DisplayName("lanza excepción al eliminar una especialidad inexistente")
    void lanzaExcepcionAlEliminarEspecialidadInexistente() {
        when(especialidadRepository.existsById(99L)).thenReturn(false);

        assertThatThrownBy(() -> especialidadService.eliminar(99L))
                .isInstanceOf(RecursoNoEncontradoException.class);

        verify(especialidadRepository, never()).deleteById(any());
    }
}
