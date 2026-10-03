package sv.edu.ues.occ.ingenieria.ppi115_2026.cafefe.model;

import jakarta.faces.application.FacesMessage;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import java.io.Serializable;
import java.util.List;
import java.util.UUID;
import sv.edu.ues.occ.ingenieria.ppi115_2026.cafefe.dao.AbstraccionDAO;
import sv.edu.ues.occ.ingenieria.ppi115_2026.cafefe.dao.RolDAO;
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
        if (getRegistro() == null) {
            return false;
        }
        if (getRegistro().getNombre() == null || getRegistro().getNombre().trim().isEmpty()) {
            mostrarMensaje(FacesMessage.SEVERITY_WARN, "El nombre del rol es obligatorio.");
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