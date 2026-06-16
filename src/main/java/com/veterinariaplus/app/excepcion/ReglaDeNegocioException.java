package com.veterinariaplus.app.excepcion;

/**
 * Se lanza cuando una operación viola una regla de negocio del dominio,
 * por ejemplo: agendar una cita con un horario que se solapa con otra,
 * o intentar reducir el stock de un medicamento por debajo de cero.
 */
public class ReglaDeNegocioException extends RuntimeException {

    public ReglaDeNegocioException(String mensaje) {
        super(mensaje);
    }
}
