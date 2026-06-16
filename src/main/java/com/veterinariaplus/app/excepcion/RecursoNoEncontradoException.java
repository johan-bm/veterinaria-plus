package com.veterinariaplus.app.excepcion;

/**
 * Se lanza cuando se intenta acceder a una entidad que no existe
 * en la base de datos (por ejemplo, al editar un registro con un
 * id inválido).
 */
public class RecursoNoEncontradoException extends RuntimeException {

    public RecursoNoEncontradoException(String mensaje) {
        super(mensaje);
    }

    public RecursoNoEncontradoException(String entidad, Long id) {
        super(entidad + " con id " + id + " no fue encontrado(a)");
    }
}
