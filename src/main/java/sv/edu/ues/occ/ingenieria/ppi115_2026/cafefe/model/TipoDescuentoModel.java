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
import sv.edu.ues.occ.ingenieria.ppi115_2026.cafefe.dao.TipoDescuentoDAO;
import sv.edu.ues.occ.ingenieria.ppi115_2026.cafefe.entity.TipoDescuento;
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
@Named("tdModel")
@ViewScoped
public class TipoDescuentoModel extends AbstraccionModel<TipoDescuento> {
    
    @Inject
    private TipoDescuentoDAO tipoDescuentoDAO;

    private LazyDataModel<TipoDescuento> registrosLazy;
    private TipoDescuento seleccionado;

    @Override
    public void cargarRegistros() {
        // Evita que la inicialización y el guardado carguen toda la tabla.
        if (registrosLazy == null) {
            registrosLazy = new LazyDataModel<TipoDescuento>() {
                @Override
                public int count(Map<String, FilterMeta> filtros) {
                    return tipoDescuentoDAO.count();
                }

                @Override
                public List<TipoDescuento> load(int primero, int cantidad,
                        Map<String, SortMeta> orden, Map<String, FilterMeta> filtros) {
                    primero = recalculateFirst(primero, cantidad, getRowCount());
                    return tipoDescuentoDAO.findRange(new int[]{primero, primero + cantidad});
                }

                @Override
                public String getRowKey(TipoDescuento entidad) {
                    return entidad.getIdTipoDescuento().toString();
                }

                @Override
                public TipoDescuento getRowData(String clave) {
                    try {
                        return tipoDescuentoDAO.buscarPorId(UUID.fromString(clave));
                    } catch (IllegalArgumentException e) {
                        return null;
                    }
                }
            };
        }
    }

    public LazyDataModel<TipoDescuento> getRegistrosLazy() {
        return registrosLazy;
    }

    public TipoDescuento getSeleccionado() {
        return seleccionado;
    }

    public void setSeleccionado(TipoDescuento seleccionado) {
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
        if (seleccionado == null || seleccionado.getIdTipoDescuento() == null) {
            mostrarMensaje(FacesMessage.SEVERITY_WARN, "Selecciona un tipo de descuento para eliminar.");
            return;
        }
        TipoDescuento actual = tipoDescuentoDAO.buscarPorId(seleccionado.getIdTipoDescuento());
        if (actual == null) {
            mostrarMensaje(FacesMessage.SEVERITY_ERROR, "No se pudo recuperar el tipo de descuento.");
            return;
        }
        super.eliminar(actual);
    }

    @Override
    protected boolean validarRegistro() {
        TipoDescuento registro = getRegistro();
        if (registro == null || (!isCreando() && !isEditando())) {
            return false;
        }
        if (registro.getNombre() == null || registro.getNombre().isBlank()) {
            mostrarMensaje(FacesMessage.SEVERITY_WARN, "El nombre es obligatorio.");
            return false;
        }
        if (registro.getActivo() == null) {
            mostrarMensaje(FacesMessage.SEVERITY_WARN, "Indica si el tipo de descuento está activo.");
            return false;
        }
        if (registro.getDescuentoMaximo() != null && registro.getDescuentoMaximo() < 0) {
            mostrarMensaje(FacesMessage.SEVERITY_WARN, "El descuento máximo no puede ser negativo.");
            return false;
        }
        if (isEditando() && tipoDescuentoDAO.buscarPorId(registro.getIdTipoDescuento()) == null) {
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
    protected AbstraccionDAO<TipoDescuento> getDAO() {
        return tipoDescuentoDAO;
    }

    @Override
    protected TipoDescuento nuevoRegistro() {
        TipoDescuento nuevo = new TipoDescuento(UUID.randomUUID());
        nuevo.setActivo(true);
        return nuevo;
    }

    @Override
    protected Object getIdRegistro(TipoDescuento entidad) {
        return entidad.getIdTipoDescuento();
    }
}
