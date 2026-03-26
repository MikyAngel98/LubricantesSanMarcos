package org.example.Servicio;

import org.example.DAO.jpa.ProveedorDAO;
import org.example.Modelo.jpa.Proveedor;
import org.example.Modelo.jpa.Contacto;
import org.example.Modelo.jpa.Persona;

import java.util.List;
import java.util.Optional;

public class ProveedorService {

    private final ProveedorDAO proveedorDAO;

    public ProveedorService() {
        this.proveedorDAO = new ProveedorDAO();
    }

    // ==================== CRUD BÁSICO ====================

    public Proveedor guardar(Proveedor proveedor) {
        validarProveedor(proveedor);
        return proveedorDAO.save(proveedor);
    }

    public Proveedor guardarConDatos(String nombres, String apellidos, String celular, String empresa) {
        Contacto contacto = new Contacto();
        contacto.setCelular(celular);

        Persona persona = new Persona();
        persona.setNombres(nombres);
        persona.setApellidos(apellidos);
        persona.setContacto(contacto);

        Proveedor proveedor = new Proveedor();
        proveedor.setPersona(persona);
        proveedor.setEmpresa(empresa);

        return guardar(proveedor);
    }

    public Proveedor actualizar(Proveedor proveedor) {
        if (proveedor.getId() == null) {
            throw new IllegalArgumentException("El ID es obligatorio para actualizar");
        }
        validarProveedor(proveedor);
        return proveedorDAO.update(proveedor);
    }

    public void eliminar(Integer id) {
        if (id == null) {
            throw new IllegalArgumentException("El ID es obligatorio");
        }

        Optional<Proveedor> proveedor = proveedorDAO.findById(id);
        if (proveedor.isEmpty()) {
            throw new IllegalStateException("No existe proveedor con ID: " + id);
        }

        proveedorDAO.deleteById(id);
    }

    public Optional<Proveedor> buscarPorId(Integer id) {
        if (id == null) {
            return Optional.empty();
        }
        return proveedorDAO.findById(id);
    }

    public List<Proveedor> listarTodos() {
        return proveedorDAO.findAll();
    }

    // ==================== MÉTODOS ADICIONALES ====================

    public List<Proveedor> buscarPorEmpresa(String empresa) {
        if (empresa == null || empresa.trim().isEmpty()) {
            return listarTodos();
        }
        return proveedorDAO.buscarPorEmpresa(empresa);
    }

    public List<Proveedor> buscarPorNombrePersona(String nombre) {
        if (nombre == null || nombre.trim().isEmpty()) {
            return listarTodos();
        }
        return proveedorDAO.buscarPorNombrePersona(nombre);
    }

    // ==================== VALIDACIONES ====================

    private void validarProveedor(Proveedor proveedor) {
        if (proveedor.getPersona() == null) {
            throw new IllegalArgumentException("El proveedor debe tener una persona asociada");
        }

        Persona persona = proveedor.getPersona();
        if (persona.getNombres() == null || persona.getNombres().trim().isEmpty()) {
            throw new IllegalArgumentException("Los nombres son obligatorios");
        }

        if (persona.getApellidos() == null || persona.getApellidos().trim().isEmpty()) {
            throw new IllegalArgumentException("Los apellidos son obligatorios");
        }

        if (persona.getContacto() == null) {
            throw new IllegalArgumentException("El proveedor debe tener un contacto");
        }

        if (persona.getContacto().getCelular() == null || persona.getContacto().getCelular().trim().isEmpty()) {
            throw new IllegalArgumentException("El número de celular es obligatorio");
        }

        if (proveedor.getEmpresa() == null || proveedor.getEmpresa().trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre de la empresa es obligatorio");
        }
    }
}
