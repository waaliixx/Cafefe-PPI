/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package sv.edu.ues.occ.ingenieria.ppi115_2026.cafefe.boundary.jsf;

import jakarta.faces.view.ViewScoped;
import jakarta.faces.application.FacesMessage;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import sv.edu.ues.occ.ingenieria.ppi115_2026.cafefe.control.AbstraccionDAO;
import sv.edu.ues.occ.ingenieria.ppi115_2026.cafefe.control.TipoProductoDAO;
import sv.edu.ues.occ.ingenieria.ppi115_2026.cafefe.entity.TipoProducto;
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
@Named("tpModel")
@ViewScoped
public class tipoProductoModel extends AbstraccionModel<TipoProducto> {

    @Inject
    private TipoProductoDAO tipoProductoDAO;

    private LazyDataModel<TipoProducto> registrosLazy;
    private TipoProducto seleccionado;

    @Override
    public void cargarRegistros() {
        // Evita que la inicialización y el guardado carguen toda la tabla.
        if (registrosLazy == null) {
            registrosLazy = new LazyDataModel<TipoProducto>() {
                @Override
                public int count(Map<String, FilterMeta> filtros) {
                    return tipoProductoDAO.count();
                }

                @Override
                public List<TipoProducto> load(int primero, int cantidad,
                        Map<String, SortMeta> orden, Map<String, FilterMeta> filtros) {
                    primero = recalculateFirst(primero, cantidad, getRowCount());
                    return tipoProductoDAO.findRange(new int[]{primero, primero + cantidad});
                }

                @Override
                public String getRowKey(TipoProducto entidad) {
                    return entidad.getIdTipoProducto().toString();
                }

                @Override
                public TipoProducto getRowData(String clave) {
                    try {
                        return tipoProductoDAO.buscarPorId(UUID.fromString(clave));
                    } catch (IllegalArgumentException e) {
                        return null;
                    }
                }
            };
        }
    }

    public LazyDataModel<TipoProducto> getRegistrosLazy() {
        return registrosLazy;
    }

    public TipoProducto getSeleccionado() {
        return seleccionado;
    }

    public void setSeleccionado(TipoProducto seleccionado) {
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
        if (seleccionado == null || seleccionado.getIdTipoProducto() == null) {
            mostrarMensaje(FacesMessage.SEVERITY_WARN, "Selecciona un tipo de producto para eliminar.");
            return;
        }
        TipoProducto actual = tipoProductoDAO.buscarPorId(seleccionado.getIdTipoProducto());
        if (actual == null) {
            mostrarMensaje(FacesMessage.SEVERITY_ERROR, "No se pudo recuperar el tipo de producto.");
            return;
        }
        super.eliminar(actual);
    }

    @Override
    protected boolean validarRegistro() {
        TipoProducto registro = getRegistro();
        if (registro == null || (!isCreando() && !isEditando())) {
            return false;
        }
        if (registro.getNombre() == null || registro.getNombre().isBlank()) {
            mostrarMensaje(FacesMessage.SEVERITY_WARN, "El nombre es obligatorio.");
            return false;
        }
        if (registro.getActivo() == null) {
            mostrarMensaje(FacesMessage.SEVERITY_WARN, "Indica si el tipo de producto está activo.");
            return false;
        }
        if (registro.getNombre().trim().length() > 155) {
            mostrarMensaje(FacesMessage.SEVERITY_WARN, "El nombre no puede superar 155 caracteres.");
            return false;
        }
        if (isEditando() && tipoProductoDAO.buscarPorId(registro.getIdTipoProducto()) == null) {
            mostrarMensaje(FacesMessage.SEVERITY_ERROR, "El registro ya no existe o no se pudo recuperar.");
            return false;
        }
        if (!validarDesactivacion(registro)) {
            return false;
        }
        registro.setNombre(registro.getNombre().trim());
        return true;
    }

    private boolean validarDesactivacion(TipoProducto registro) {
        if (!isEditando() || Boolean.TRUE.equals(registro.getActivo())) {
            return true;
        }
        TipoProducto guardado = tipoProductoDAO.buscarPorId(registro.getIdTipoProducto());
        if (guardado == null || !Boolean.TRUE.equals(guardado.getActivo())) {
            return true;
        }
        long enUso = tipoProductoDAO.contarProductos(registro.getIdTipoProducto());
        if (enUso > 0) {
            mostrarMensaje(FacesMessage.SEVERITY_ERROR,
                    "No se puede desactivar el tipo de producto \"" + registro.getNombre()
                    + "\" porque tiene " + enUso
                    + (enUso == 1 ? " producto asociado." : " productos asociados."));
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
    protected AbstraccionDAO<TipoProducto> getDAO() {
        return tipoProductoDAO;
    }

    @Override
    protected TipoProducto nuevoRegistro() {
        TipoProducto nuevo = new TipoProducto(UUID.randomUUID());
        nuevo.setActivo(true);
        return nuevo;
    }

    @Override
    protected Object getIdRegistro(TipoProducto entidad) {
        return entidad.getIdTipoProducto();
    }
}
