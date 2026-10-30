package sv.edu.ues.occ.ingenieria.ppi115_2026.cafefe.control;

import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.io.Serializable;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import sv.edu.ues.occ.ingenieria.ppi115_2026.cafefe.entity.EmpleadoRol;

/**
 *
 * @author edenrvm
 */
@Stateless
public class EmpleadoRolDAO extends AbstraccionDAO<EmpleadoRol> implements Serializable {

    @PersistenceContext(unitName = "CafefePU")
    private EntityManager em;

    public EmpleadoRolDAO() {
        super(EmpleadoRol.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public List<EmpleadoRol> findByEmpleado(UUID idEmpleado) {
        if (idEmpleado == null || em == null) {
            return Collections.emptyList();
        }
        try {
            return em.createQuery(
                    "SELECT er FROM EmpleadoRol er WHERE er.idEmpleado = :id", EmpleadoRol.class)
                    .setParameter("id", idEmpleado)
                    .getResultList();
        } catch (Exception e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }

    public long contarPorRol(UUID idRol) {
        if (idRol == null) {
            return 0;
        }
        return em.createQuery(
                "SELECT COUNT(er) FROM EmpleadoRol er WHERE er.idRol.idRol = :id", Long.class)
                .setParameter("id", idRol)
                .getSingleResult();
    }

    public List<EmpleadoRol> findActivosPorNombreRol(String nombreRol) {
        if (nombreRol == null || nombreRol.isBlank() || em == null) {
            return Collections.emptyList();
        }
        try {
            return em.createQuery(
                    "SELECT er FROM EmpleadoRol er "
                    + "WHERE LOWER(er.idRol.nombre) = :nombre AND er.activo = :activo", EmpleadoRol.class)
                    .setParameter("nombre", nombreRol.trim().toLowerCase())
                    .setParameter("activo", Boolean.TRUE)
                    .getResultList();
        } catch (Exception e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }

}
