package org.example.DAO.jpa;

import org.example.Modelo.jpa.Proveedor;
import javax.persistence.TypedQuery;
import java.util.List;

public class ProveedorDAO extends BaseRepository<Proveedor, Integer> {

    public ProveedorDAO() {
        super(Proveedor.class);
    }

    public List<Proveedor> buscarPorEmpresa(String empresa) {
        TypedQuery<Proveedor> query = getEntityManager().createQuery(
                "SELECT p FROM Proveedor p WHERE p.Empresa LIKE :empresa", Proveedor.class);
        query.setParameter("empresa", "%" + empresa + "%");
        return query.getResultList();
    }

    public List<Proveedor> buscarPorNombrePersona(String nombre) {
        TypedQuery<Proveedor> query = getEntityManager().createQuery(
                "SELECT p FROM Proveedor p WHERE p.persona.Nombres LIKE :nombre OR p.persona.Apellidos LIKE :nombre",
                Proveedor.class);
        query.setParameter("nombre", "%" + nombre + "%");
        return query.getResultList();
    }
}
