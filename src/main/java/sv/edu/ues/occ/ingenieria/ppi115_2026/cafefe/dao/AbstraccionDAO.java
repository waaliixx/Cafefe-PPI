package sv.edu.ues.occ.ingenieria.ppi115_2026.cafefe.dao;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import java.io.Serializable;
import java.util.Collections;
import java.util.List;

/**
 *
 * @author edenrvm
 */

public abstract class AbstraccionDAO<T> implements Serializable {

    private final Class<T> entityClass;

    public AbstraccionDAO(Class<T> entityClass) {
        this.entityClass = entityClass;
    }

    protected abstract EntityManager getEntityManager();

    public boolean crear(T entity) {
        if (entity == null || getEntityManager() == null) {
            return false;
        }
        try {
            getEntityManager().persist(entity);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean modificar(T entity) {
        if (entity == null || getEntityManager() == null) {
            return false;
        }
        try {
            getEntityManager().merge(entity);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean eliminar(T entity) {
        if (entity == null || getEntityManager() == null) {
            return false;
        }
        try {
            getEntityManager().remove(getEntityManager().merge(entity));
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public T buscarPorId(Object id) {
        if (id == null || getEntityManager() == null) {
            return null;
        }
        try {
            return getEntityManager().find(entityClass, id);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public List<T> findAll() {
        if (getEntityManager() == null) {
            return Collections.emptyList();
        }
        try {
            CriteriaQuery<T> cq = getEntityManager().getCriteriaBuilder().createQuery(entityClass);
            cq.select(cq.from(entityClass));
            return getEntityManager().createQuery(cq).getResultList();
        } catch (Exception e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }

    public List<T> findRange(int[] range) {
        if (getEntityManager() == null || range == null || range.length < 2) {
            return Collections.emptyList();
        }
        try {
            CriteriaQuery<T> cq = getEntityManager().getCriteriaBuilder().createQuery(entityClass);
            cq.select(cq.from(entityClass));
            TypedQuery<T> q = getEntityManager().createQuery(cq);
            q.setMaxResults(range[1] - range[0]);
            q.setFirstResult(range[0]);
            return q.getResultList();
        } catch (Exception e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }

    public int count() {
        if (getEntityManager() == null) {
            return 0;
        }
        try {
            CriteriaQuery<Long> cq = getEntityManager().getCriteriaBuilder().createQuery(Long.class);
            Root<T> rt = cq.from(entityClass);
            cq.select(getEntityManager().getCriteriaBuilder().count(rt));
            
            // Usar TypedQuery<Long> de JPA explícitamente
            jakarta.persistence.TypedQuery<Long> q = getEntityManager().createQuery(cq);
            return q.getSingleResult().intValue();
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }
}
