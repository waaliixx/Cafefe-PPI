package sv.edu.ues.occ.ingenieria.ppi115_2026.cafefe.dao;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import java.io.Serializable;
import java.util.Collections;
import java.util.List;
import sv.edu.ues.occ.ingenieria.ppi115_2026.cafefe.entity.Empleado;

/**
 *
 * @author edenrvm
 */

public class EmpleadoDAO extends AbstraccionDAO<Empleado> implements Serializable {

    @PersistenceContext(unitName = "cafefePU")
    private EntityManager em;

    public EmpleadoDAO() {
        super(Empleado.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public List<Empleado> findByNombre(String nombre) {
        if (nombre == null || nombre.trim().isEmpty() || getEntityManager() == null) {
            return Collections.emptyList();
        }
        try {
            TypedQuery<Empleado> query = getEntityManager().createNamedQuery("Empleado.findByNombre", Empleado.class);
            query.setParameter("nombre", nombre);
            return query.getResultList();
        } catch (Exception e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }

    public List<Empleado> findByApellido(String apellido) {
        if (apellido == null || apellido.trim().isEmpty() || getEntityManager() == null) {
            return Collections.emptyList();
        }
        try {
            TypedQuery<Empleado> query = getEntityManager().createNamedQuery("Empleado.findByApellido", Empleado.class);
            query.setParameter("apellido", apellido);
            return query.getResultList();
        } catch (Exception e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }

    public List<Empleado> findByActivo(Boolean activo) {
        if (activo == null || getEntityManager() == null) {
            return Collections.emptyList();
        }
        try {
            TypedQuery<Empleado> query = getEntityManager().createNamedQuery("Empleado.findByActivo", Empleado.class);
            query.setParameter("activo", activo);
            return query.getResultList();
        } catch (Exception e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }

}