package sv.edu.ues.occ.ingenieria.ppi115_2026.cafefe.dao;

import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.io.Serializable;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import sv.edu.ues.occ.ingenieria.ppi115_2026.cafefe.entity.ProductoCaracteristica;

/**
 *
 * @author edenrvm
 */

@Stateless
public class ProductoCaracteristicaDAO extends AbstraccionDAO<ProductoCaracteristica> implements Serializable {

    @PersistenceContext(unitName = "CafefePU")
    private EntityManager em;

    public ProductoCaracteristicaDAO() {
        super(ProductoCaracteristica.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public List<ProductoCaracteristica> findByProducto(UUID idProducto) {
        if (idProducto == null) {
            return Collections.emptyList();
        }
        return em.createQuery(
                "SELECT p FROM ProductoCaracteristica p WHERE p.idProducto.idProducto = :id",
                ProductoCaracteristica.class)
                .setParameter("id", idProducto)
                .getResultList();
    }
}
