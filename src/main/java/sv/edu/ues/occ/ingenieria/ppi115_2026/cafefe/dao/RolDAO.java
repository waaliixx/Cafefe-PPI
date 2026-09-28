package sv.edu.ues.occ.ingenieria.ppi115_2026.cafefe.dao;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import java.io.Serializable;
import java.util.Collections;
import java.util.List;
import sv.edu.ues.occ.ingenieria.ppi115_2026.cafefe.entity.Rol;

/**
 *
 * @author edenrvm
 */

public class RolDAO extends AbstraccionDAO<Rol> implements Serializable {

    @PersistenceContext(unitName = "cafefePU")
    private EntityManager em;

    public RolDAO() {
        super(Rol.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public List<Rol> findByNombre(String nombre) {
        if (nombre == null || nombre.trim().isEmpty() || getEntityManager() == null) {
            return Collections.emptyList();
        }
        try {
            TypedQuery<Rol> query = getEntityManager().createNamedQuery("Rol.findByNombre", Rol.class);
            query.setParameter("nombre", nombre);
            return query.getResultList();
        } catch (Exception e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }

    public List<Rol> findByActivo(Boolean activo) {
        if (activo == null || getEntityManager() == null) {
            return Collections.emptyList();
        }
        try {
            TypedQuery<Rol> query = getEntityManager().createNamedQuery("Rol.findByActivo", Rol.class);
            query.setParameter("activo", activo);
            return query.getResultList();
        } catch (Exception e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }

}
