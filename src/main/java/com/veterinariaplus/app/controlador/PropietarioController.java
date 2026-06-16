package com.veterinariaplus.app.controlador;

import com.veterinariaplus.app.modelo.Propietario;
import com.veterinariaplus.app.servicio.PropietarioService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * Controlador para la gestión de propietarios (CU-01 relacionado,
 * RF-02). Implementa el CRUD completo con validaciones de formulario.
 */
@Controller
@RequestMapping("/propietarios")
public class PropietarioController {

    private final PropietarioService propietarioService;

    public PropietarioController(PropietarioService propietarioService) {
        this.propietarioService = propietarioService;
    }

    @GetMapping
    public String listar(@RequestParam(required = false) String texto, Model model) {
        model.addAttribute("propietarios", propietarioService.buscar(texto));
        model.addAttribute("textoBusqueda", texto);
        return "propietarios/listado";
    }

    @GetMapping("/nuevo")
    public String formularioNuevo(Model model) {
        model.addAttribute("propietario", new Propietario());
        return "propietarios/formulario";
    }

    @GetMapping("/{id}/editar")
    public String formularioEditar(@PathVariable Long id, Model model, RedirectAttributes redirect) {
        return propietarioService.buscarPorId(id)
                .map(propietario -> {
                    model.addAttribute("propietario", propietario);
                    return "propietarios/formulario";
                })
                .orElseGet(() -> {
                    redirect.addFlashAttribute("mensajeError", "error.noEncontrado");
                    return "redirect:/propietarios";
                });
    }

    @PostMapping("/guardar")
    public String guardar(@Valid @ModelAttribute("propietario") Propietario propietario,
                           BindingResult bindingResult, RedirectAttributes redirect) {
        if (bindingResult.hasErrors()) {
            return "propietarios/formulario";
        }
        propietarioService.guardar(propietario);
        redirect.addFlashAttribute("mensajeExito", "propietario.guardado.exito");
        return "redirect:/propietarios";
    }

    @PostMapping("/{id}/eliminar")
    public String eliminar(@PathVariable Long id, RedirectAttributes redirect) {
        propietarioService.eliminar(id);
        redirect.addFlashAttribute("mensajeExito", "propietario.eliminado.exito");
        return "redirect:/propietarios";
    }
}
