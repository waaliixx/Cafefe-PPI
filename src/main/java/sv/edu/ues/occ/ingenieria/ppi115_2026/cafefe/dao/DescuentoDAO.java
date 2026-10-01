package sv.edu.ues.occ.ingenieria.ppi115_2026.cafefe.dao;

import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import java.io.Serializable;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import sv.edu.ues.occ.ingenieria.ppi115_2026.cafefe.entity.Descuento;

/**
 *
 * @author edenrvm
 */
@Stateless
public class DescuentoDAO extends AbstraccionDAO<Descuento> implements Serializable {

    @PersistenceContext(unitName = "cafefePU")
    private EntityManager em;

    public DescuentoDAO() {
        super(Descuento.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public List<Descuento> findByNombre(String nombre) {
        if (nombre == null || nombre.trim().isEmpty() || getEntityManager() == null) {
            return Collections.emptyList();
        }
        try {
            TypedQuery<Descuento> query = getEntityManager().createNamedQuery("Descuento.findByNombre", Descuento.class);
            query.setParameter("nombre", nombre);
            return query.getResultList();
        } catch (Exception e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }

    /**
     * Busca descuentos que inician en una fecha específica.
     */
    public List<Descuento> findByFechaDesde(Date fechaDesde) {
        if (fechaDesde == null || getEntityManager() == null) {
            return Collections.emptyList();
        }
        try {
            TypedQuery<Descuento> query = getEntityManager().createNamedQuery("Descuento.findByFechaDesde", Descuento.class);
            query.setParameter("fechaDesde", fechaDesde);
            return query.getResultList();
        } catch (Exception e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }

    /**
     * Busca descuentos que finalizan en una fecha específica.
     */
    public List<Descuento> findByFechaHasta(Date fechaHasta) {
        if (fechaHasta == null || getEntityManager() == null) {
            return Collections.emptyList();
        }
        try {
            TypedQuery<Descuento> query = getEntityManager().createNamedQuery("Descuento.findByFechaHasta", Descuento.class);
            query.setParameter("fechaHasta", fechaHasta);
            return query.getResultList();
        } catch (Exception e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }
    
    public List<Descuento> findVigentes() { //Busqueda descuentos vigentes
        if (getEntityManager() == null) {
            return Collections.emptyList();
        }
        try {
            Date hoy = new Date();
            TypedQuery<Descuento> query = getEntityManager().createQuery(
                "SELECT d FROM Descuento d WHERE d.fechaDesde <= :hoy AND (d.fechaHasta IS NULL OR d.fechaHasta >= :hoy)", 
                Descuento.class
            );
            query.setParameter("hoy", hoy);
            return query.getResultList();
        } catch (Exception e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }
}