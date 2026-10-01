package sv.edu.ues.occ.ingenieria.ppi115_2026.cafefe.model;

import jakarta.faces.application.FacesMessage;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import java.io.Serializable;
import java.util.List;
import java.util.UUID;
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

    @Inject
    private CaracteristicaDAO caracteristicaDAO;

    @Inject
    private TipoCaracteristicaDAO tipoCaracteristicaDAO;

    private List<TipoCaracteristica> listaTipoCaracteristica;

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
    protected boolean validarRegistro() {
        if (getRegistro() == null) {
            return false;
        }
        
        if (getRegistro().getNombre() == null || getRegistro().getNombre().trim().isEmpty()) {
            mostrarMensaje(FacesMessage.SEVERITY_WARN, "El nombre de la característica es obligatorio.");
            return false;
        }

        if (getRegistro().getIdTipoCaracteristica() == null) {
            mostrarMensaje(FacesMessage.SEVERITY_WARN, "Debe seleccionar un tipo de característica.");
            return false;
        }

        return true;
    }

    public List<TipoCaracteristica> getListaTipoCaracteristica() {
        if (listaTipoCaracteristica == null && tipoCaracteristicaDAO != null) {
            try {
                listaTipoCaracteristica = tipoCaracteristicaDAO.findAll();
            } catch (RuntimeException e) {
                mostrarMensaje(FacesMessage.SEVERITY_ERROR, "No se pudieron cargar los tipos de características.");
            }
        }
        return listaTipoCaracteristica;
    }

    public void setListaTipoCaracteristica(List<TipoCaracteristica> listaTipoCaracteristica) {
        this.listaTipoCaracteristica = listaTipoCaracteristica;
    }
}
