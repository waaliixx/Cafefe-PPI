
package sv.edu.ues.occ.ingenieria.ppi115_2026.cafefe.boundary.jsf;

import jakarta.faces.application.FacesMessage;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import sv.edu.ues.occ.ingenieria.ppi115_2026.cafefe.control.AbstraccionDAO;
import sv.edu.ues.occ.ingenieria.ppi115_2026.cafefe.control.EmpleadoDAO;
import sv.edu.ues.occ.ingenieria.ppi115_2026.cafefe.control.EmpleadoRolDAO;
import sv.edu.ues.occ.ingenieria.ppi115_2026.cafefe.control.RolDAO;
import sv.edu.ues.occ.ingenieria.ppi115_2026.cafefe.entity.Empleado;
import sv.edu.ues.occ.ingenieria.ppi115_2026.cafefe.entity.EmpleadoRol;
import sv.edu.ues.occ.ingenieria.ppi115_2026.cafefe.entity.Rol;

/**
 *
 * @author edenrvm
 */

@Named("empleadoModel")
@ViewScoped
public class EmpleadoModel extends AbstraccionModel<Empleado> implements Serializable {

    private static final long serialVersionUID = 1L;
    private static final Logger LOG = Logger.getLogger(EmpleadoModel.class.getName());

    @Inject
    private EmpleadoDAO empleadoDAO;
    @Inject
    private EmpleadoRolDAO empleadoRolDAO;
    @Inject
    private RolDAO rolDAO;

    /** Fila seleccionada en la tabla (para Editar / Eliminar). */
    private Empleado seleccionado;
    /** ID (como texto) del único rol elegido en el formulario; vacío = sin rol. */
    private String idRolSeleccionado;
    private List<Rol> rolesDisponibles = new ArrayList<>();
    private Map<UUID, String> rolesPorEmpleado = new HashMap<>();

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
    public void nuevo() {
        seleccionado = null;
        super.nuevo();
        idRolSeleccionado = null;
        cargarRolesDisponibles();
    }

    @Override
    public void editar(Empleado entidad) {
        super.editar(entidad);
        if (isEditando()) {
            idRolSeleccionado = rolActual(getRegistro().getIdEmpleado());
            cargarRolesDisponibles();
        }
    }

    @Override
    public void cancelar() {
        super.cancelar();
        idRolSeleccionado = null;
    }

    private void cargarRolesDisponibles() {
        try {
            rolesDisponibles = new ArrayList<>(rolDAO.findAll());
        } catch (RuntimeException e) {
            LOG.log(Level.SEVERE, "No se pudieron cargar los roles", e);
            mostrarMensaje(FacesMessage.SEVERITY_ERROR, "No se pudieron cargar los roles.");
        }
    }

    private String rolActual(UUID idEmpleado) {
        for (EmpleadoRol er : empleadoRolDAO.findByEmpleado(idEmpleado)) {
            if (er.getIdRol() != null && er.getIdRol().getIdRol() != null) {
                return er.getIdRol().getIdRol().toString();
            }
        }
        return null;
    }

    private static String normalizar(String id) {
        return (id == null || id.isBlank()) ? null : id.trim();
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
        return validarRolActivo();
    }

    private boolean validarRolActivo() {
        String elegido = normalizar(idRolSeleccionado);
        if (elegido == null) {
            return true;
        }
        try {
            String actual = isEditando() ? rolActual(getRegistro().getIdEmpleado()) : null;
            if (elegido.equals(actual)) {
                return true;
            }
            Rol rol = rolDAO.buscarPorId(UUID.fromString(elegido));
            if (rol == null) {
                mostrarMensaje(FacesMessage.SEVERITY_ERROR, "El rol elegido ya no existe.");
                return false;
            }
            if (!Boolean.TRUE.equals(rol.getActivo())) {
                mostrarMensaje(FacesMessage.SEVERITY_ERROR,
                        "No se puede elegir un rol inactivo (\"" + rol.getNombre()
                        + "\"). Elija un rol activo en la pestaña Roles.");
                return false;
            }
        } catch (RuntimeException e) {
            LOG.log(Level.SEVERE, "Error al validar el rol", e);
            mostrarMensaje(FacesMessage.SEVERITY_ERROR, "No se pudo validar el rol.");
            return false;
        }
        return true;
    }

    @Override
    public void guardar() {
        Empleado emp = getRegistro();
        if (emp == null) {
            super.guardar();
            return;
        }
        UUID idEmpleado = emp.getIdEmpleado();
        String rolElegido = normalizar(idRolSeleccionado);

        super.guardar();  

        if (getRegistro() == null) {
            sincronizarRol(idEmpleado, rolElegido);
            cargarResumenRoles();
            seleccionado = null;
        }
    }

    private void sincronizarRol(UUID idEmpleado, String idRolElegido) {
        boolean conservado = false;
        try {
            for (EmpleadoRol er : empleadoRolDAO.findByEmpleado(idEmpleado)) {
                String idRol = (er.getIdRol() != null && er.getIdRol().getIdRol() != null)
                        ? er.getIdRol().getIdRol().toString() : null;
                if (!conservado && idRol != null && idRol.equals(idRolElegido)) {
                    conservado = true;
                } else if (!empleadoRolDAO.eliminar(er)) {
                    mostrarMensaje(FacesMessage.SEVERITY_ERROR, "No se pudo quitar el rol anterior.");
                }
            }
            if (!conservado && idRolElegido != null) {
                Rol rol = rolDAO.buscarPorId(UUID.fromString(idRolElegido));
                if (rol != null) {
                    EmpleadoRol nuevo = new EmpleadoRol(UUID.randomUUID());
                    nuevo.setIdEmpleado(idEmpleado);
                    nuevo.setIdRol(rol);
                    nuevo.setActivo(Boolean.TRUE);
                    if (!empleadoRolDAO.crear(nuevo)) {
                        mostrarMensaje(FacesMessage.SEVERITY_ERROR,
                                "No se pudo asignar el rol \"" + rol.getNombre() + "\".");
                    }
                }
            }
        } catch (RuntimeException e) {
            LOG.log(Level.SEVERE, "Error al sincronizar el rol", e);
            mostrarMensaje(FacesMessage.SEVERITY_ERROR,
                    "El empleado se guardó, pero hubo un problema con su rol "
                    + "(un rol quitado puede tener órdenes o facturas asociadas).");
        }
    }

    @Override
    public void eliminar(Empleado entidad) {
        if (entidad != null && entidad.getIdEmpleado() != null) {
            try {
                for (EmpleadoRol er : empleadoRolDAO.findByEmpleado(entidad.getIdEmpleado())) {
                    if (!empleadoRolDAO.eliminar(er)) {
                        mostrarMensaje(FacesMessage.SEVERITY_ERROR,
                                "No se pudo quitar el rol del empleado.");
                        return;
                    }
                }
            } catch (RuntimeException e) {
                LOG.log(Level.SEVERE, "Error al quitar el rol del empleado", e);
                mostrarMensaje(FacesMessage.SEVERITY_ERROR,
                        "No se puede eliminar: el empleado tiene órdenes o facturas asociadas.");
                return;
            }
        }
        super.eliminar(entidad);
        seleccionado = null;
    }
    
    @Override
    public void cargarRegistros() {
        super.cargarRegistros();
        cargarResumenRoles();
    }

    private void cargarResumenRoles() {
        rolesPorEmpleado = new HashMap<>();
        try {
            for (EmpleadoRol er : empleadoRolDAO.findAll()) {
                if (er.getIdEmpleado() != null && er.getIdRol() != null) {
                    rolesPorEmpleado.merge(er.getIdEmpleado(), er.getIdRol().getNombre(),
                            (a, b) -> a + ", " + b);
                }
            }
        } catch (RuntimeException e) {
            LOG.log(Level.SEVERE, "No se pudo cargar el resumen de roles", e);
        }
    }

    public String getRolTexto(Empleado e) {
        String texto = e == null ? null : rolesPorEmpleado.get(e.getIdEmpleado());
        return texto == null ? "Sin rol" : texto;
    }

    @Override
    public List<Empleado> getRegistros() {
        return super.getRegistros();
    }

    public Empleado getSeleccionado() { return seleccionado; }
    public void setSeleccionado(Empleado seleccionado) { this.seleccionado = seleccionado; }
    public List<Rol> getRolesDisponibles() { return rolesDisponibles; }
    public String getIdRolSeleccionado() { return idRolSeleccionado; }
    public void setIdRolSeleccionado(String id) { this.idRolSeleccionado = id; }
}