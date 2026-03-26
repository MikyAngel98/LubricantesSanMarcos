package org.example.Servicio;

import org.example.DAO.jpa.MarcaDAO;
import org.example.Modelo.jpa.Marca;

import java.util.List;
import java.util.Optional;

public class MarcaService {

    private final MarcaDAO marcaDAO;

    public MarcaService() {
        this.marcaDAO = new MarcaDAO();
    }

    public Marca guardar(Marca marca) {
        if (marca.getNombre() == null || marca.getNombre().trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre de la marca es obligatorio");
        }

        if (marcaDAO.existePorNombre(marca.getNombre())) {
            throw new IllegalStateException("Ya existe una marca con el nombre: " + marca.getNombre());
        }

        return marcaDAO.save(marca);
    }

    public Marca actualizar(Marca marca) {
        if (marca.getId() == null) {
            throw new IllegalArgumentException("El ID es obligatorio para actualizar");
        }

        if (marca.getNombre() == null || marca.getNombre().trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre de la marca es obligatorio");
        }

        return marcaDAO.update(marca);
    }

    public void eliminar(Integer id) {
        if (id == null) {
            throw new IllegalArgumentException("El ID es obligatorio");
        }

        Optional<Marca> marca = marcaDAO.findById(id);
        if (marca.isEmpty()) {
            throw new IllegalStateException("No existe marca con ID: " + id);
        }

        marcaDAO.deleteById(id);
    }

    public Optional<Marca> buscarPorId(Integer id) {
        if (id == null) {
            return Optional.empty();
        }
        return marcaDAO.findById(id);
    }

    public List<Marca> listarTodos() {
        return marcaDAO.findAll();
    }

    public List<Marca> buscarPorNombre(String nombre) {
        if (nombre == null || nombre.trim().isEmpty()) {
            return listarTodos();
        }
        return marcaDAO.buscarPorNombre(nombre);
    }
}