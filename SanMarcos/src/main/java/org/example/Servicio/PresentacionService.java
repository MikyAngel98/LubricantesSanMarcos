package org.example.Servicio;

import org.example.DAO.jpa.PresentacionDAO;
import org.example.Modelo.jpa.Presentacion;

import java.util.List;
import java.util.Optional;

public class PresentacionService {

    private final PresentacionDAO presentacionDAO;

    public PresentacionService() {
        this.presentacionDAO = new PresentacionDAO();
    }

    public Presentacion guardar(Presentacion presentacion) {
        if (presentacion.getNombre() == null || presentacion.getNombre().trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre de la presentación es obligatorio");
        }

        return presentacionDAO.save(presentacion);
    }

    public Presentacion actualizar(Presentacion presentacion) {
        if (presentacion.getId() == null) {
            throw new IllegalArgumentException("El ID es obligatorio para actualizar");
        }

        return presentacionDAO.update(presentacion);
    }

    public void eliminar(Integer id) {
        if (id == null) {
            throw new IllegalArgumentException("El ID es obligatorio");
        }

        Optional<Presentacion> presentacion = presentacionDAO.findById(id);
        if (presentacion.isEmpty()) {
            throw new IllegalStateException("No existe presentación con ID: " + id);
        }

        presentacionDAO.deleteById(id);
    }

    public Optional<Presentacion> buscarPorId(Integer id) {
        if (id == null) {
            return Optional.empty();
        }
        return presentacionDAO.findById(id);
    }

    public List<Presentacion> listarTodos() {
        return presentacionDAO.findAll();
    }

    public List<Presentacion> buscarPorNombre(String nombre) {
        if (nombre == null || nombre.trim().isEmpty()) {
            return listarTodos();
        }
        return presentacionDAO.buscarPorNombre(nombre);
    }
}
