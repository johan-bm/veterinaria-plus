package com.veterinariaplus.app.evento;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

/**
 * Observador (listener) que reacciona a los cambios de estado de una cita.
 *
 * En esta versión registra el cambio en el log de la aplicación, lo cual
 * es suficiente para fines académicos y de demostración del patrón
 * Observer. En un entorno productivo, aquí se agregarían notificaciones
 * por correo electrónico o SMS al propietario de la mascota.
 */
@Component
public class CambioEstadoCitaListener {

    private static final Logger log = LoggerFactory.getLogger(CambioEstadoCitaListener.class);

    @EventListener
    public void alCambiarEstado(CambioEstadoCitaEvent evento) {
        log.info("Cita #{} cambió de estado: {} -> {}",
                evento.getCita().getId(),
                evento.getEstadoAnterior(),
                evento.getEstadoNuevo());
    }
}
