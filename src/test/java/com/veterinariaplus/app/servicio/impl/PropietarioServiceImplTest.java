package com.veterinariaplus.app.servicio.impl;

import com.veterinariaplus.app.excepcion.ReglaDeNegocioException;
import com.veterinariaplus.app.modelo.Propietario;
import com.veterinariaplus.app.repositorio.PropietarioRepository;
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
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Pruebas unitarias de PropietarioServiceImpl, enfocadas en la regla
 * de negocio de correo electrónico único.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("PropietarioServiceImpl")
class PropietarioServiceImplTest {

    @Mock
    private PropietarioRepository propietarioRepository;

    @InjectMocks
    private PropietarioServiceImpl propietarioService;

    private Propietario propietario;

    @BeforeEach
    void setUp() {
        propietario = new Propietario("Ana", "Pérez", "5551234567", "ana@example.com", "Calle 1");
        propietario.setId(1L);
    }

    @Test
    @DisplayName("guarda correctamente un propietario con correo único")
    void guardaPropietarioConCorreoUnico() {
        when(propietarioRepository.findByEmailIgnoreCase("ana@example.com")).thenReturn(Optional.empty());
        when(propietarioRepository.save(any(Propietario.class))).thenReturn(propietario);

        Propietario resultado = propietarioService.guardar(propietario);

        assertThat(resultado).isEqualTo(propietario);
    }

    @Test
    @DisplayName("rechaza el guardado cuando el correo ya está registrado por otro propietario")
    void rechazaCorreoDuplicado() {
        Propietario otroExistente = new Propietario("Luis", "Gómez", "5559876543", "ana@example.com", "Calle 2");
        otroExistente.setId(2L);

        when(propietarioRepository.findByEmailIgnoreCase("ana@example.com"))
                .thenReturn(Optional.of(otroExistente));

        assertThatThrownBy(() -> propietarioService.guardar(propietario))
                .isInstanceOf(ReglaDeNegocioException.class)
                .hasMessageContaining("Ya existe un propietario");

        verify(propietarioRepository, never()).save(any(Propietario.class));
    }

    @Test
    @DisplayName("permite actualizar el propio propietario sin disparar el conflicto de correo duplicado")
    void permiteActualizarElMismoPropietario() {
        when(propietarioRepository.findByEmailIgnoreCase("ana@example.com")).thenReturn(Optional.of(propietario));
        when(propietarioRepository.save(any(Propietario.class))).thenReturn(propietario);

        Propietario resultado = propietarioService.guardar(propietario);

        assertThat(resultado).isEqualTo(propietario);
    }

    @Test
    @DisplayName("buscar() devuelve todos los propietarios cuando el texto de búsqueda está vacío")
    void buscarSinTextoDevuelveTodos() {
        when(propietarioRepository.findAll()).thenReturn(List.of(propietario));

        List<Propietario> resultado = propietarioService.buscar("");

        assertThat(resultado).containsExactly(propietario);
        verify(propietarioRepository, never()).buscarPorNombreOApellido(any());
    }

    @Test
    @DisplayName("buscar() delega al repositorio cuando se provee un texto")
    void buscarConTextoDelegaAlRepositorio() {
        when(propietarioRepository.buscarPorNombreOApellido("Ana")).thenReturn(List.of(propietario));

        List<Propietario> resultado = propietarioService.buscar("Ana");

        assertThat(resultado).containsExactly(propietario);
    }
}
