package sv.edu.ues.occ.ingenieria.ppi115_2026.cafefe.control;

import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import java.io.Serializable;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import sv.edu.ues.occ.ingenieria.ppi115_2026.cafefe.entity.TipoProducto;

/**
 *
 * @author edenrvm
 */
@Stateless
public class TipoProductoDAO extends AbstraccionDAO<TipoProducto> implements Serializable {

    @PersistenceContext(unitName = "CafefePU")
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

    public long contarProductos(UUID idTipoProducto) {
        if (idTipoProducto == null) {
            return 0;
        }
        return em.createQuery(
                "SELECT COUNT(p) FROM ProductoTipoProducto p "
                + "WHERE p.idTipoProducto.idTipoProducto = :id", Long.class)
                .setParameter("id", idTipoProducto)
                .getSingleResult();
    }
}
