package com.veterinariaplus.app.servicio.impl;

import com.veterinariaplus.app.excepcion.RecursoNoEncontradoException;
import com.veterinariaplus.app.excepcion.ReglaDeNegocioException;
import com.veterinariaplus.app.modelo.Propietario;
import com.veterinariaplus.app.repositorio.PropietarioRepository;
import com.veterinariaplus.app.servicio.PropietarioService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Implementación del servicio de gestión de propietarios.
 *
 * Aplica la regla de negocio de que el correo electrónico debe ser
 * único entre propietarios (ya reforzado a nivel de base de datos
 * con una restricción UNIQUE, pero validado aquí también para dar
 * un mensaje de error claro al usuario en lugar de una excepción
 * de integridad de base de datos).
 */
@Service
public class PropietarioServiceImpl implements PropietarioService {

    private final PropietarioRepository propietarioRepository;

    public PropietarioServiceImpl(PropietarioRepository propietarioRepository) {
        this.propietarioRepository = propietarioRepository;
    }

    @Override
    @Transactional
    public Propietario guardar(Propietario entidad) {
        propietarioRepository.findByEmailIgnoreCase(entidad.getEmail())
                .filter(existente -> !existente.getId().equals(entidad.getId()))
                .ifPresent(existente -> {
                    throw new ReglaDeNegocioException(
                            "Ya existe un propietario registrado con el correo " + entidad.getEmail());
                });
        return propietarioRepository.save(entidad);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Propietario> buscarPorId(Long id) {
        return propietarioRepository.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Propietario> buscarTodos() {
        return propietarioRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Propietario> buscar(String texto) {
        if (texto == null || texto.isBlank()) {
            return buscarTodos();
        }
        return propietarioRepository.buscarPorNombreOApellido(texto);
    }

    @Override
    @Transactional
    public void eliminar(Long id) {
        if (!propietarioRepository.existsById(id)) {
            throw new RecursoNoEncontradoException("Propietario", id);
        }
        propietarioRepository.deleteById(id);
    }
}
