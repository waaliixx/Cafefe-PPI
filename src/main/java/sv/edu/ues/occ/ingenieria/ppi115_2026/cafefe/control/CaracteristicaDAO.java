package sv.edu.ues.occ.ingenieria.ppi115_2026.cafefe.control;

import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import java.io.Serializable;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import sv.edu.ues.occ.ingenieria.ppi115_2026.cafefe.entity.Caracteristica;
import sv.edu.ues.occ.ingenieria.ppi115_2026.cafefe.entity.TipoCaracteristica;

/**
 *
 * @author edenrvm
 */
@Stateless
public class CaracteristicaDAO extends AbstraccionDAO<Caracteristica> implements Serializable {

    @PersistenceContext(unitName = "CafefePU")
    private EntityManager em;

    public CaracteristicaDAO() {
        super(Caracteristica.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public List<Caracteristica> findByNombre(String nombre) {
        if (nombre == null || getEntityManager() == null) {
            return Collections.emptyList();
        }
        try {
            TypedQuery<Caracteristica> query = getEntityManager().createNamedQuery("Caracteristica.findByNombre", Caracteristica.class);
            query.setParameter("nombre", nombre);
            return query.getResultList();
        } catch (Exception e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }

    public List<Caracteristica> findByActivo(Boolean activo) {
        if (activo == null || getEntityManager() == null) {
            return Collections.emptyList();
        }
        try {
            TypedQuery<Caracteristica> query = getEntityManager().createNamedQuery("Caracteristica.findByActivo", Caracteristica.class);
            query.setParameter("activo", activo);
            return query.getResultList();
        } catch (Exception e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }

    public List<Caracteristica> findByTipoCaracteristica(TipoCaracteristica tipoCaracteristica) {
        if (tipoCaracteristica == null || getEntityManager() == null) {
            return Collections.emptyList();
        }
        try {
            TypedQuery<Caracteristica> query = getEntityManager().createQuery(
                "SELECT c FROM Caracteristica c WHERE c.idTipoCaracteristica = :tipoCaracteristica", 
                Caracteristica.class
            );
            query.setParameter("tipoCaracteristica", tipoCaracteristica);
            return query.getResultList();
        } catch (Exception e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }
    
    public long contarPorTipo(UUID idTipoCaracteristica) {
    if (idTipoCaracteristica == null) {
        return 0;
    }
    return em.createQuery(
            "SELECT COUNT(c) FROM Caracteristica c "
            + "WHERE c.idTipoCaracteristica.idTipoCaracteristica = :id", Long.class)
            .setParameter("id", idTipoCaracteristica)
            .getSingleResult();
}
}
