package com.veterinariaplus.app.controlador;

import com.veterinariaplus.app.dto.MascotaDTO;
import com.veterinariaplus.app.modelo.Mascota;
import com.veterinariaplus.app.servicio.EspecieService;
import com.veterinariaplus.app.servicio.MascotaService;
import com.veterinariaplus.app.servicio.PropietarioService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * Controlador para la gestión de mascotas (RF-01, RF-06, CU-01, CU-04).
 */
@Controller
@RequestMapping("/mascotas")
public class MascotaController {

    private final MascotaService mascotaService;
    private final EspecieService especieService;
    private final PropietarioService propietarioService;

    public MascotaController(MascotaService mascotaService, EspecieService especieService,
                              PropietarioService propietarioService) {
        this.mascotaService = mascotaService;
        this.especieService = especieService;
        this.propietarioService = propietarioService;
    }

    @GetMapping
    public String listar(@RequestParam(required = false) String texto, Model model) {
        model.addAttribute("mascotas", mascotaService.buscar(texto));
        model.addAttribute("textoBusqueda", texto);
        return "mascotas/listado";
    }

    @GetMapping("/nuevo")
    public String formularioNuevo(Model model) {
        model.addAttribute("mascotaDto", new MascotaDTO());
        cargarListasParaFormulario(model);
        return "mascotas/formulario";
    }

    @GetMapping("/{id}/editar")
    public String formularioEditar(@PathVariable Long id, Model model, RedirectAttributes redirect) {
        return mascotaService.buscarPorId(id)
                .map(mascota -> {
                    model.addAttribute("mascotaDto", aDto(mascota));
                    cargarListasParaFormulario(model);
                    return "mascotas/formulario";
                })
                .orElseGet(() -> {
                    redirect.addFlashAttribute("mensajeError", "error.noEncontrado");
                    return "redirect:/mascotas";
                });
    }

    @PostMapping("/guardar")
    public String guardar(@Valid @ModelAttribute("mascotaDto") MascotaDTO dto,
                           BindingResult bindingResult, Model model, RedirectAttributes redirect) {
        if (bindingResult.hasErrors()) {
            cargarListasParaFormulario(model);
            return "mascotas/formulario";
        }
        mascotaService.guardarDesdeDto(dto);
        redirect.addFlashAttribute("mensajeExito", "mascota.guardado.exito");
        return "redirect:/mascotas";
    }

    @PostMapping("/{id}/eliminar")
    public String eliminar(@PathVariable Long id, RedirectAttributes redirect) {
        mascotaService.eliminar(id);
        redirect.addFlashAttribute("mensajeExito", "mascota.eliminado.exito");
        return "redirect:/mascotas";
    }

    private void cargarListasParaFormulario(Model model) {
        model.addAttribute("especies", especieService.buscarTodos());
        model.addAttribute("propietarios", propietarioService.buscarTodos());
        model.addAttribute("sexos", Mascota.Sexo.values());
    }

    private MascotaDTO aDto(Mascota mascota) {
        MascotaDTO dto = new MascotaDTO();
        dto.setId(mascota.getId());
        dto.setNombre(mascota.getNombre());
        dto.setEspecieId(mascota.getEspecie().getId());
        dto.setRaza(mascota.getRaza());
        dto.setFechaNacimiento(mascota.getFechaNacimiento());
        dto.setSexo(mascota.getSexo());
        dto.setPropietarioId(mascota.getPropietario().getId());
        return dto;
    }
}
