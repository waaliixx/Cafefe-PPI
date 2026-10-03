/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package sv.edu.ues.occ.ingenieria.ppi115_2026.cafefe.model;

import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import sv.edu.ues.occ.ingenieria.ppi115_2026.cafefe.dao.AbstraccionDAO;
import sv.edu.ues.occ.ingenieria.ppi115_2026.cafefe.dao.TipoDescuentoDAO;
import sv.edu.ues.occ.ingenieria.ppi115_2026.cafefe.entity.TipoDescuento;
import java.util.UUID;
/**
 *
 * @author walixx
 */
@Named
@ViewScoped
public class TipoDescuentoModel extends AbstraccionModel<TipoDescuento> {
    
    @Inject
    private TipoDescuentoDAO tipoDescuentoDAO;

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
