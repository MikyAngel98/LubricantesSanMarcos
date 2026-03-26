package org.example.Servicio;

import org.example.DAO.jpa.ClienteDAO;
import org.example.Modelo.jpa.Cliente;
import org.example.Modelo.jpa.Contacto;
import org.example.Modelo.jpa.Persona;

import java.util.List;
import java.util.Optional;

public class ClienteService {

    private final ClienteDAO clienteDAO;

    public ClienteService() {
        this.clienteDAO = new ClienteDAO();
    }

    // ==================== CRUD BÁSICO ====================

    public Cliente guardar(Cliente cliente) {
        validarCliente(cliente);
        return clienteDAO.save(cliente);
    }

    public Cliente guardarConDatos(String nombres, String apellidos, String celular) {
        Contacto contacto = new Contacto();
        contacto.setCelular(celular);

        Persona persona = new Persona();
        persona.setNombres(nombres);
        persona.setApellidos(apellidos);
        persona.setContacto(contacto);

        Cliente cliente = new Cliente();
        cliente.setPersona(persona);

        return guardar(cliente);
    }

    public Cliente actualizar(Cliente cliente) {
        if (cliente.getId() == null) {
            throw new IllegalArgumentException("El ID es obligatorio para actualizar");
        }
        validarCliente(cliente);
        return clienteDAO.update(cliente);
    }

    public void eliminar(Integer id) {
        if (id == null) {
            throw new IllegalArgumentException("El ID es obligatorio");
        }

        Optional<Cliente> cliente = clienteDAO.findById(id);
        if (cliente.isEmpty()) {
            throw new IllegalStateException("No existe cliente con ID: " + id);
        }

        clienteDAO.deleteById(id);
    }

    public Optional<Cliente> buscarPorId(Integer id) {
        if (id == null) {
            return Optional.empty();
        }
        return clienteDAO.findById(id);
    }

    public List<Cliente> listarTodos() {
        return clienteDAO.findAll();
    }

    // ==================== MÉTODOS ADICIONALES ====================

    public List<Cliente> buscarPorNombre(String nombre) {
        if (nombre == null || nombre.trim().isEmpty()) {
            return listarTodos();
        }
        return clienteDAO.buscarPorNombre(nombre);
    }

    public List<Cliente> buscarPorNombreCompleto(String texto) {
        if (texto == null || texto.trim().isEmpty()) {
            return listarTodos();
        }
        return clienteDAO.buscarPorNombreCompleto(texto);
    }

    // ==================== VALIDACIONES ====================

    private void validarCliente(Cliente cliente) {
        if (cliente.getPersona() == null) {
            throw new IllegalArgumentException("El cliente debe tener una persona asociada");
        }

        Persona persona = cliente.getPersona();
        if (persona.getNombres() == null || persona.getNombres().trim().isEmpty()) {
            throw new IllegalArgumentException("Los nombres son obligatorios");
        }

        if (persona.getApellidos() == null || persona.getApellidos().trim().isEmpty()) {
            throw new IllegalArgumentException("Los apellidos son obligatorios");
        }

        if (persona.getContacto() == null) {
            throw new IllegalArgumentException("El cliente debe tener un contacto");
        }

        if (persona.getContacto().getCelular() == null || persona.getContacto().getCelular().trim().isEmpty()) {
            throw new IllegalArgumentException("El número de celular es obligatorio");
        }
    }
}
