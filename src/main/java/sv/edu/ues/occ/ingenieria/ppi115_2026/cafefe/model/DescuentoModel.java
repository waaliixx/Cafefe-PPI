/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package sv.edu.ues.occ.ingenieria.ppi115_2026.cafefe.model;

import jakarta.faces.view.ViewScoped;
import jakarta.faces.application.FacesMessage;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import sv.edu.ues.occ.ingenieria.ppi115_2026.cafefe.dao.AbstraccionDAO;
import sv.edu.ues.occ.ingenieria.ppi115_2026.cafefe.dao.DescuentoDAO;
import sv.edu.ues.occ.ingenieria.ppi115_2026.cafefe.entity.Descuento;
import sv.edu.ues.occ.ingenieria.ppi115_2026.cafefe.entity.TipoDescuento;
import sv.edu.ues.occ.ingenieria.ppi115_2026.cafefe.dao.TipoDescuentoDAO;
import java.util.UUID;
import java.util.List;
import java.util.Map;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.FilterMeta;
import org.primefaces.model.SortMeta;
/**
 *
 * @author walixx
 */
@Named("dmodel")
@ViewScoped
public class DescuentoModel extends AbstraccionModel<Descuento> {
    
    @Inject
    private DescuentoDAO descuentoDAO;

    @Inject
    private TipoDescuentoDAO tipoDescuentoDAO;

    private List<TipoDescuento> tiposDescuento;

    public List<TipoDescuento> getTiposDescuento() {
        if (tiposDescuento == null) {
            tiposDescuento = tipoDescuentoDAO.findAll();
        }
        return tiposDescuento;
    }

    public String getIdTipoDescuentoSeleccionado() {
        return getRegistro() == null || getRegistro().getIdTipoDescuento() == null
                ? null : getRegistro().getIdTipoDescuento().toString();
    }

    public void setIdTipoDescuentoSeleccionado(String id) {
        if (getRegistro() != null) {
            getRegistro().setIdTipoDescuento(id == null || id.isBlank() ? null : UUID.fromString(id));
        }
    }

    public String nombreTipoDescuento(UUID id) {
        return getTiposDescuento().stream()
                .filter(tipo -> tipo.getIdTipoDescuento().equals(id))
                .map(TipoDescuento::getNombre).findFirst().orElse("");
    }

    private LazyDataModel<Descuento> registrosLazy;
    private Descuento seleccionado;

    @Override
    public void cargarRegistros() {
        // Evita que la inicialización y el guardado carguen toda la tabla.
        if (registrosLazy == null) {
            registrosLazy = new LazyDataModel<Descuento>() {
                @Override
                public int count(Map<String, FilterMeta> filtros) {
                    return descuentoDAO.count();
                }

                @Override
                public List<Descuento> load(int primero, int cantidad,
                        Map<String, SortMeta> orden, Map<String, FilterMeta> filtros) {
                    primero = recalculateFirst(primero, cantidad, getRowCount());
                    return descuentoDAO.findRange(new int[]{primero, primero + cantidad});
                }

                @Override
                public String getRowKey(Descuento entidad) {
                    return entidad.getIdDescuento().toString();
                }

                @Override
                public Descuento getRowData(String clave) {
                    try {
                        return descuentoDAO.buscarPorId(UUID.fromString(clave));
                    } catch (IllegalArgumentException e) {
                        return null;
                    }
                }
            };
        }
    }

    public LazyDataModel<Descuento> getRegistrosLazy() {
        return registrosLazy;
    }

    public Descuento getSeleccionado() {
        return seleccionado;
    }

    public void setSeleccionado(Descuento seleccionado) {
        this.seleccionado = seleccionado;
    }

    @Override
    public void nuevo() {
        if (isListado()) {
            seleccionado = null;
            super.nuevo();
        }
    }

    public void modificarSeleccionado() {
        if (isListado()) {
            super.editar(seleccionado);
        }
    }

    public void eliminarSeleccionado() {
        if (!isListado()) {
            mostrarMensaje(FacesMessage.SEVERITY_WARN, "Cancela o guarda el formulario antes de eliminar.");
            return;
        }
        if (seleccionado == null || seleccionado.getIdDescuento() == null) {
            mostrarMensaje(FacesMessage.SEVERITY_WARN, "Selecciona un descuento para eliminar.");
            return;
        }
        Descuento actual = descuentoDAO.buscarPorId(seleccionado.getIdDescuento());
        if (actual == null) {
            mostrarMensaje(FacesMessage.SEVERITY_ERROR, "No se pudo recuperar el descuento.");
            return;
        }
        super.eliminar(actual);
    }

    @Override
    protected boolean validarRegistro() {
        Descuento registro = getRegistro();
        if (registro == null || (!isCreando() && !isEditando())) {
            return false;
        }
        if (registro.getNombre() == null || registro.getNombre().isBlank()) {
            mostrarMensaje(FacesMessage.SEVERITY_WARN, "El nombre es obligatorio.");
            return false;
        }
        if (registro.getNombre().trim().length() > 155) {
            mostrarMensaje(FacesMessage.SEVERITY_WARN, "El nombre no puede superar 155 caracteres.");
            return false;
        }
        if (registro.getIdTipoDescuento() == null
                || tipoDescuentoDAO.buscarPorId(registro.getIdTipoDescuento()) == null) {
            mostrarMensaje(FacesMessage.SEVERITY_WARN, "Selecciona un tipo de descuento válido.");
            return false;
        }
        if (registro.getFechaDesde() == null) {
            mostrarMensaje(FacesMessage.SEVERITY_WARN, "La fecha de inicio es obligatoria.");
            return false;
        }
        if (registro.getFechaHasta() != null
                && registro.getFechaHasta().before(registro.getFechaDesde())) {
            mostrarMensaje(FacesMessage.SEVERITY_WARN, "La fecha final no puede ser anterior a la fecha de inicio.");
            return false;
        }
        if (isEditando() && descuentoDAO.buscarPorId(registro.getIdDescuento()) == null) {
            mostrarMensaje(FacesMessage.SEVERITY_ERROR, "El registro ya no existe o no se pudo recuperar.");
            return false;
        }
        registro.setNombre(registro.getNombre().trim());
        return true;
    }

    @Override
    public void cancelar() {
        super.cancelar();
        seleccionado = null;
    }

    @Override
    protected AbstraccionDAO<Descuento> getDAO() {
        return descuentoDAO;
    }

    @Override
    protected Descuento nuevoRegistro() {
        Descuento nuevo = new Descuento(UUID.randomUUID());
        return nuevo;
    }

    @Override
    protected Object getIdRegistro(Descuento entidad) {
        return entidad.getIdDescuento();
    }
}
