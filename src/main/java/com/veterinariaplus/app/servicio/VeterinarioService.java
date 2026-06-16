package com.veterinariaplus.app.servicio;

import com.veterinariaplus.app.modelo.Veterinario;

import java.util.List;

/**
 * Operaciones de negocio disponibles para la gestión de veterinarios.
 */
public interface VeterinarioService extends ServicioCrud<Veterinario, Long> {

    List<Veterinario> buscarActivos();
}
