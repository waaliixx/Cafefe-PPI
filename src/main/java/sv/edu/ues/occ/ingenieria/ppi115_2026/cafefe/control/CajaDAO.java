package sv.edu.ues.occ.ingenieria.ppi115_2026.cafefe.control;

import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import java.io.Serializable;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import sv.edu.ues.occ.ingenieria.ppi115_2026.cafefe.entity.Caja;

/**
 *
 * @author edenrvm
 */
@Stateless
public class CajaDAO extends AbstraccionDAO<Caja> implements Serializable {

    @PersistenceContext(unitName = "CafefePU")
    private EntityManager em;

    public CajaDAO() {
        super(Caja.class);
    }

    static final String JPQL_CONTAR_FACTURAS
            = "SELECT COUNT(f) FROM Factura f WHERE f.idCaja.idCaja = :id";

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public List<Caja> findByNombre(String nombre) {
        if (nombre == null || nombre.trim().isEmpty() || getEntityManager() == null) {
            return Collections.emptyList();
        }
        try {
            TypedQuery<Caja> query = getEntityManager().createNamedQuery("Caja.findByNombre", Caja.class);
            query.setParameter("nombre", nombre);
            return query.getResultList();
        } catch (Exception e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }

    public List<Caja> findByActivo(Boolean activo) {
        if (activo == null || getEntityManager() == null) {
            return Collections.emptyList();
        }
        try {
            TypedQuery<Caja> query = getEntityManager().createNamedQuery("Caja.findByActivo", Caja.class);
            query.setParameter("activo", activo);
            return query.getResultList();
        } catch (Exception e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }
    
    /** Cantidad de facturas emitidas en la caja; sirve para decidir si se puede eliminar. */
    public long contarFacturas(UUID idCaja) {
        if (idCaja == null || getEntityManager() == null) {
            return 0;
        }
        return getEntityManager().createQuery(JPQL_CONTAR_FACTURAS, Long.class)
                .setParameter("id", idCaja)
                .getSingleResult();
    }
}
