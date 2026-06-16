package com.veterinariaplus.app.servicio.impl;

import com.veterinariaplus.app.excepcion.RecursoNoEncontradoException;
import com.veterinariaplus.app.excepcion.ReglaDeNegocioException;
import com.veterinariaplus.app.modelo.Medicamento;
import com.veterinariaplus.app.repositorio.MedicamentoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.ArgumentMatchers.any;

/**
 * Pruebas unitarias de MedicamentoServiceImpl, enfocadas en la lógica
 * de control de stock (RF-05, RF-12).
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("MedicamentoServiceImpl")
class MedicamentoServiceImplTest {

    @Mock
    private MedicamentoRepository medicamentoRepository;

    @InjectMocks
    private MedicamentoServiceImpl medicamentoService;

    private Medicamento medicamento;

    @BeforeEach
    void setUp() {
        medicamento = new Medicamento("Amoxicilina", "Amoxicilina trihidratada", 20, 5);
        medicamento.setId(1L);
    }

    @Test
    @DisplayName("descuenta stock correctamente cuando hay suficiente disponible")
    void descuentaStockConDisponibilidadSuficiente() {
        when(medicamentoRepository.findById(1L)).thenReturn(Optional.of(medicamento));
        when(medicamentoRepository.save(any(Medicamento.class))).thenAnswer(inv -> inv.getArgument(0));

        Medicamento resultado = medicamentoService.descontarStock(1L, 5);

        assertThat(resultado.getStock()).isEqualTo(15);
    }

    @Test
    @DisplayName("lanza excepción al intentar descontar más stock del disponible")
    void lanzaExcepcionCuandoStockEsInsuficiente() {
        when(medicamentoRepository.findById(1L)).thenReturn(Optional.of(medicamento));

        assertThatThrownBy(() -> medicamentoService.descontarStock(1L, 100))
                .isInstanceOf(ReglaDeNegocioException.class)
                .hasMessageContaining("Stock insuficiente");

        verify(medicamentoRepository, never()).save(any(Medicamento.class));
    }

    @Test
    @DisplayName("lanza excepción al intentar descontar una cantidad negativa o cero")
    void lanzaExcepcionConCantidadInvalida() {
        assertThatThrownBy(() -> medicamentoService.descontarStock(1L, 0))
                .isInstanceOf(ReglaDeNegocioException.class);

        assertThatThrownBy(() -> medicamentoService.descontarStock(1L, -3))
                .isInstanceOf(ReglaDeNegocioException.class);
    }

    @Test
    @DisplayName("lanza excepción cuando el medicamento no existe")
    void lanzaExcepcionCuandoMedicamentoNoExiste() {
        when(medicamentoRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> medicamentoService.descontarStock(99L, 1))
                .isInstanceOf(RecursoNoEncontradoException.class);
    }

    @Test
    @DisplayName("incrementa el stock correctamente")
    void incrementaStockCorrectamente() {
        when(medicamentoRepository.findById(1L)).thenReturn(Optional.of(medicamento));
        when(medicamentoRepository.save(any(Medicamento.class))).thenAnswer(inv -> inv.getArgument(0));

        Medicamento resultado = medicamentoService.incrementarStock(1L, 10);

        assertThat(resultado.getStock()).isEqualTo(30);
    }

    @Test
    @DisplayName("isStockBajo detecta correctamente cuando el stock está por debajo del mínimo")
    void detectaStockBajoCorrectamente() {
        Medicamento medicamentoConStockBajo = new Medicamento("Ibuprofeno", "Ibuprofeno", 2, 5);
        assertThat(medicamentoConStockBajo.isStockBajo()).isTrue();

        Medicamento medicamentoConStockSuficiente = new Medicamento("Paracetamol", "Paracetamol", 50, 5);
        assertThat(medicamentoConStockSuficiente.isStockBajo()).isFalse();
    }
}
