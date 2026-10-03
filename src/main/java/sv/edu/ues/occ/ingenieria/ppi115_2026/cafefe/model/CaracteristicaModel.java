package sv.edu.ues.occ.ingenieria.ppi115_2026.cafefe.model;

import jakarta.faces.application.FacesMessage;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;
import sv.edu.ues.occ.ingenieria.ppi115_2026.cafefe.dao.AbstraccionDAO;
import sv.edu.ues.occ.ingenieria.ppi115_2026.cafefe.dao.CaracteristicaDAO;
import sv.edu.ues.occ.ingenieria.ppi115_2026.cafefe.dao.TipoCaracteristicaDAO;
import sv.edu.ues.occ.ingenieria.ppi115_2026.cafefe.entity.Caracteristica;
import sv.edu.ues.occ.ingenieria.ppi115_2026.cafefe.entity.TipoCaracteristica;

/**
 *
 * @author edenrvm
 */

@Named("caracteristicaModel")
@ViewScoped
public class CaracteristicaModel extends AbstraccionModel<Caracteristica> implements Serializable {

    private static final long serialVersionUID = 1L;
    private static final Logger LOG = Logger.getLogger(CaracteristicaModel.class.getName());

    @Inject
    private CaracteristicaDAO caracteristicaDAO;
    @Inject
    private TipoCaracteristicaDAO tipoCaracteristicaDAO;

    private Caracteristica seleccionado;
    private String idTipoSeleccionado;
    private String idTipoOriginal;
    private List<TipoCaracteristica> tiposDisponibles = new ArrayList<>();

    @Override
    protected AbstraccionDAO<Caracteristica> getDAO() {
        return caracteristicaDAO;
    }

    @Override
    protected Caracteristica nuevoRegistro() {
        Caracteristica nuevo = new Caracteristica();
        nuevo.setIdCaracteristica(UUID.randomUUID());
        nuevo.setActivo(Boolean.TRUE);
        return nuevo;
    }

    @Override
    protected Object getIdRegistro(Caracteristica entidad) {
        return entidad != null ? entidad.getIdCaracteristica() : null;
    }

    @Override
    public void nuevo() {
        seleccionado = null;
        super.nuevo();
        idTipoSeleccionado = null;
        idTipoOriginal = null;
        cargarTiposDisponibles();
    }

    @Override
    public void editar(Caracteristica entidad) {
        super.editar(entidad);
        if (isEditando()) {
            TipoCaracteristica tipo = getRegistro().getIdTipoCaracteristica();
            idTipoOriginal = (tipo != null && tipo.getIdTipoCaracteristica() != null)
                    ? tipo.getIdTipoCaracteristica().toString() : null;
            idTipoSeleccionado = idTipoOriginal;
            cargarTiposDisponibles();
        }
    }

    @Override
    public void cancelar() {
        super.cancelar();
        seleccionado = null;
        idTipoSeleccionado = null;
        idTipoOriginal = null;
    }

    private void cargarTiposDisponibles() {
        try {
            tiposDisponibles = new ArrayList<>(tipoCaracteristicaDAO.findAll());
        } catch (RuntimeException e) {
            LOG.log(Level.SEVERE, "No se pudieron cargar los tipos", e);
            mostrarMensaje(FacesMessage.SEVERITY_ERROR, "No se pudieron cargar los tipos de característica.");
        }
    }

    @Override
    public void guardar() {
        if (getRegistro() != null) {
            getRegistro().setIdTipoCaracteristica(resolverTipo());
        }
        super.guardar();
    }

    private TipoCaracteristica resolverTipo() {
        if (idTipoSeleccionado == null || idTipoSeleccionado.isBlank()) {
            return null;
        }
        try {
            return tipoCaracteristicaDAO.buscarPorId(UUID.fromString(idTipoSeleccionado));
        } catch (RuntimeException e) {
            LOG.log(Level.SEVERE, "No se pudo recuperar el tipo", e);
            return null;
        }
    }

    @Override
    protected boolean validarRegistro() {
        Caracteristica c = getRegistro();
        if (c == null) {
            return false;
        }
        if (c.getNombre() == null || c.getNombre().trim().isEmpty()) {
            mostrarMensaje(FacesMessage.SEVERITY_WARN, "El nombre de la característica es obligatorio.");
            return false;
        }
        TipoCaracteristica tipo = c.getIdTipoCaracteristica();
        if (tipo == null) {
            mostrarMensaje(FacesMessage.SEVERITY_WARN, "Debe seleccionar un tipo de característica.");
            return false;
        }

        String er = tipo.getExpresionRegular();
        try {
            if (er == null || er.isEmpty()) {
                mostrarMensaje(FacesMessage.SEVERITY_ERROR,
                        "El tipo \"" + tipo.getNombre() + "\" no tiene expresión regular. "
                        + "Corríjalo en Tipos de Característica.");
                return false;
            }
            Pattern.compile(er);
        } catch (PatternSyntaxException e) {
            mostrarMensaje(FacesMessage.SEVERITY_ERROR,
                    "La expresión regular del tipo \"" + tipo.getNombre() + "\" no es válida ("
                    + e.getDescription() + "). Corríjala en Tipos de Característica.");
            return false;
        }

        boolean cambioTipo = !tipo.getIdTipoCaracteristica().toString().equals(idTipoOriginal);
        if (cambioTipo && !Boolean.TRUE.equals(tipo.getActivo())) {
            mostrarMensaje(FacesMessage.SEVERITY_ERROR,
                    "No se puede elegir un tipo de característica inactivo (\"" + tipo.getNombre() + "\").");
            return false;
        }
        return true;
    }

    @Override
    public List<Caracteristica> getRegistros() {
        return super.getRegistros();
    }

    public Caracteristica getSeleccionado() {
        return seleccionado;
    }

    public void setSeleccionado(Caracteristica seleccionado) {
        this.seleccionado = seleccionado;
    }

    public List<TipoCaracteristica> getTiposDisponibles() {
        return tiposDisponibles;
    }

    public String getIdTipoSeleccionado() {
        return idTipoSeleccionado;
    }

    public void setIdTipoSeleccionado(String id) {
        this.idTipoSeleccionado = id;
    }
}
