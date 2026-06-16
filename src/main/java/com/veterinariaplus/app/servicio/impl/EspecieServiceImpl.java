package com.veterinariaplus.app.servicio.impl;

import com.veterinariaplus.app.excepcion.RecursoNoEncontradoException;
import com.veterinariaplus.app.modelo.Especie;
import com.veterinariaplus.app.repositorio.EspecieRepository;
import com.veterinariaplus.app.servicio.EspecieService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Implementación del servicio de gestión del catálogo de especies.
 */
@Service
public class EspecieServiceImpl implements EspecieService {

    private final EspecieRepository especieRepository;

    public EspecieServiceImpl(EspecieRepository especieRepository) {
        this.especieRepository = especieRepository;
    }

    @Override
    @Transactional
    public Especie guardar(Especie entidad) {
        return especieRepository.save(entidad);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Especie> buscarPorId(Long id) {
        return especieRepository.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Especie> buscarTodos() {
        return especieRepository.findAll();
    }

    @Override
    @Transactional
    public void eliminar(Long id) {
        if (!especieRepository.existsById(id)) {
            throw new RecursoNoEncontradoException("Especie", id);
        }
        especieRepository.deleteById(id);
    }
}
