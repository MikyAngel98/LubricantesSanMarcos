package org.example.Servicio;

import java.util.List;
import java.util.Optional;

public abstract class BaseService<T, ID> {

    public abstract T guardar(T entity);
    public abstract T actualizar(T entity);
    public abstract void eliminar(ID id);
    public abstract Optional<T> buscarPorId(ID id);
    public abstract List<T> listarTodos();
}
