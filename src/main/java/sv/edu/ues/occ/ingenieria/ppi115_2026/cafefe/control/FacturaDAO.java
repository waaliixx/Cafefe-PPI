package sv.edu.ues.occ.ingenieria.ppi115_2026.cafefe.control;

import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import java.io.Serializable;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import sv.edu.ues.occ.ingenieria.ppi115_2026.cafefe.entity.Factura;

/**
 *
 * @author edenrvm
 */
@Stateless
public class FacturaDAO extends AbstraccionDAO<Factura> implements Serializable {

    @PersistenceContext(unitName = "CafefePU")
    private EntityManager em;

    public FacturaDAO() {
        super(Factura.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public List<Factura> findByCliente(String cliente) {
        if (cliente == null || cliente.trim().isEmpty() || getEntityManager() == null) {
            return Collections.emptyList();
        }
        try {
            TypedQuery<Factura> query = getEntityManager().createNamedQuery("Factura.findByCliente", Factura.class);
            query.setParameter("cliente", cliente);
            return query.getResultList();
        } catch (Exception e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }


    public List<Factura> findByFechaFacturacion(Date fechaFacturacion) {
        if (fechaFacturacion == null || getEntityManager() == null) {
            return Collections.emptyList();
        }
        try {
            TypedQuery<Factura> query = getEntityManager().createNamedQuery("Factura.findByFechaFacturacion", Factura.class);
            query.setParameter("fechaFacturacion", fechaFacturacion);
            return query.getResultList();
        } catch (Exception e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }

    public List<Factura> findByEstado(String estado) { //Pagada, Anulada, Pendiente
        if (estado == null || estado.trim().isEmpty() || getEntityManager() == null) {
            return Collections.emptyList();
        }
        try {
            TypedQuery<Factura> query = getEntityManager().createNamedQuery("Factura.findByEstado", Factura.class);
            query.setParameter("estado", estado);
            return query.getResultList();
        } catch (Exception e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }

}