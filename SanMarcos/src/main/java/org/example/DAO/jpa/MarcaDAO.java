package org.example.DAO.jpa;

import org.example.Modelo.jpa.Marca;
import javax.persistence.TypedQuery;
import java.util.List;

public class MarcaDAO extends BaseRepository<Marca, Integer> {

    public MarcaDAO() {
        super(Marca.class);
    }

    public List<Marca> buscarPorNombre(String nombre) {
        TypedQuery<Marca> query = getEntityManager().createQuery(
                "SELECT m FROM Marca m WHERE m.Nombre LIKE :nombre", Marca.class);
        query.setParameter("nombre", "%" + nombre + "%");
        return query.getResultList();
    }

    public boolean existePorNombre(String nombre) {
        TypedQuery<Long> query = getEntityManager().createQuery(
                "SELECT COUNT(m) FROM Marca m WHERE m.Nombre = :nombre", Long.class);
        query.setParameter("nombre", nombre);
        return query.getSingleResult() > 0;
    }
}