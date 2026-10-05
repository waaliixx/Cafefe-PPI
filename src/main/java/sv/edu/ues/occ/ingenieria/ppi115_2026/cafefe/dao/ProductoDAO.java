package sv.edu.ues.occ.ingenieria.ppi115_2026.cafefe.dao;

import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import sv.edu.ues.occ.ingenieria.ppi115_2026.cafefe.entity.Producto;

/**
 *
 * @author edenrvm
 */

@Stateless
public class ProductoDAO extends AbstraccionDAO<Producto> implements Serializable {

    @PersistenceContext(unitName = "CafefePU")
    private EntityManager em;

    public ProductoDAO() {
        super(Producto.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public List<Producto> findByNombre(String nombre) {
        if (nombre == null || nombre.trim().isEmpty() || getEntityManager() == null) {
            return Collections.emptyList();
        }
        try {
            TypedQuery<Producto> query = getEntityManager().createNamedQuery("Producto.findByNombre", Producto.class);
            query.setParameter("nombre", nombre);
            return query.getResultList();
        } catch (Exception e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }

    public List<Producto> findByActivo(Boolean activo) {
        if (activo == null || getEntityManager() == null) {
            return Collections.emptyList();
        }
        try {
            TypedQuery<Producto> query = getEntityManager().createNamedQuery("Producto.findByActivo", Producto.class);
            query.setParameter("activo", activo);
            return query.getResultList();
        } catch (Exception e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }

    public List<Producto> findByPrecioSugerido(BigDecimal precioSugerido) {
        if (precioSugerido == null || getEntityManager() == null) {
            return Collections.emptyList();
        }
        try {
            TypedQuery<Producto> query = getEntityManager().createNamedQuery("Producto.findByPrecioSugerido", Producto.class);
            query.setParameter("precioSugerido", precioSugerido);
            return query.getResultList();
        } catch (Exception e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }
}
