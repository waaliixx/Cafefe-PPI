package sv.edu.ues.occ.ingenieria.ppi115_2026.cafefe.dao;

import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.io.Serializable;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import sv.edu.ues.occ.ingenieria.ppi115_2026.cafefe.entity.ProductoTipoProducto;

/**
 *
 * @author edenrvm
 */

@Stateless
public class ProductoTipoProductoDAO extends AbstraccionDAO<ProductoTipoProducto> implements Serializable {

    @PersistenceContext(unitName = "CafefePU")
    private EntityManager em;

    public ProductoTipoProductoDAO() {
        super(ProductoTipoProducto.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public List<ProductoTipoProducto> findByProducto(UUID idProducto) {
        if (idProducto == null) {
            return Collections.emptyList();
        }
        return em.createQuery(
                "SELECT p FROM ProductoTipoProducto p WHERE p.idProducto.idProducto = :id "
                + "ORDER BY p.fechaCreacion", ProductoTipoProducto.class)
                .setParameter("id", idProducto)
                .getResultList();
    }
}
