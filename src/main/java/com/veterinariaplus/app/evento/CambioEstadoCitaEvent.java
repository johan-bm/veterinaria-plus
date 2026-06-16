package com.veterinariaplus.app.evento;

import com.veterinariaplus.app.modelo.Cita;
import org.springframework.context.ApplicationEvent;

/**
 * Evento de dominio publicado cada vez que una cita cambia de estado
 * (AGENDADA, CONFIRMADA, CANCELADA, COMPLETADA).
 *
 * Implementa el patrón de diseño Observer mediante el mecanismo de
 * eventos de Spring (ApplicationEventPublisher / @EventListener):
 * CitaServiceImpl actúa como "sujeto observado" que publica el evento,
 * y cualquier número de "observadores" (listeners) puede reaccionar
 * sin que el servicio de citas conozca su existencia. Esto desacopla
 * la lógica de negocio principal de tareas secundarias como notificar,
 * registrar auditoría o enviar correos.
 */
public class CambioEstadoCitaEvent extends ApplicationEvent {

    private final Cita cita;
    private final Cita.EstadoCita estadoAnterior;
    private final Cita.EstadoCita estadoNuevo;

    public CambioEstadoCitaEvent(Cita cita, Cita.EstadoCita estadoAnterior, Cita.EstadoCita estadoNuevo) {
        super(cita);
        this.cita = cita;
        this.estadoAnterior = estadoAnterior;
        this.estadoNuevo = estadoNuevo;
    }

    public Cita getCita() {
        return cita;
    }

    public Cita.EstadoCita getEstadoAnterior() {
        return estadoAnterior;
    }

    public Cita.EstadoCita getEstadoNuevo() {
        return estadoNuevo;
    }
}
