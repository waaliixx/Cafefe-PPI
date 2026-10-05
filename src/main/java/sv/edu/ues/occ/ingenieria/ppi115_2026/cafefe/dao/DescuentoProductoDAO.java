package sv.edu.ues.occ.ingenieria.ppi115_2026.cafefe.dao;

import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.io.Serializable;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import sv.edu.ues.occ.ingenieria.ppi115_2026.cafefe.entity.DescuentoProducto;

/**
 *
 * @author edenrvm
 */

@Stateless
public class DescuentoProductoDAO extends AbstraccionDAO<DescuentoProducto> implements Serializable {

    @PersistenceContext(unitName = "CafefePU")
    private EntityManager em;

    public DescuentoProductoDAO() {
        super(DescuentoProducto.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public List<DescuentoProducto> findByProducto(UUID idProducto) {
        if (idProducto == null) {
            return Collections.emptyList();
        }
        return em.createQuery(
                "SELECT d FROM DescuentoProducto d WHERE d.idProducto.idProducto = :id "
                + "ORDER BY d.fechaDesde", DescuentoProducto.class)
                .setParameter("id", idProducto)
                .getResultList();
    }
}
