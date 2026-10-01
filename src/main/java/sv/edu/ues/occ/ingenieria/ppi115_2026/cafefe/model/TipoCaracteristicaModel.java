package sv.edu.ues.occ.ingenieria.ppi115_2026.cafefe.model;

import jakarta.faces.application.FacesMessage;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import java.io.Serializable;
import java.util.UUID;
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

    @Override
    protected AbstraccionDAO<TipoCaracteristica> getDAO() {
        return tipoCaracteristicaDAO;
    }

    @Override
    protected TipoCaracteristica nuevoRegistro() {
        TipoCaracteristica nuevo = new TipoCaracteristica();
        nuevo.setIdTipoCaracteristica(UUID.randomUUID());
        nuevo.setActivo(Boolean.TRUE);
        nuevo.setExpresionRegular(".");
        return nuevo;
    }

    @Override
    protected Object getIdRegistro(TipoCaracteristica entidad) {
        return entidad != null ? entidad.getIdTipoCaracteristica() : null;
    }

    @Override
    protected boolean validarRegistro() {
        if (getRegistro() == null) {
            return false;
        }
        if (getRegistro().getNombre() == null || getRegistro().getNombre().trim().isEmpty()) {
            mostrarMensaje(FacesMessage.SEVERITY_WARN, "El nombre del tipo de característica es obligatorio.");
            return false;
        }
        return true;
    }
}
