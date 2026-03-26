package org.example.Servicio;

import org.example.DAO.jpa.CategoriaDAO;
import org.example.Modelo.jpa.Categoria;

import java.util.List;
import java.util.Optional;

public class CategoriaService {

    private final CategoriaDAO categoriaDAO;

    public CategoriaService() {
        this.categoriaDAO = new CategoriaDAO();
    }

    // ==================== CRUD BÁSICO ====================

    public Categoria guardar(Categoria categoria) {
        if (categoria.getNombre() == null || categoria.getNombre().trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre de la categoría es obligatorio");
        }

        if (categoriaDAO.existePorNombre(categoria.getNombre())) {
            throw new IllegalStateException("Ya existe una categoría con el nombre: " + categoria.getNombre());
        }

        return categoriaDAO.save(categoria);
    }

    public Categoria actualizar(Categoria categoria) {
        if (categoria.getId() == null) {
            throw new IllegalArgumentException("El ID es obligatorio para actualizar");
        }

        if (categoria.getNombre() == null || categoria.getNombre().trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre de la categoría es obligatorio");
        }

        return categoriaDAO.update(categoria);
    }

    public void eliminar(Integer id) {
        if (id == null) {
            throw new IllegalArgumentException("El ID es obligatorio");
        }

        Optional<Categoria> categoria = categoriaDAO.findById(id);
        if (categoria.isEmpty()) {
            throw new IllegalStateException("No existe categoría con ID: " + id);
        }

        categoriaDAO.deleteById(id);
    }

    public Optional<Categoria> buscarPorId(Integer id) {
        if (id == null) {
            return Optional.empty();
        }
        return categoriaDAO.findById(id);
    }

    public List<Categoria> listarTodos() {
        return categoriaDAO.findAll();
    }

    // ==================== MÉTODOS ADICIONALES ====================

    public List<Categoria> buscarPorNombre(String nombre) {
        if (nombre == null || nombre.trim().isEmpty()) {
            return listarTodos();
        }
        return categoriaDAO.buscarPorNombre(nombre);
    }

    public boolean existePorNombre(String nombre) {
        return categoriaDAO.existePorNombre(nombre);
    }
}
