package org.example.DAO.jpa;

import org.example.Modelo.jpa.Cliente;
import javax.persistence.TypedQuery;
import java.util.List;

public class ClienteDAO extends BaseRepository<Cliente, Integer> {

    public ClienteDAO() {
        super(Cliente.class);
    }

    public List<Cliente> buscarPorNombre(String nombre) {
        TypedQuery<Cliente> query = getEntityManager().createQuery(
                "SELECT c FROM Cliente c WHERE c.persona.Nombres LIKE :nombre OR c.persona.Apellidos LIKE :nombre",
                Cliente.class);
        query.setParameter("nombre", "%" + nombre + "%");
        return query.getResultList();
    }

    public List<Cliente> buscarPorNombreCompleto(String texto) {
        TypedQuery<Cliente> query = getEntityManager().createQuery(
                "SELECT c FROM Cliente c WHERE CONCAT(c.persona.Nombres, ' ', c.persona.Apellidos) LIKE :texto",
                Cliente.class);
        query.setParameter("texto", "%" + texto + "%");
        return query.getResultList();
    }
}