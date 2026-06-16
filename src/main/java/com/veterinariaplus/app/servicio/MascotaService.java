package com.veterinariaplus.app.servicio;

import com.veterinariaplus.app.dto.MascotaDTO;
import com.veterinariaplus.app.modelo.Mascota;

import java.util.List;
import java.util.Optional;

/**
 * Operaciones de negocio disponibles para la gestión de mascotas.
 */
public interface MascotaService {

    Mascota guardarDesdeDto(MascotaDTO dto);

    Optional<Mascota> buscarPorId(Long id);

    List<Mascota> buscarTodos();

    /**
     * Búsqueda por nombre de mascota, especie o propietario (RF-06).
     */
    List<Mascota> buscar(String texto);

    void eliminar(Long id);
}
