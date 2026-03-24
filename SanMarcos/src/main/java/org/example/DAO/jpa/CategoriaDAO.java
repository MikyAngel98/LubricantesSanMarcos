package org.example.DAO.jpa;

import org.example.Modelo.jpa.Categoria;
import javax.persistence.TypedQuery;
import java.util.List;

public class CategoriaDAO extends BaseRepository<Categoria, Integer> {

    public CategoriaDAO() {
        super(Categoria.class);
    }

    public List<Categoria> buscarPorNombre(String nombre) {
        TypedQuery<Categoria> query = getEntityManager().createQuery(
                "SELECT c FROM Categoria c WHERE c.Nombre LIKE :nombre", Categoria.class);
        query.setParameter("nombre", "%" + nombre + "%");
        return query.getResultList();
    }

    public boolean existePorNombre(String nombre) {
        TypedQuery<Long> query = getEntityManager().createQuery(
                "SELECT COUNT(c) FROM Categoria c WHERE c.Nombre = :nombre", Long.class);
        query.setParameter("nombre", nombre);
        return query.getSingleResult() > 0;
    }
}