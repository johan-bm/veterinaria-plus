package com.veterinariaplus.app.servicio.impl;

import com.veterinariaplus.app.excepcion.RecursoNoEncontradoException;
import com.veterinariaplus.app.modelo.Especie;
import com.veterinariaplus.app.repositorio.EspecieRepository;
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
 * Pruebas unitarias de EspecieServiceImpl, el servicio CRUD básico
 * del catálogo de especies.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("EspecieServiceImpl")
class EspecieServiceImplTest {

    @Mock
    private EspecieRepository especieRepository;

    @InjectMocks
    private EspecieServiceImpl especieService;

    private Especie especie;

    @BeforeEach
    void setUp() {
        especie = new Especie("Canino", "Perro doméstico");
        especie.setId(1L);
    }

    @Test
    @DisplayName("guarda correctamente una especie")
    void guardaEspecie() {
        when(especieRepository.save(any(Especie.class))).thenReturn(especie);

        Especie resultado = especieService.guardar(especie);

        assertThat(resultado).isEqualTo(especie);
        verify(especieRepository, times(1)).save(especie);
    }

    @Test
    @DisplayName("buscarPorId delega en el repositorio")
    void buscarPorIdDelegaEnRepositorio() {
        when(especieRepository.findById(1L)).thenReturn(Optional.of(especie));

        Optional<Especie> resultado = especieService.buscarPorId(1L);

        assertThat(resultado).contains(especie);
    }

    @Test
    @DisplayName("buscarTodos delega en el repositorio")
    void buscarTodosDelegaEnRepositorio() {
        when(especieRepository.findAll()).thenReturn(List.of(especie));

        List<Especie> resultado = especieService.buscarTodos();

        assertThat(resultado).containsExactly(especie);
    }

    @Test
    @DisplayName("elimina correctamente una especie existente")
    void eliminaEspecieExistente() {
        when(especieRepository.existsById(1L)).thenReturn(true);

        especieService.eliminar(1L);

        verify(especieRepository, times(1)).deleteById(1L);
    }

    @Test
    @DisplayName("lanza excepción al eliminar una especie inexistente")
    void lanzaExcepcionAlEliminarEspecieInexistente() {
        when(especieRepository.existsById(99L)).thenReturn(false);

        assertThatThrownBy(() -> especieService.eliminar(99L))
                .isInstanceOf(RecursoNoEncontradoException.class);

        verify(especieRepository, never()).deleteById(any());
    }
}
