package com.veterinariaplus.app.controlador;

import com.veterinariaplus.app.excepcion.RecursoNoEncontradoException;
import com.veterinariaplus.app.excepcion.ReglaDeNegocioException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.ModelAndView;
import jakarta.servlet.http.HttpServletRequest;

/**
 * Manejador global de excepciones de negocio para los controladores MVC.
 *
 * Captura ReglaDeNegocioException y RecursoNoEncontradoException
 * (lanzadas desde la capa de servicio) y redirige al usuario a la
 * página anterior con un mensaje de error legible, en lugar de
 * mostrar una página de error genérica de Spring.
 *
 * El mensaje de estas excepciones se muestra TAL CUAL (no como clave
 * de i18n), porque ya viene en español desde el servicio. Para
 * mensajes de éxito controlados por esta aplicación, en cambio, se
 * usan claves de i18n (ver fragmentos/alertas.html).
 */
@ControllerAdvice
public class ManejadorExcepcionesWeb {

    @ExceptionHandler(ReglaDeNegocioException.class)
    public ModelAndView manejarReglaDeNegocio(ReglaDeNegocioException ex, HttpServletRequest request,
                                               RedirectAttributes redirectAttributes) {
        String referer = request.getHeader("Referer");
        redirectAttributes.addFlashAttribute("mensajeErrorDirecto", ex.getMessage());
        return new ModelAndView("redirect:" + (referer != null ? referer : "/"));
    }

    @ExceptionHandler(RecursoNoEncontradoException.class)
    public ModelAndView manejarRecursoNoEncontrado(RecursoNoEncontradoException ex, HttpServletRequest request,
                                                    RedirectAttributes redirectAttributes) {
        String referer = request.getHeader("Referer");
        redirectAttributes.addFlashAttribute("mensajeErrorDirecto", ex.getMessage());
        return new ModelAndView("redirect:" + (referer != null ? referer : "/"));
    }
}
