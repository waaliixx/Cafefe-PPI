package sv.edu.ues.occ.ingenieria.ppi115_2026.cafefe.control;

import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import java.io.Serializable;
import java.util.Collections;
import java.util.List;
import sv.edu.ues.occ.ingenieria.ppi115_2026.cafefe.entity.TipoDescuento;

/**
 *
 * @author edenrvm
 */
@Stateless
public class TipoDescuentoDAO extends AbstraccionDAO<TipoDescuento> implements Serializable {

    @PersistenceContext(unitName = "CafefePU")
    private EntityManager em;

    public TipoDescuentoDAO() {
        super(TipoDescuento.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    @Override
    public List<TipoDescuento> findRange(int[] range) {
        if (range == null || range.length < 2 || range[0] < 0 || range[1] <= range[0]) {
            return Collections.emptyList();
        }
        return em.createQuery("SELECT t FROM TipoDescuento t ORDER BY t.nombre, t.idTipoDescuento",
                TipoDescuento.class)
                .setFirstResult(range[0])
                .setMaxResults(range[1] - range[0])
                .getResultList();
    }

    public List<TipoDescuento> findByNombre(String nombre) {
        if (nombre == null || getEntityManager() == null) {
            return Collections.emptyList();
        }
        try {
            TypedQuery<TipoDescuento> query = getEntityManager().createNamedQuery("TipoDescuento.findByNombre", TipoDescuento.class);
            query.setParameter("nombre", nombre);
            return query.getResultList();
        } catch (Exception e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }

    public List<TipoDescuento> findByActivo(Boolean activo) {
        if (activo == null || getEntityManager() == null) {
            return Collections.emptyList();
        }
        try {
            TypedQuery<TipoDescuento> query = getEntityManager().createNamedQuery("TipoDescuento.findByActivo", TipoDescuento.class);
            query.setParameter("activo", activo);
            return query.getResultList();
        } catch (Exception e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }

    public List<TipoDescuento> findByDescuentoMaximo(Integer descuentoMaximo) {
        if (descuentoMaximo == null || getEntityManager() == null) {
            return Collections.emptyList();
        }
        try {
            TypedQuery<TipoDescuento> query = getEntityManager().createNamedQuery("TipoDescuento.findByDescuentoMaximo", TipoDescuento.class);
            query.setParameter("descuentoMaximo", descuentoMaximo);
            return query.getResultList();
        } catch (Exception e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }
}
