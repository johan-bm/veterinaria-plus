package com.veterinariaplus.app.servicio.impl;

import com.veterinariaplus.app.dto.MascotaDTO;
import com.veterinariaplus.app.excepcion.RecursoNoEncontradoException;
import com.veterinariaplus.app.modelo.Especie;
import com.veterinariaplus.app.modelo.Mascota;
import com.veterinariaplus.app.modelo.Propietario;
import com.veterinariaplus.app.repositorio.EspecieRepository;
import com.veterinariaplus.app.repositorio.MascotaRepository;
import com.veterinariaplus.app.repositorio.PropietarioRepository;
import com.veterinariaplus.app.servicio.MascotaService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Implementación del servicio de gestión de mascotas.
 *
 * Se encarga de traducir el MascotaDTO recibido desde la vista en una
 * entidad Mascota persistente, resolviendo las relaciones con Especie
 * y Propietario.
 */
@Service
public class MascotaServiceImpl implements MascotaService {

    private final MascotaRepository mascotaRepository;
    private final EspecieRepository especieRepository;
    private final PropietarioRepository propietarioRepository;

    public MascotaServiceImpl(MascotaRepository mascotaRepository,
                               EspecieRepository especieRepository,
                               PropietarioRepository propietarioRepository) {
        this.mascotaRepository = mascotaRepository;
        this.especieRepository = especieRepository;
        this.propietarioRepository = propietarioRepository;
    }

    @Override
    @Transactional
    public Mascota guardarDesdeDto(MascotaDTO dto) {
        Especie especie = especieRepository.findById(dto.getEspecieId())
                .orElseThrow(() -> new RecursoNoEncontradoException("Especie", dto.getEspecieId()));

        Propietario propietario = propietarioRepository.findById(dto.getPropietarioId())
                .orElseThrow(() -> new RecursoNoEncontradoException("Propietario", dto.getPropietarioId()));

        Mascota mascota;
        if (dto.getId() != null) {
            mascota = mascotaRepository.findById(dto.getId())
                    .orElseThrow(() -> new RecursoNoEncontradoException("Mascota", dto.getId()));
        } else {
            mascota = new Mascota();
        }

        mascota.setNombre(dto.getNombre());
        mascota.setEspecie(especie);
        mascota.setRaza(dto.getRaza());
        mascota.setFechaNacimiento(dto.getFechaNacimiento());
        mascota.setSexo(dto.getSexo());
        mascota.setPropietario(propietario);

        return mascotaRepository.save(mascota);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Mascota> buscarPorId(Long id) {
        return mascotaRepository.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Mascota> buscarTodos() {
        return mascotaRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Mascota> buscar(String texto) {
        if (texto == null || texto.isBlank()) {
            return buscarTodos();
        }
        return mascotaRepository.buscar(texto);
    }

    @Override
    @Transactional
    public void eliminar(Long id) {
        if (!mascotaRepository.existsById(id)) {
            throw new RecursoNoEncontradoException("Mascota", id);
        }
        mascotaRepository.deleteById(id);
    }
}
