package org.example.DAO.jpa;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.TypedQuery;
import java.util.List;
import java.util.Optional;

public abstract class BaseRepository<T, ID> {

    protected EntityManager em;
    private final Class<T> entityClass;

    public BaseRepository(Class<T> entityClass) {
        this.entityClass = entityClass;
    }

    protected EntityManager getEntityManager() {
        if (em == null || !em.isOpen()) {
            em = org.example.Config.JpaUtil.getEntityManager();
        }
        return em;
    }

    public T save(T entity) {
        EntityTransaction tx = getEntityManager().getTransaction();
        try {
            tx.begin();
            getEntityManager().persist(entity);
            tx.commit();
            return entity;
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            throw new RuntimeException("Error al guardar: " + e.getMessage(), e);
        }
    }

    public T update(T entity) {
        EntityTransaction tx = getEntityManager().getTransaction();
        try {
            tx.begin();
            T merged = getEntityManager().merge(entity);
            tx.commit();
            return merged;
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            throw new RuntimeException("Error al actualizar: " + e.getMessage(), e);
        }
    }

    public void delete(T entity) {
        EntityTransaction tx = getEntityManager().getTransaction();
        try {
            tx.begin();
            getEntityManager().remove(getEntityManager().contains(entity) ? entity : getEntityManager().merge(entity));
            tx.commit();
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            throw new RuntimeException("Error al eliminar: " + e.getMessage(), e);
        }
    }

    public void deleteById(ID id) {
        findById(id).ifPresent(this::delete);
    }

    public Optional<T> findById(ID id) {
        T entity = getEntityManager().find(entityClass, id);
        return Optional.ofNullable(entity);
    }

    public List<T> findAll() {
        TypedQuery<T> query = getEntityManager().createQuery(
                "SELECT e FROM " + entityClass.getSimpleName() + " e", entityClass);
        return query.getResultList();
    }

    public boolean existsById(ID id) {
        return findById(id).isPresent();
    }

    public long count() {
        TypedQuery<Long> query = getEntityManager().createQuery(
                "SELECT COUNT(e) FROM " + entityClass.getSimpleName() + " e", Long.class);
        return query.getSingleResult();
    }
}
