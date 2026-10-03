
package sv.edu.ues.occ.ingenieria.ppi115_2026.cafefe.model;

import jakarta.faces.application.FacesMessage;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import java.io.Serializable;
import java.util.List;
import java.util.UUID;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;
import sv.edu.ues.occ.ingenieria.ppi115_2026.cafefe.dao.AbstraccionDAO;
import sv.edu.ues.occ.ingenieria.ppi115_2026.cafefe.dao.TipoCaracteristicaDAO;
import sv.edu.ues.occ.ingenieria.ppi115_2026.cafefe.entity.TipoCaracteristica;

/**
 *
 * @author edenrvm
 */

@Named("tipoCaracteristicaModel")
@ViewScoped
public class TipoCaracteristicaModel extends AbstraccionModel<TipoCaracteristica> implements Serializable {

    private static final long serialVersionUID = 1L;

    @Inject
    private TipoCaracteristicaDAO tipoCaracteristicaDAO;

    /** Fila seleccionada en la tabla (para Editar / Eliminar). */
    private TipoCaracteristica seleccionado;

    @Override
    protected AbstraccionDAO<TipoCaracteristica> getDAO() {
        return tipoCaracteristicaDAO;
    }

    @Override
    protected TipoCaracteristica nuevoRegistro() {
        TipoCaracteristica nuevo = new TipoCaracteristica();
        nuevo.setIdTipoCaracteristica(UUID.randomUUID());
        nuevo.setActivo(Boolean.TRUE);
        nuevo.setExpresionRegular(".*");   // acepta cualquier valor
        return nuevo;
    }

    @Override
    protected Object getIdRegistro(TipoCaracteristica entidad) {
        return entidad != null ? entidad.getIdTipoCaracteristica() : null;
    }

    @Override
    protected boolean validarRegistro() {
        TipoCaracteristica t = getRegistro();
        if (t == null) {
            return false;
        }
        if (t.getNombre() == null || t.getNombre().trim().isEmpty()) {
            mostrarMensaje(FacesMessage.SEVERITY_WARN, "El nombre del tipo de característica es obligatorio.");
            return false;
        }
        if (t.getExpresionRegular() == null || t.getExpresionRegular().isEmpty()) {
            mostrarMensaje(FacesMessage.SEVERITY_WARN,
                    "La expresión regular es obligatoria (use .* para aceptar cualquier valor).");
            return false;
        }
        try {
            Pattern.compile(t.getExpresionRegular());
        } catch (PatternSyntaxException e) {
            mostrarMensaje(FacesMessage.SEVERITY_ERROR,
                    "La expresión regular no es válida: " + e.getDescription());
            return false;
        }
        return true;
    }

    @Override
    public void nuevo() {
        seleccionado = null;
        super.nuevo();
    }

    @Override
    public void cancelar() {
        super.cancelar();
        seleccionado = null;
    }

    @Override
    public List<TipoCaracteristica> getRegistros() {
        return super.getRegistros();
    }

    public TipoCaracteristica getSeleccionado() {
        return seleccionado;
    }

    public void setSeleccionado(TipoCaracteristica seleccionado) {
        this.seleccionado = seleccionado;
    }
}