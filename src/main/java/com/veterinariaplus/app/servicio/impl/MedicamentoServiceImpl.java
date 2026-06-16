package com.veterinariaplus.app.servicio.impl;

import com.veterinariaplus.app.excepcion.RecursoNoEncontradoException;
import com.veterinariaplus.app.excepcion.ReglaDeNegocioException;
import com.veterinariaplus.app.modelo.Medicamento;
import com.veterinariaplus.app.repositorio.MedicamentoRepository;
import com.veterinariaplus.app.servicio.MedicamentoService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Implementación del servicio de gestión de medicamentos.
 */
@Service
public class MedicamentoServiceImpl implements MedicamentoService {

    private final MedicamentoRepository medicamentoRepository;

    public MedicamentoServiceImpl(MedicamentoRepository medicamentoRepository) {
        this.medicamentoRepository = medicamentoRepository;
    }

    @Override
    @Transactional
    public Medicamento guardar(Medicamento entidad) {
        return medicamentoRepository.save(entidad);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Medicamento> buscarPorId(Long id) {
        return medicamentoRepository.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Medicamento> buscarTodos() {
        return medicamentoRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Medicamento> buscarConStockBajo() {
        return medicamentoRepository.findConStockBajo();
    }

    @Override
    @Transactional
    public Medicamento descontarStock(Long medicamentoId, int cantidad) {
        if (cantidad <= 0) {
            throw new ReglaDeNegocioException("La cantidad a descontar debe ser mayor a cero");
        }
        Medicamento medicamento = medicamentoRepository.findById(medicamentoId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Medicamento", medicamentoId));

        if (medicamento.getStock() < cantidad) {
            throw new ReglaDeNegocioException(
                    "Stock insuficiente de " + medicamento.getNombre()
                            + ". Disponible: " + medicamento.getStock() + ", solicitado: " + cantidad);
        }

        medicamento.setStock(medicamento.getStock() - cantidad);
        return medicamentoRepository.save(medicamento);
    }

    @Override
    @Transactional
    public Medicamento incrementarStock(Long medicamentoId, int cantidad) {
        if (cantidad <= 0) {
            throw new ReglaDeNegocioException("La cantidad a incrementar debe ser mayor a cero");
        }
        Medicamento medicamento = medicamentoRepository.findById(medicamentoId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Medicamento", medicamentoId));

        medicamento.setStock(medicamento.getStock() + cantidad);
        return medicamentoRepository.save(medicamento);
    }

    @Override
    @Transactional
    public void eliminar(Long id) {
        if (!medicamentoRepository.existsById(id)) {
            throw new RecursoNoEncontradoException("Medicamento", id);
        }
        medicamentoRepository.deleteById(id);
    }
}
