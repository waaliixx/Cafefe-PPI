/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package sv.edu.ues.occ.ingenieria.ppi115_2026.cafefe.boundary.jsf;

import jakarta.faces.application.FacesMessage;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import java.io.Serializable;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import sv.edu.ues.occ.ingenieria.ppi115_2026.cafefe.control.AbstraccionDAO;
import sv.edu.ues.occ.ingenieria.ppi115_2026.cafefe.control.CajaDAO;
import sv.edu.ues.occ.ingenieria.ppi115_2026.cafefe.entity.Caja;

/**
 *
 * @author walixx
 */
/**
 * Administración de las cajas donde se cobran las órdenes.
 * Una caja inactiva no admite cobros nuevos; una con facturas no se puede eliminar.
 */
@Named("cajaModel")
@ViewScoped
public class CajaModel extends AbstraccionModel<Caja> implements Serializable{

    private static final long serialVersionUID = 1L;
    private static final Logger LOG = Logger.getLogger(CajaModel.class.getName());
    static final int MAX_NOMBRE = 155;

    @Inject
    private CajaDAO cajaDAO;
    private Caja seleccionado;
    @Override
    protected AbstraccionDAO<Caja> getDAO() {
        return cajaDAO;
    }

    @Override
    protected Caja nuevoRegistro() {
        Caja nueva = new Caja(UUID.randomUUID());
        nueva.setActivo(Boolean.TRUE);
        return nueva;
    }

    @Override
    protected Object getIdRegistro(Caja entidad) {
        return entidad != null ? entidad.getIdCaja() : null;
    }
    
    @Override
    protected boolean validarRegistro() {
        Caja caja = getRegistro();
        if (caja == null) {
            return false;
        }
        String nombre = caja.getNombre() == null ? "" : caja.getNombre().trim();
        if (nombre.isEmpty()) {
            mostrarMensaje(FacesMessage.SEVERITY_WARN, "El nombre de la caja es obligatorio.");
            return false;
        }
        if (nombre.length() > MAX_NOMBRE) {
            mostrarMensaje(FacesMessage.SEVERITY_WARN,
                    "El nombre de la caja no puede superar " + MAX_NOMBRE + " caracteres.");
            return false;
        }
        for (Caja otra : cajaDAO.findAll()) {
            if (!otra.getIdCaja().equals(caja.getIdCaja())
                    && otra.getNombre() != null && otra.getNombre().trim().equalsIgnoreCase(nombre)) {
                mostrarMensaje(FacesMessage.SEVERITY_ERROR, "Ya existe una caja llamada \"" + nombre + "\".");
                return false;
            }
        }
        caja.setNombre(nombre);
        return true;
    }

    @Override
    public void eliminar(Caja entidad) {
        if (entidad != null && entidad.getIdCaja() != null) {
            try {
                long facturas = cajaDAO.contarFacturas(entidad.getIdCaja());
                if (facturas > 0) {
                    mostrarMensaje(FacesMessage.SEVERITY_ERROR,
                            "No se puede eliminar la caja \"" + entidad.getNombre() + "\" porque tiene "
                            + facturas + (facturas == 1 ? " factura" : " facturas")
                            + ". Desactívela para que no admita más cobros.");
                    return;
                }
            } catch (RuntimeException e) {
                LOG.log(Level.SEVERE, "No se pudieron contar las facturas de la caja", e);
                mostrarMensaje(FacesMessage.SEVERITY_ERROR, "No se pudo verificar si la caja tiene facturas.");
                return;
            }
        }
        super.eliminar(entidad);
        seleccionado = null;
    }

    @Override
    public void cancelar() {
        super.cancelar();
        seleccionado = null;
    }

    public Caja getSeleccionado() {
        return seleccionado;
    }

    public void setSeleccionado(Caja seleccionado) {
        this.seleccionado = seleccionado;
    }
}
