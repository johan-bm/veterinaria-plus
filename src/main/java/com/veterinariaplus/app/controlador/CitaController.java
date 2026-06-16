package com.veterinariaplus.app.controlador;

import com.veterinariaplus.app.dto.CitaDTO;
import com.veterinariaplus.app.modelo.Cita;
import com.veterinariaplus.app.servicio.CitaService;
import com.veterinariaplus.app.servicio.MascotaService;
import com.veterinariaplus.app.servicio.VeterinarioService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * Controlador para la gestión de citas veterinarias.
 *
 * Cubre RF-03 (agendar validando disponibilidad), CU-02 (agendar),
 * CU-03 (cancelar), y el ciclo de vida completo de la cita.
 */
@Controller
@RequestMapping("/citas")
public class CitaController {

    private final CitaService citaService;
    private final MascotaService mascotaService;
    private final VeterinarioService veterinarioService;

    public CitaController(CitaService citaService, MascotaService mascotaService,
                           VeterinarioService veterinarioService) {
        this.citaService = citaService;
        this.mascotaService = mascotaService;
        this.veterinarioService = veterinarioService;
    }

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("citas", citaService.buscarTodas());
        return "citas/listado";
    }

    @GetMapping("/nuevo")
    public String formularioNuevo(Model model) {
        model.addAttribute("citaDto", new CitaDTO());
        cargarListasParaFormulario(model);
        return "citas/formulario";
    }

    @GetMapping("/{id}/editar")
    public String formularioEditar(@PathVariable Long id, Model model, RedirectAttributes redirect) {
        return citaService.buscarPorId(id)
                .map(cita -> {
                    model.addAttribute("citaDto", aDto(cita));
                    cargarListasParaFormulario(model);
                    return "citas/formulario";
                })
                .orElseGet(() -> {
                    redirect.addFlashAttribute("mensajeError", "error.noEncontrado");
                    return "redirect:/citas";
                });
    }

    @PostMapping("/guardar")
    public String guardar(@Valid @ModelAttribute("citaDto") CitaDTO dto,
                           BindingResult bindingResult, Model model, RedirectAttributes redirect) {
        if (bindingResult.hasErrors()) {
            cargarListasParaFormulario(model);
            return "citas/formulario";
        }

        if (dto.getId() != null) {
            citaService.actualizar(dto);
        } else {
            citaService.agendar(dto);
        }
        redirect.addFlashAttribute("mensajeExito", "cita.guardado.exito");
        return "redirect:/citas";
    }

    @PostMapping("/{id}/confirmar")
    public String confirmar(@PathVariable Long id, RedirectAttributes redirect) {
        citaService.confirmar(id);
        redirect.addFlashAttribute("mensajeExito", "cita.confirmado.exito");
        return "redirect:/citas";
    }

    @PostMapping("/{id}/cancelar")
    public String cancelar(@PathVariable Long id, @RequestParam(required = false) String motivo,
                            RedirectAttributes redirect) {
        citaService.cancelar(id, motivo);
        redirect.addFlashAttribute("mensajeExito", "cita.cancelado.exito");
        return "redirect:/citas";
    }

    @PostMapping("/{id}/completar")
    public String completar(@PathVariable Long id, RedirectAttributes redirect) {
        citaService.completar(id);
        redirect.addFlashAttribute("mensajeExito", "cita.completado.exito");
        return "redirect:/citas";
    }

    private void cargarListasParaFormulario(Model model) {
        model.addAttribute("mascotas", mascotaService.buscarTodos());
        model.addAttribute("veterinarios", veterinarioService.buscarActivos());
    }

    private CitaDTO aDto(Cita cita) {
        CitaDTO dto = new CitaDTO();
        dto.setId(cita.getId());
        dto.setMascotaId(cita.getMascota().getId());
        dto.setVeterinarioId(cita.getVeterinario().getId());
        dto.setFechaCita(cita.getFechaCita());
        dto.setHoraInicio(cita.getHoraInicio());
        dto.setHoraFin(cita.getHoraFin());
        dto.setMotivo(cita.getMotivo());
        return dto;
    }
}
