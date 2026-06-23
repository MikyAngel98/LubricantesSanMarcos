package org.example.DAO.jpa;

import org.example.Config.JpaUtil;
import org.example.Modelo.jpa.Usuario;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;
import java.util.List;
import java.util.Optional;

public class UsuarioDAO {

    private EntityManager getEntityManager() {
        return JpaUtil.getEntityManager();
    }

    public Usuario save(Usuario usuario) {
        EntityManager em = getEntityManager();
        try {
            em.getTransaction().begin();
            em.persist(usuario);
            em.getTransaction().commit();
            return usuario;
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw e;
        } finally {
            em.close();
        }
    }

    public Usuario update(Usuario usuario) {
        EntityManager em = getEntityManager();
        try {
            em.getTransaction().begin();
            Usuario actualizado = em.merge(usuario);
            em.getTransaction().commit();
            return actualizado;
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw e;
        } finally {
            em.close();
        }
    }

    public Optional<Usuario> findById(int id) {
        EntityManager em = getEntityManager();
        try {
            Usuario usuario = em.find(Usuario.class, id);
            return Optional.ofNullable(usuario);
        } finally {
            em.close();
        }
    }

    public Optional<Usuario> findByNombreUsuario(String nombreUsuario) {
        EntityManager em = getEntityManager();
        try {
            TypedQuery<Usuario> query = em.createQuery(
                    "SELECT u FROM Usuario u WHERE u.nombreUsuario = :nombreUsuario",
                    Usuario.class
            );
            query.setParameter("nombreUsuario", nombreUsuario);
            Usuario usuario = query.getSingleResult();
            return Optional.of(usuario);
        } catch (NoResultException e) {
            return Optional.empty();
        } finally {
            em.close();
        }
    }

    public List<Usuario> findAll() {
        EntityManager em = getEntityManager();
        try {
            TypedQuery<Usuario> query = em.createQuery(
                    "SELECT u FROM Usuario u ORDER BY u.nombreUsuario",
                    Usuario.class
            );
            return query.getResultList();
        } finally {
            em.close();
        }
    }

    public boolean existeUsuario(String nombreUsuario) {
        EntityManager em = getEntityManager();
        try {
            TypedQuery<Long> query = em.createQuery(
                    "SELECT COUNT(u) FROM Usuario u WHERE u.nombreUsuario = :nombreUsuario",
                    Long.class
            );
            query.setParameter("nombreUsuario", nombreUsuario);
            return query.getSingleResult() > 0;
        } finally {
            em.close();
        }
    }
}
