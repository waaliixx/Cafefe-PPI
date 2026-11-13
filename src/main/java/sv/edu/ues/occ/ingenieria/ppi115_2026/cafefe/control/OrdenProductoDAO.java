package sv.edu.ues.occ.ingenieria.ppi115_2026.cafefe.control;

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

    /** Estado de factura que libera las líneas para volver a cobrarse. */
    public static final String FACTURA_ANULADA = "Anulada";

    /** Líneas de una orden que no pertenecen a ninguna factura vigente (no anulada). */
    static final String JPQL_NO_FACTURADAS
            = "SELECT op FROM OrdenProducto op WHERE op.idOrden.idOrden = :id "
            + "AND NOT EXISTS (SELECT f.idFacturaOrdenProducto FROM FacturaOrdenProducto f WHERE f.idOrdenProducto = op "
            + "AND (f.idFactura.estado IS NULL OR f.idFactura.estado <> :anulada)) "
            + "ORDER BY op.idOrdenProducto";
    
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
    
    /**
     * Líneas de la orden que aún no han sido cobradas: no están en ninguna
     * factura, o solo están en facturas anuladas.
     */
    public List<OrdenProducto> findNoFacturadasByOrden(UUID idOrden) {
        if (idOrden == null || getEntityManager() == null) {
            return Collections.emptyList();
        }
        try {
            return getEntityManager().createQuery(JPQL_NO_FACTURADAS, OrdenProducto.class)
                    .setParameter("id", idOrden)
                    .setParameter("anulada", FACTURA_ANULADA)
                    .getResultList();
        } catch (Exception e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }
}
