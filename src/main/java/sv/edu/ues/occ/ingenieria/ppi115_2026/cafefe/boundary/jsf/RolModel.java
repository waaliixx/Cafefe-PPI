package sv.edu.ues.occ.ingenieria.ppi115_2026.cafefe.boundary.jsf;

import jakarta.faces.application.FacesMessage;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import java.io.Serializable;
import java.util.List;
import java.util.UUID;
import sv.edu.ues.occ.ingenieria.ppi115_2026.cafefe.control.AbstraccionDAO;
import sv.edu.ues.occ.ingenieria.ppi115_2026.cafefe.control.EmpleadoRolDAO;
import sv.edu.ues.occ.ingenieria.ppi115_2026.cafefe.control.RolDAO;
import sv.edu.ues.occ.ingenieria.ppi115_2026.cafefe.entity.Rol;

/**
 *
 * @author edenrvm
 */

@Named("rolModel")
@ViewScoped
public class RolModel extends AbstraccionModel<Rol> implements Serializable {

    private static final long serialVersionUID = 1L;

    @Inject
    private RolDAO rolDAO;
    @Inject
    private EmpleadoRolDAO empleadoRolDAO;
    private Rol seleccionado;

    @Override
    protected AbstraccionDAO<Rol> getDAO() {
        return rolDAO;
    }

    @Override
    protected Rol nuevoRegistro() {
        Rol nuevo = new Rol();
        nuevo.setIdRol(UUID.randomUUID());
        nuevo.setActivo(Boolean.TRUE);
        return nuevo;
    }

    @Override
    protected Object getIdRegistro(Rol entidad) {
        return entidad != null ? entidad.getIdRol() : null;
    }

    @Override
    protected boolean validarRegistro() {
        Rol rol = getRegistro();
        if (rol == null) {
            return false;
        }
        if (rol.getNombre() == null || rol.getNombre().trim().isEmpty()) {
            mostrarMensaje(FacesMessage.SEVERITY_WARN, "El nombre del rol es obligatorio.");
            return false;
        }
        return validarDesactivacion(rol);
    }

    private boolean validarDesactivacion(Rol rol) {
        if (!isEditando() || Boolean.TRUE.equals(rol.getActivo())) {
            return true;
        }
        Rol guardado = rolDAO.buscarPorId(rol.getIdRol());
        if (guardado == null || !Boolean.TRUE.equals(guardado.getActivo())) {
            return true;
        }
        long enUso = empleadoRolDAO.contarPorRol(rol.getIdRol());
        if (enUso > 0) {
            mostrarMensaje(FacesMessage.SEVERITY_ERROR,
                    "No se puede desactivar el rol \"" + rol.getNombre() + "\" porque está asignado a "
                    + enUso + (enUso == 1 ? " empleado." : " empleados."));
            return false;
        }
        return true;
    }

    @Override
    public void cancelar() {
        super.cancelar();
        seleccionado = null;
    }

    @Override
    public List<Rol> getRegistros() {
        return super.getRegistros();
    }

    public Rol getSeleccionado() {
        return seleccionado;
    }

    public void setSeleccionado(Rol seleccionado) {
        this.seleccionado = seleccionado;
    }
}
