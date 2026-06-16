package com.veterinariaplus.app.servicio;

import com.veterinariaplus.app.modelo.Propietario;

import java.util.List;

/**
 * Operaciones de negocio disponibles para la gestión de propietarios.
 */
public interface PropietarioService extends ServicioCrud<Propietario, Long> {

    /**
     * Busca propietarios cuyo nombre o apellido contenga el texto indicado.
     */
    List<Propietario> buscar(String texto);
}
