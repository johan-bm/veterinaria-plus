package com.veterinariaplus.app.servicio.impl;

import com.veterinariaplus.app.excepcion.RecursoNoEncontradoException;
import com.veterinariaplus.app.modelo.Especialidad;
import com.veterinariaplus.app.repositorio.EspecialidadRepository;
import com.veterinariaplus.app.servicio.EspecialidadService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Implementación del servicio de gestión del catálogo de especialidades.
 */
@Service
public class EspecialidadServiceImpl implements EspecialidadService {

    private final EspecialidadRepository especialidadRepository;

    public EspecialidadServiceImpl(EspecialidadRepository especialidadRepository) {
        this.especialidadRepository = especialidadRepository;
    }

    @Override
    @Transactional
    public Especialidad guardar(Especialidad entidad) {
        return especialidadRepository.save(entidad);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Especialidad> buscarPorId(Long id) {
        return especialidadRepository.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Especialidad> buscarTodos() {
        return especialidadRepository.findAll();
    }

    @Override
    @Transactional
    public void eliminar(Long id) {
        if (!especialidadRepository.existsById(id)) {
            throw new RecursoNoEncontradoException("Especialidad", id);
        }
        especialidadRepository.deleteById(id);
    }
}
