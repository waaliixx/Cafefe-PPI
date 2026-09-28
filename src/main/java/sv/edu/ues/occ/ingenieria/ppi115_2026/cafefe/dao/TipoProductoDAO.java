package sv.edu.ues.occ.ingenieria.ppi115_2026.cafefe.dao;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import java.io.Serializable;
import java.util.Collections;
import java.util.List;
import sv.edu.ues.occ.ingenieria.ppi115_2026.cafefe.entity.TipoProducto;

/**
 *
 * @author edenrvm
 */

public class TipoProductoDAO extends AbstraccionDAO<TipoProducto> implements Serializable {

    @PersistenceContext(unitName = "cafefePU")
    private EntityManager em;

    public TipoProductoDAO() {
        super(TipoProducto.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public List<TipoProducto> findByNombre(String nombre) {
        if (nombre == null || getEntityManager() == null) {
            return Collections.emptyList();
        }
        try {
            TypedQuery<TipoProducto> query = getEntityManager().createNamedQuery("TipoProducto.findByNombre", TipoProducto.class);
            query.setParameter("nombre", nombre);
            return query.getResultList();
        } catch (Exception e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }

    public List<TipoProducto> findByActivo(Boolean activo) {
        if (activo == null || getEntityManager() == null) {
            return Collections.emptyList();
        }
        try {
            TypedQuery<TipoProducto> query = getEntityManager().createNamedQuery("TipoProducto.findByActivo", TipoProducto.class);
            query.setParameter("activo", activo);
            return query.getResultList();
        } catch (Exception e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }
}
