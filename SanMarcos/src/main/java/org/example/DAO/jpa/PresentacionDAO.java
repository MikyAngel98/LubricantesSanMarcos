package org.example.DAO.jpa;

import org.example.Modelo.jpa.Presentacion;
import javax.persistence.TypedQuery;
import java.util.List;

public class PresentacionDAO extends BaseRepository<Presentacion, Integer> {

    public PresentacionDAO() {
        super(Presentacion.class);
    }

    public List<Presentacion> buscarPorNombre(String nombre) {
        TypedQuery<Presentacion> query = getEntityManager().createQuery(
                "SELECT p FROM Presentacion p WHERE p.Nombre LIKE :nombre", Presentacion.class);
        query.setParameter("nombre", "%" + nombre + "%");
        return query.getResultList();
    }
}
