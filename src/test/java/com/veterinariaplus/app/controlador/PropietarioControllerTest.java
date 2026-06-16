package com.veterinariaplus.app.controlador;

import com.veterinariaplus.app.modelo.Propietario;
import com.veterinariaplus.app.servicio.PropietarioService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

/**
 * Pruebas de la capa Vista-Controlador para PropietarioController,
 * usando MockMvc para simular peticiones HTTP sin levantar un
 * servidor real (Spring MockMvc, mencionado en el stack tecnológico).
 *
 * Se importa NavegacionControllerAdvice explícitamente porque las
 * vistas reales (Thymeleaf) dependen del atributo de modelo
 * "rutaActual" que provee ese advice para el selector de idioma;
 * sin importarlo, el renderizado de la vista fallaría en estas
 * pruebas aunque funcione correctamente en la aplicación real.
 */
@WebMvcTest(PropietarioController.class)
@Import(NavegacionControllerAdvice.class)
@DisplayName("PropietarioController")
class PropietarioControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PropietarioService propietarioService;

    @Test
    @DisplayName("GET /propietarios muestra el listado correctamente")
    void listarPropietarios() throws Exception {
        Propietario propietario = new Propietario("Ana", "Pérez", "5551234567", "ana@example.com", "Calle 1");
        when(propietarioService.buscar(any())).thenReturn(List.of(propietario));

        mockMvc.perform(get("/propietarios"))
                .andExpect(status().isOk())
                .andExpect(view().name("propietarios/listado"))
                .andExpect(model().attributeExists("propietarios"));
    }

    @Test
    @DisplayName("GET /propietarios/nuevo muestra el formulario vacío")
    void mostrarFormularioNuevo() throws Exception {
        mockMvc.perform(get("/propietarios/nuevo"))
                .andExpect(status().isOk())
                .andExpect(view().name("propietarios/formulario"))
                .andExpect(model().attributeExists("propietario"));
    }

    @Test
    @DisplayName("POST /propietarios/guardar con datos válidos redirige al listado")
    void guardarPropietarioValido() throws Exception {
        mockMvc.perform(post("/propietarios/guardar")
                        .param("nombre", "Ana")
                        .param("apellido", "Pérez")
                        .param("telefono", "5551234567")
                        .param("email", "ana@example.com")
                        .param("direccion", "Calle 1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/propietarios"));

        verify(propietarioService).guardar(any(Propietario.class));
    }

    @Test
    @DisplayName("POST /propietarios/guardar con email inválido vuelve al formulario con errores")
    void guardarPropietarioConEmailInvalidoMuestraErrores() throws Exception {
        mockMvc.perform(post("/propietarios/guardar")
                        .param("nombre", "Ana")
                        .param("apellido", "Pérez")
                        .param("telefono", "5551234567")
                        .param("email", "correo-invalido")
                        .param("direccion", "Calle 1"))
                .andExpect(status().isOk())
                .andExpect(view().name("propietarios/formulario"))
                .andExpect(model().attributeHasFieldErrors("propietario", "email"));
    }

    @Test
    @DisplayName("POST /propietarios/{id}/eliminar redirige al listado")
    void eliminarPropietario() throws Exception {
        mockMvc.perform(post("/propietarios/1/eliminar"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/propietarios"));

        verify(propietarioService).eliminar(1L);
    }
}
