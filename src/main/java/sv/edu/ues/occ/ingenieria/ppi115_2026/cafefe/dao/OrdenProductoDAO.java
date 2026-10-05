package sv.edu.ues.occ.ingenieria.ppi115_2026.cafefe.dao;

import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.io.Serializable;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import sv.edu.ues.occ.ingenieria.ppi115_2026.cafefe.entity.OrdenProducto;

/**
 *
 * @author edenrvm
 */

@Stateless
public class OrdenProductoDAO extends AbstraccionDAO<OrdenProducto> implements Serializable {

    @PersistenceContext(unitName = "CafefePU")
    private EntityManager em;

    public OrdenProductoDAO() {
        super(OrdenProducto.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public List<OrdenProducto> findByOrden(UUID idOrden) {
        if (idOrden == null || em == null) {
            return Collections.emptyList();
        }
        try {
            return em.createQuery(
                    "SELECT op FROM OrdenProducto op WHERE op.idOrden.idOrden = :id "
                    + "ORDER BY op.idProducto.nombre, op.idOrdenProducto", OrdenProducto.class)
                    .setParameter("id", idOrden)
                    .getResultList();
        } catch (Exception e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }
}
