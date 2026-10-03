package sv.edu.ues.occ.ingenieria.ppi115_2026.cafefe.dao;

import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import java.io.Serializable;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import sv.edu.ues.occ.ingenieria.ppi115_2026.cafefe.entity.Orden;

/**
 *
 * @author edenrvm
 */
@Stateless
public class OrdenDAO extends AbstraccionDAO<Orden> implements Serializable {

    @PersistenceContext(unitName = "CafefePU")
    private EntityManager em;

    public OrdenDAO() {
        super(Orden.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public List<Orden> findByFechaCreacion(Date fechaCreacion) {
        if (fechaCreacion == null || getEntityManager() == null) {
            return Collections.emptyList();
        }
        try {
            TypedQuery<Orden> query = getEntityManager().createNamedQuery("Orden.findByFechaCreacion", Orden.class);
            query.setParameter("fechaCreacion", fechaCreacion);
            return query.getResultList();
        } catch (Exception e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }
}
