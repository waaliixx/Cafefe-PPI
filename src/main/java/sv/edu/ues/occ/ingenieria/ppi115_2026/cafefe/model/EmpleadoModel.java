package sv.edu.ues.occ.ingenieria.ppi115_2026.cafefe.model;

import jakarta.faces.application.FacesMessage;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import java.io.Serializable;
import java.util.UUID;
import sv.edu.ues.occ.ingenieria.ppi115_2026.cafefe.dao.AbstraccionDAO;
import sv.edu.ues.occ.ingenieria.ppi115_2026.cafefe.dao.EmpleadoDAO;
import sv.edu.ues.occ.ingenieria.ppi115_2026.cafefe.entity.Empleado;

/**
 *
 * @author edenrvm
 */

@Named("empleadoModel")
@ViewScoped
public class EmpleadoModel extends AbstraccionModel<Empleado> implements Serializable {

    private static final long serialVersionUID = 1L;

    @Inject
    private EmpleadoDAO empleadoDAO;

    @Override
    protected AbstraccionDAO<Empleado> getDAO() {
        return empleadoDAO;
    }

    @Override
    protected Empleado nuevoRegistro() {
        Empleado nuevo = new Empleado();
        nuevo.setIdEmpleado(UUID.randomUUID());
        nuevo.setActivo(Boolean.TRUE);
        return nuevo;
    }

    @Override
    protected Object getIdRegistro(Empleado entidad) {
        return entidad != null ? entidad.getIdEmpleado() : null;
    }

    @Override
    protected boolean validarRegistro() {
        if (getRegistro() == null) {
            return false;
        }

        if (getRegistro().getNombre() == null || getRegistro().getNombre().trim().isEmpty()) {
            mostrarMensaje(FacesMessage.SEVERITY_WARN, "El nombre del empleado es obligatorio.");
            return false;
        }

        if (getRegistro().getApellido() == null || getRegistro().getApellido().trim().isEmpty()) {
            mostrarMensaje(FacesMessage.SEVERITY_WARN, "El apellido del empleado es obligatorio.");
            return false;
        }

        return true;
    }
}
