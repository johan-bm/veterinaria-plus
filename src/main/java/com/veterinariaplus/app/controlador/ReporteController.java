package com.veterinariaplus.app.controlador;

import com.veterinariaplus.app.servicio.ReporteService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;

/**
 * Controlador para la generación de reportes (RF-09).
 */
@Controller
@RequestMapping("/reportes")
public class ReporteController {

    private final ReporteService reporteService;

    public ReporteController(ReporteService reporteService) {
        this.reporteService = reporteService;
    }

    @GetMapping
    public String formulario(Model model) {
        model.addAttribute("fechaInicio", LocalDate.now().withDayOfMonth(1));
        model.addAttribute("fechaFin", LocalDate.now());
        return "reportes/formulario";
    }

    @GetMapping("/citas-por-periodo")
    public String citasPorPeriodo(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaInicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaFin,
            Model model) {

        model.addAttribute("citas", reporteService.citasPorPeriodo(fechaInicio, fechaFin));
        model.addAttribute("estadisticas", reporteService.estadisticasPorEstado(fechaInicio, fechaFin));
        model.addAttribute("fechaInicio", fechaInicio);
        model.addAttribute("fechaFin", fechaFin);
        return "reportes/resultado";
    }
}
