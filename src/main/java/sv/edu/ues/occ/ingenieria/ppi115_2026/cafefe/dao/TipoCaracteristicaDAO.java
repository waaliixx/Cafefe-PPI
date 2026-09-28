package sv.edu.ues.occ.ingenieria.ppi115_2026.cafefe.dao;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import java.io.Serializable;
import java.util.Collections;
import java.util.List;
import sv.edu.ues.occ.ingenieria.ppi115_2026.cafefe.entity.TipoCaracteristica;

/**
 *
 * @author edenrvm
 */

public class TipoCaracteristicaDAO extends AbstraccionDAO<TipoCaracteristica> implements Serializable {

    @PersistenceContext(unitName = "cafefePU")
    private EntityManager em;

    public TipoCaracteristicaDAO() {
        super(TipoCaracteristica.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public List<TipoCaracteristica> findByNombre(String nombre) {
        if (nombre == null || getEntityManager() == null) {
            return Collections.emptyList();
        }
        try {
            TypedQuery<TipoCaracteristica> query = getEntityManager().createNamedQuery("TipoCaracteristica.findByNombre", TipoCaracteristica.class);
            query.setParameter("nombre", nombre);
            return query.getResultList();
        } catch (Exception e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }

    public List<TipoCaracteristica> findByExpresionRegular(String expresionRegular) {
        if (expresionRegular == null || getEntityManager() == null) {
            return Collections.emptyList();
        }
        try {
            TypedQuery<TipoCaracteristica> query = getEntityManager().createNamedQuery("TipoCaracteristica.findByExpresionRegular", TipoCaracteristica.class);
            query.setParameter("expresionRegular", expresionRegular);
            return query.getResultList();
        } catch (Exception e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }

    public List<TipoCaracteristica> findByActivo(Boolean activo) {
        if (activo == null || getEntityManager() == null) {
            return Collections.emptyList();
        }
        try {
            TypedQuery<TipoCaracteristica> query = getEntityManager().createNamedQuery("TipoCaracteristica.findByActivo", TipoCaracteristica.class);
            query.setParameter("activo", activo);
            return query.getResultList();
        } catch (Exception e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }
}