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
import sv.edu.ues.occ.ingenieria.ppi115_2026.cafefe.entity.PagoDetalle;

/**
 *
 * @author walixx
 */
@Stateless
public class PagoDetalleDAO extends AbstraccionDAO<PagoDetalle> implements Serializable {
    static final String JPQL_POR_PAGO
            = "SELECT d FROM PagoDetalle d WHERE d.idPago.idPago = :id";
    @PersistenceContext(unitName = "CafefePU")
    private EntityManager em;
    
    public PagoDetalleDAO() {
        super(PagoDetalle.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }
    
    /*Metodo para buscar por pago*/
    public List<PagoDetalle> findByPago(UUID idPago) {
        if (idPago == null || getEntityManager() == null) {
            return Collections.emptyList();
        }
        try {
            return getEntityManager()
                    .createQuery(JPQL_POR_PAGO, PagoDetalle.class)
                    .setParameter("id", idPago)
                    .getResultList();
        } catch (Exception e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }
}
