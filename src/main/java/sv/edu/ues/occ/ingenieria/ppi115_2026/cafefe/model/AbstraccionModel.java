package sv.edu.ues.occ.ingenieria.ppi115_2026.cafefe.model;

import jakarta.annotation.PostConstruct;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import sv.edu.ues.occ.ingenieria.ppi115_2026.cafefe.dao.AbstraccionDAO;

/**
 * metodos del crud comunes por los models de vista.
 * Cada model concreto declara @Named y @ViewScoped e inyecta su DAO.
 * Las acciones de este model no agrupan transacciones entre varios DAO.
 *
 * @param <T> es la entidad administrada para cada vista
 * @author walixx
 */
public abstract class AbstraccionModel<T> implements Serializable {

    private static final long serialVersionUID = 1L;
    private static final Logger LOG = Logger.getLogger(AbstraccionModel.class.getName());

    private List<T> registros = new ArrayList<>();
    private T registro;
    private Estado estado = Estado.NINGUNO;

    protected abstract AbstraccionDAO<T> getDAO();

    /** Crea la entidad con sus valores iniciales e identificador, si corresponde. */
    protected abstract T nuevoRegistro();

    protected abstract Object getIdRegistro(T entidad);

    @PostConstruct
    public void inicializar() {
        cargarRegistros();
    }

    public void cargarRegistros() {
        try {
            registros = new ArrayList<>(getDAO().findAll());
        } catch (RuntimeException e) {
            informarError("No se pudieron cargar los registros.", e);
        }
    }

    public void nuevo() {
        registro = nuevoRegistro();
        estado = Estado.CREAR;
    }

    /** Recarga la entidad para que el formulario no modifique la fila de la lista. */
    public void editar(T entidad) {
        if (entidad == null || getIdRegistro(entidad) == null) {
            mostrarMensaje(FacesMessage.SEVERITY_WARN, "Selecciona un registro para editar.");
            return;
        }
        try {
            T encontrado = getDAO().buscarPorId(getIdRegistro(entidad));
            if (encontrado == null) {
                mostrarMensaje(FacesMessage.SEVERITY_ERROR, "No se pudo recuperar el registro.");
                return;
            }
            registro = encontrado;
            estado = Estado.EDITAR;
        } catch (RuntimeException e) {
            informarError("No se pudo recuperar el registro.", e);
        }
    }

    public void editar() {
        editar(registro);
    }

    /** Permite agregar validaciones propias del módulo antes de guardar. */
    protected boolean validarRegistro() {
        return true;
    }

    public void guardar() {
        if (registro == null || estado == Estado.NINGUNO) {
            mostrarMensaje(FacesMessage.SEVERITY_WARN, "Abre un registro para guardar.");
            return;
        }
        try {
            if (!validarRegistro()) {
                return;
            }
            boolean guardado = estado == Estado.CREAR
                    ? getDAO().crear(registro) : getDAO().modificar(registro);
            if (!guardado) {
                mostrarMensaje(FacesMessage.SEVERITY_ERROR, "No se pudo guardar el registro.");
                return;
            }
            cancelar();
            mostrarMensaje(FacesMessage.SEVERITY_INFO, "Registro guardado.");
            cargarRegistros();
        } catch (RuntimeException e) {
            informarError("No se pudo guardar el registro.", e);
        }
    }

    public void eliminar(T entidad) {
        if (entidad == null || getIdRegistro(entidad) == null) {
            mostrarMensaje(FacesMessage.SEVERITY_WARN, "Selecciona un registro para eliminar.");
            return;
        }
        try {
            if (!getDAO().eliminar(entidad)) {
                mostrarMensaje(FacesMessage.SEVERITY_ERROR, "No se pudo eliminar el registro.");
                return;
            }
            cancelar();
            mostrarMensaje(FacesMessage.SEVERITY_INFO, "Registro eliminado.");
            cargarRegistros();
        } catch (RuntimeException e) {
            informarError("No se pudo eliminar el registro.", e);
        }
    }

    public void eliminar() {
        eliminar(registro);
    }

    public void cancelar() {
        registro = null;
        estado = Estado.NINGUNO;
    }

    protected void mostrarMensaje(FacesMessage.Severity severidad, String texto) {
        FacesContext contexto = FacesContext.getCurrentInstance();
        if (contexto != null) {
            contexto.addMessage(null, new FacesMessage(severidad, texto, null));
        }
    }

    private void informarError(String texto, RuntimeException error) {
        LOG.log(Level.SEVERE, texto, error);
        mostrarMensaje(FacesMessage.SEVERITY_ERROR, texto);
    }

    public List<T> getRegistros() {
        return registros;
    }

    public T getRegistro() {
        return registro;
    }

    public void setRegistro(T registro) {
        this.registro = registro;
    }

    public Estado getEstado() {
        return estado;
    }

    public boolean isListado() {
        return estado == Estado.NINGUNO;
    }

    public boolean isCreando() {
        return estado == Estado.CREAR;
    }

    public boolean isEditando() {
        return estado == Estado.EDITAR;
    }

    public boolean isMostrarFormulario() {
        return estado != Estado.NINGUNO;
    }
}
