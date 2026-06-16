package com.veterinariaplus.app.servicio;

import java.util.List;
import java.util.Optional;

/**
 * Contrato genérico para los servicios CRUD del dominio.
 *
 * Aplica el patrón de diseño Service Layer: define una API uniforme
 * de negocio independiente de los detalles de persistencia, que las
 * implementaciones concretas (en el paquete servicio.impl) satisfacen.
 *
 * @param <T>  tipo de la entidad
 * @param <ID> tipo del identificador de la entidad
 */
public interface ServicioCrud<T, ID> {

    T guardar(T entidad);

    Optional<T> buscarPorId(ID id);

    List<T> buscarTodos();

    void eliminar(ID id);
}
