package com.veterinariaplus.app.controlador;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

/**
 * Provee atributos comunes a todas las vistas de la aplicación.
 *
 * En particular, expone "rutaActual" (la ruta de la petición sin query
 * string) para que el fragmento de navegación pueda construir los
 * enlaces del selector de idioma preservando la página en la que el
 * usuario se encuentra, sin perder su contexto de navegación.
 */
@ControllerAdvice
public class NavegacionControllerAdvice {

    @ModelAttribute("rutaActual")
    public String rutaActual(HttpServletRequest request) {
        return request.getRequestURI();
    }
}
