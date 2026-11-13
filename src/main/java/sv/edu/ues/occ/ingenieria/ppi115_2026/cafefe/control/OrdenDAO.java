package sv.edu.ues.occ.ingenieria.ppi115_2026.cafefe.control;

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
    
    /** Órdenes con al menos una línea sin factura vigente, es decir, pendientes de pago */
    static final String JPQL_PENDIENTES_DE_PAGO
            = "SELECT o FROM Orden o WHERE EXISTS (SELECT op.idOrdenProducto FROM OrdenProducto op WHERE op.idOrden = o "
            + "AND NOT EXISTS (SELECT f.idFacturaOrdenProducto FROM FacturaOrdenProducto f WHERE f.idOrdenProducto = op "
            + "AND (f.idFactura.estado IS NULL OR f.idFactura.estado <> :anulada))) "
            + "ORDER BY o.fechaCreacion";
    
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
    
    /**
     * Órdenes pendientes de pago, de la más antigua a la más reciente
     * Una orden sin productos no se considera pendiente.
     */
    public List<Orden> findPendientesDePago() {
        if (getEntityManager() == null) {
            return Collections.emptyList();
        }
        try {
            return getEntityManager().createQuery(JPQL_PENDIENTES_DE_PAGO, Orden.class)
                    .setParameter("anulada", OrdenProductoDAO.FACTURA_ANULADA)
                    .getResultList();
        } catch (Exception e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }
}
