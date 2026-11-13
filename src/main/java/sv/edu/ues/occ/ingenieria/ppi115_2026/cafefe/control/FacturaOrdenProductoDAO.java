/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package sv.edu.ues.occ.ingenieria.ppi115_2026.cafefe.control;

import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.io.Serializable;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import sv.edu.ues.occ.ingenieria.ppi115_2026.cafefe.entity.FacturaOrdenProducto;

/**
 *
 * @author walixx
 */
@Stateless
public class FacturaOrdenProductoDAO extends AbstraccionDAO<FacturaOrdenProducto> implements Serializable{
    static final String JPQL_POR_FACTURA
            = "SELECT f FROM FacturaOrdenProducto f WHERE f.idFactura.idFactura = :id";
    @PersistenceContext(unitName = "CafefePU")
    private EntityManager em;

    public FacturaOrdenProductoDAO() {
        super(FacturaOrdenProducto.class);
    }
    
    @Override
    protected EntityManager getEntityManager() {
        return em;
    }
    
    /*Metodo para buscar por factura*/
    public List<FacturaOrdenProducto> findByFactura(UUID idFactura) {
        if (idFactura == null || getEntityManager() == null) {
            return Collections.emptyList();
        }
        try {
            return getEntityManager()
                    .createQuery(JPQL_POR_FACTURA, FacturaOrdenProducto.class)
                    .setParameter("id", idFactura)
                    .getResultList();
        } catch (Exception e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }
    
}
