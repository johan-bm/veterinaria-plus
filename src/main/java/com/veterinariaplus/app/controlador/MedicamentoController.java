package com.veterinariaplus.app.controlador;

import com.veterinariaplus.app.modelo.Medicamento;
import com.veterinariaplus.app.servicio.MedicamentoService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * Controlador para la gestión del catálogo de medicamentos (RF-05, RF-12).
 */
@Controller
@RequestMapping("/medicamentos")
public class MedicamentoController {

    private final MedicamentoService medicamentoService;

    public MedicamentoController(MedicamentoService medicamentoService) {
        this.medicamentoService = medicamentoService;
    }

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("medicamentos", medicamentoService.buscarTodos());
        return "medicamentos/listado";
    }

    @GetMapping("/nuevo")
    public String formularioNuevo(Model model) {
        model.addAttribute("medicamento", new Medicamento());
        return "medicamentos/formulario";
    }

    @GetMapping("/{id}/editar")
    public String formularioEditar(@PathVariable Long id, Model model, RedirectAttributes redirect) {
        return medicamentoService.buscarPorId(id)
                .map(medicamento -> {
                    model.addAttribute("medicamento", medicamento);
                    return "medicamentos/formulario";
                })
                .orElseGet(() -> {
                    redirect.addFlashAttribute("mensajeError", "error.noEncontrado");
                    return "redirect:/medicamentos";
                });
    }

    @PostMapping("/guardar")
    public String guardar(@Valid @ModelAttribute("medicamento") Medicamento medicamento,
                           BindingResult bindingResult, RedirectAttributes redirect) {
        if (bindingResult.hasErrors()) {
            return "medicamentos/formulario";
        }
        medicamentoService.guardar(medicamento);
        redirect.addFlashAttribute("mensajeExito", "medicamento.guardado.exito");
        return "redirect:/medicamentos";
    }

    @PostMapping("/{id}/incrementar-stock")
    public String incrementarStock(@PathVariable Long id, @RequestParam int cantidad,
                                    RedirectAttributes redirect) {
        medicamentoService.incrementarStock(id, cantidad);
        redirect.addFlashAttribute("mensajeExito", "medicamento.guardado.exito");
        return "redirect:/medicamentos";
    }

    @PostMapping("/{id}/eliminar")
    public String eliminar(@PathVariable Long id, RedirectAttributes redirect) {
        medicamentoService.eliminar(id);
        redirect.addFlashAttribute("mensajeExito", "medicamento.eliminado.exito");
        return "redirect:/medicamentos";
    }
}
