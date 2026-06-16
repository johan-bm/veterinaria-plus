package com.veterinariaplus.app.controlador;

import com.veterinariaplus.app.modelo.Especialidad;
import com.veterinariaplus.app.modelo.Veterinario;
import com.veterinariaplus.app.servicio.EspecialidadService;
import com.veterinariaplus.app.servicio.VeterinarioService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * Controlador para la gestión de veterinarios (RF y CU relacionados
 * con el catálogo de médicos veterinarios de la clínica).
 */
@Controller
@RequestMapping("/veterinarios")
public class VeterinarioController {

    private final VeterinarioService veterinarioService;
    private final EspecialidadService especialidadService;

    public VeterinarioController(VeterinarioService veterinarioService,
                                  EspecialidadService especialidadService) {
        this.veterinarioService = veterinarioService;
        this.especialidadService = especialidadService;
    }

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("veterinarios", veterinarioService.buscarTodos());
        return "veterinarios/listado";
    }

    @GetMapping("/nuevo")
    public String formularioNuevo(Model model) {
        model.addAttribute("veterinarioForm", new VeterinarioForm());
        model.addAttribute("especialidades", especialidadService.buscarTodos());
        return "veterinarios/formulario";
    }

    @GetMapping("/{id}/editar")
    public String formularioEditar(@PathVariable Long id, Model model, RedirectAttributes redirect) {
        return veterinarioService.buscarPorId(id)
                .map(veterinario -> {
                    model.addAttribute("veterinarioForm", VeterinarioForm.desde(veterinario));
                    model.addAttribute("especialidades", especialidadService.buscarTodos());
                    return "veterinarios/formulario";
                })
                .orElseGet(() -> {
                    redirect.addFlashAttribute("mensajeError", "error.noEncontrado");
                    return "redirect:/veterinarios";
                });
    }

    @PostMapping("/guardar")
    public String guardar(@Valid @ModelAttribute("veterinarioForm") VeterinarioForm form,
                           BindingResult bindingResult, Model model, RedirectAttributes redirect) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("especialidades", especialidadService.buscarTodos());
            return "veterinarios/formulario";
        }

        Especialidad especialidad = especialidadService.buscarPorId(form.getEspecialidadId())
                .orElseThrow(() -> new IllegalArgumentException("Especialidad no encontrada"));

        Veterinario veterinario;
        if (form.getId() != null) {
            veterinario = veterinarioService.buscarPorId(form.getId())
                    .orElse(new Veterinario());
        } else {
            veterinario = new Veterinario();
        }
        veterinario.setNombre(form.getNombre());
        veterinario.setApellido(form.getApellido());
        veterinario.setCedulaProfesional(form.getCedulaProfesional());
        veterinario.setEspecialidad(especialidad);

        veterinarioService.guardar(veterinario);
        redirect.addFlashAttribute("mensajeExito", "veterinario.guardado.exito");
        return "redirect:/veterinarios";
    }

    @PostMapping("/{id}/eliminar")
    public String eliminar(@PathVariable Long id, RedirectAttributes redirect) {
        veterinarioService.eliminar(id);
        redirect.addFlashAttribute("mensajeExito", "veterinario.eliminado.exito");
        return "redirect:/veterinarios";
    }

    /**
     * Form de soporte para el binding del formulario HTML, ya que
     * Veterinario.especialidad es una relación JPA y el <select>
     * HTML solo puede enviar el id como texto plano.
     */
    public static class VeterinarioForm {
        private Long id;

        @jakarta.validation.constraints.NotBlank(message = "{veterinario.nombre.requerido}")
        private String nombre;

        @jakarta.validation.constraints.NotBlank(message = "{veterinario.apellido.requerido}")
        private String apellido;

        @jakarta.validation.constraints.NotBlank(message = "{veterinario.cedulaProfesional.requerido}")
        private String cedulaProfesional;

        @NotNull(message = "{veterinario.especialidad.requerido}")
        private Long especialidadId;

        public static VeterinarioForm desde(Veterinario veterinario) {
            VeterinarioForm form = new VeterinarioForm();
            form.id = veterinario.getId();
            form.nombre = veterinario.getNombre();
            form.apellido = veterinario.getApellido();
            form.cedulaProfesional = veterinario.getCedulaProfesional();
            form.especialidadId = veterinario.getEspecialidad().getId();
            return form;
        }

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public String getNombre() {
            return nombre;
        }

        public void setNombre(String nombre) {
            this.nombre = nombre;
        }

        public String getApellido() {
            return apellido;
        }

        public void setApellido(String apellido) {
            this.apellido = apellido;
        }

        public String getCedulaProfesional() {
            return cedulaProfesional;
        }

        public void setCedulaProfesional(String cedulaProfesional) {
            this.cedulaProfesional = cedulaProfesional;
        }

        public Long getEspecialidadId() {
            return especialidadId;
        }

        public void setEspecialidadId(Long especialidadId) {
            this.especialidadId = especialidadId;
        }
    }
}

