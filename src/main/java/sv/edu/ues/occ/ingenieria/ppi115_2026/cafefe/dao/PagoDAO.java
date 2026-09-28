package sv.edu.ues.occ.ingenieria.ppi115_2026.cafefe.dao;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import java.io.Serializable;
import java.util.Collections;
import java.util.List;
import sv.edu.ues.occ.ingenieria.ppi115_2026.cafefe.entity.Factura;
import sv.edu.ues.occ.ingenieria.ppi115_2026.cafefe.entity.Pago;

/**
 *
 * @author edenrvm
 */

public class PagoDAO extends AbstraccionDAO<Pago> implements Serializable {

    @PersistenceContext(unitName = "cafefePU")
    private EntityManager em;

    public PagoDAO() {
        super(Pago.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public List<Pago> findByEstado(String estado) { //Pendiente, Pagado, Cancelado
        if (estado == null || estado.trim().isEmpty() || getEntityManager() == null) {
            return Collections.emptyList();
        }
        try {
            TypedQuery<Pago> query = getEntityManager().createNamedQuery("Pago.findByEstado", Pago.class);
            query.setParameter("estado", estado);
            return query.getResultList();
        } catch (Exception e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }

    public List<Pago> findByFactura(Factura factura) {
        if (factura == null || getEntityManager() == null) {
            return Collections.emptyList();
        }
        try {
            TypedQuery<Pago> query = getEntityManager().createQuery(
                "SELECT p FROM Pago p WHERE p.idFactura = :factura", 
                Pago.class
            );
            query.setParameter("factura", factura);
            return query.getResultList();
        } catch (Exception e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }
}