package com.veterinariaplus.app.servicio.impl;

import com.veterinariaplus.app.excepcion.RecursoNoEncontradoException;
import com.veterinariaplus.app.excepcion.ReglaDeNegocioException;
import com.veterinariaplus.app.modelo.Veterinario;
import com.veterinariaplus.app.repositorio.VeterinarioRepository;
import com.veterinariaplus.app.servicio.VeterinarioService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Implementación del servicio de gestión de veterinarios.
 */
@Service
public class VeterinarioServiceImpl implements VeterinarioService {

    private final VeterinarioRepository veterinarioRepository;

    public VeterinarioServiceImpl(VeterinarioRepository veterinarioRepository) {
        this.veterinarioRepository = veterinarioRepository;
    }

    @Override
    @Transactional
    public Veterinario guardar(Veterinario entidad) {
        veterinarioRepository.findByCedulaProfesional(entidad.getCedulaProfesional())
                .filter(existente -> !existente.getId().equals(entidad.getId()))
                .ifPresent(existente -> {
                    throw new ReglaDeNegocioException(
                            "Ya existe un veterinario registrado con la cédula " + entidad.getCedulaProfesional());
                });
        return veterinarioRepository.save(entidad);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Veterinario> buscarPorId(Long id) {
        return veterinarioRepository.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Veterinario> buscarTodos() {
        return veterinarioRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Veterinario> buscarActivos() {
        return veterinarioRepository.findByActivoTrue();
    }

    @Override
    @Transactional
    public void eliminar(Long id) {
        Veterinario veterinario = veterinarioRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Veterinario", id));
        // Baja logica en lugar de eliminacion fisica, para preservar el
        // historial de citas asociadas a este veterinario.
        veterinario.setActivo(false);
        veterinarioRepository.save(veterinario);
    }
}
