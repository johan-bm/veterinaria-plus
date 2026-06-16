package com.veterinariaplus.app.controlador;

import com.veterinariaplus.app.servicio.MedicamentoService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Controlador de la página de inicio.
 */
@Controller
public class InicioController {

    private final MedicamentoService medicamentoService;

    public InicioController(MedicamentoService medicamentoService) {
        this.medicamentoService = medicamentoService;
    }

    @GetMapping("/")
    public String inicio(Model model) {
        model.addAttribute("medicamentosStockBajo", medicamentoService.buscarConStockBajo());
        return "inicio";
    }
}
