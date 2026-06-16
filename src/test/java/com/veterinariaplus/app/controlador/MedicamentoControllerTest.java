package com.veterinariaplus.app.controlador;

import com.veterinariaplus.app.modelo.Medicamento;
import com.veterinariaplus.app.servicio.MedicamentoService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

/**
 * Pruebas de la capa Vista-Controlador para MedicamentoController.
 *
 * Se importa NavegacionControllerAdvice por la misma razón explicada
 * en PropietarioControllerTest: las vistas reales dependen del
 * atributo "rutaActual" para el selector de idioma.
 */
@WebMvcTest(MedicamentoController.class)
@Import(NavegacionControllerAdvice.class)
@DisplayName("MedicamentoController")
class MedicamentoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private MedicamentoService medicamentoService;

    @Test
    @DisplayName("GET /medicamentos muestra el listado correctamente")
    void listarMedicamentos() throws Exception {
        Medicamento medicamento = new Medicamento("Amoxicilina", "Amoxicilina trihidratada", 20, 5);
        when(medicamentoService.buscarTodos()).thenReturn(List.of(medicamento));

        mockMvc.perform(get("/medicamentos"))
                .andExpect(status().isOk())
                .andExpect(view().name("medicamentos/listado"))
                .andExpect(model().attributeExists("medicamentos"));
    }

    @Test
    @DisplayName("GET /medicamentos/nuevo muestra el formulario vacío")
    void mostrarFormularioNuevo() throws Exception {
        mockMvc.perform(get("/medicamentos/nuevo"))
                .andExpect(status().isOk())
                .andExpect(view().name("medicamentos/formulario"))
                .andExpect(model().attributeExists("medicamento"));
    }

    @Test
    @DisplayName("GET /medicamentos/{id}/editar muestra el formulario con datos cuando el medicamento existe")
    void mostrarFormularioEditarConMedicamentoExistente() throws Exception {
        Medicamento medicamento = new Medicamento("Amoxicilina", "Amoxicilina trihidratada", 20, 5);
        medicamento.setId(1L);
        when(medicamentoService.buscarPorId(1L)).thenReturn(Optional.of(medicamento));

        mockMvc.perform(get("/medicamentos/1/editar"))
                .andExpect(status().isOk())
                .andExpect(view().name("medicamentos/formulario"))
                .andExpect(model().attributeExists("medicamento"));
    }

    @Test
    @DisplayName("GET /medicamentos/{id}/editar redirige al listado cuando el medicamento no existe")
    void redirigeCuandoMedicamentoNoExisteAlEditar() throws Exception {
        when(medicamentoService.buscarPorId(99L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/medicamentos/99/editar"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/medicamentos"));
    }

    @Test
    @DisplayName("POST /medicamentos/guardar con datos válidos redirige al listado")
    void guardarMedicamentoValido() throws Exception {
        mockMvc.perform(post("/medicamentos/guardar")
                        .param("nombre", "Amoxicilina")
                        .param("principioActivo", "Amoxicilina trihidratada")
                        .param("stock", "20")
                        .param("stockMinimo", "5"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/medicamentos"));

        verify(medicamentoService).guardar(any(Medicamento.class));
    }

    @Test
    @DisplayName("POST /medicamentos/guardar con nombre vacío vuelve al formulario con errores")
    void guardarMedicamentoConNombreVacioMuestraErrores() throws Exception {
        mockMvc.perform(post("/medicamentos/guardar")
                        .param("nombre", "")
                        .param("principioActivo", "Amoxicilina trihidratada")
                        .param("stock", "20")
                        .param("stockMinimo", "5"))
                .andExpect(status().isOk())
                .andExpect(view().name("medicamentos/formulario"));
    }

    @Test
    @DisplayName("POST /medicamentos/{id}/incrementar-stock redirige al listado")
    void incrementarStockRedirigeAlListado() throws Exception {
        mockMvc.perform(post("/medicamentos/1/incrementar-stock")
                        .param("cantidad", "10"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/medicamentos"));

        verify(medicamentoService).incrementarStock(eq(1L), eq(10));
    }

    @Test
    @DisplayName("POST /medicamentos/{id}/eliminar redirige al listado")
    void eliminarMedicamentoRedirigeAlListado() throws Exception {
        mockMvc.perform(post("/medicamentos/1/eliminar"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/medicamentos"));

        verify(medicamentoService).eliminar(1L);
    }
}
