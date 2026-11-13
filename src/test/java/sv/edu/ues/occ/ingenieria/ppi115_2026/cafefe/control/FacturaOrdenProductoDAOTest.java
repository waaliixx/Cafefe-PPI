/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package sv.edu.ues.occ.ingenieria.ppi115_2026.cafefe.control;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceException;
import jakarta.persistence.TypedQuery;
import java.util.List;
import java.util.UUID;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import sv.edu.ues.occ.ingenieria.ppi115_2026.cafefe.entity.FacturaOrdenProducto;

/**
 *
 * @author walixx
 */
/** Pruebas unitarias: EntityManager y consultas simulados con Mockito. */
@ExtendWith(MockitoExtension.class)
class FacturaOrdenProductoDAOTest {

    @Mock
    EntityManager em;

    @Mock
    TypedQuery<FacturaOrdenProducto> consulta;

    @InjectMocks
    FacturaOrdenProductoDAO dao;

    @Test
    void entityManagerEsElInyectado() {
        assertSame(em, dao.getEntityManager());
    }

    @Test
    void buscarPorIdUsaLaClaseDeLaEntidad() {
        UUID id = UUID.randomUUID();
        FacturaOrdenProducto entidad = new FacturaOrdenProducto(id);
        when(em.find(FacturaOrdenProducto.class, id)).thenReturn(entidad);
        assertSame(entidad, dao.buscarPorId(id));
    }

    @Test
    void findByFacturaUsaConsultaYParametroCorrectos() {
        UUID id = UUID.randomUUID();
        when(em.createQuery(FacturaOrdenProductoDAO.JPQL_POR_FACTURA, FacturaOrdenProducto.class)).thenReturn(consulta);
        when(consulta.setParameter("id", id)).thenReturn(consulta);
        List<FacturaOrdenProducto> esperado = List.of(new FacturaOrdenProducto(UUID.randomUUID()));
        when(consulta.getResultList()).thenReturn(esperado);
        assertSame(esperado, dao.findByFactura(id));
        verify(consulta).setParameter("id", id);
    }

    @Test
    void findByFacturaConNullNoConsulta() {
        assertTrue(dao.findByFactura(null).isEmpty());
        verifyNoInteractions(em);
    }

    @Test
    void findByFacturaSinEntityManagerDevuelveListaVacia() {
        assertTrue(new FacturaOrdenProductoDAO().findByFactura(UUID.randomUUID()).isEmpty());
        verifyNoInteractions(em);
    }

    @Test
    void findByFacturaSinResultados() {
        UUID id = UUID.randomUUID();
        when(em.createQuery(FacturaOrdenProductoDAO.JPQL_POR_FACTURA, FacturaOrdenProducto.class)).thenReturn(consulta);
        when(consulta.setParameter("id", id)).thenReturn(consulta);
        when(consulta.getResultList()).thenReturn(List.of());
        assertTrue(dao.findByFactura(id).isEmpty());
    }

    @Test
    void findByFacturaErrorAlCrearConsulta() {
        when(em.createQuery(FacturaOrdenProductoDAO.JPQL_POR_FACTURA, FacturaOrdenProducto.class)).thenThrow(new PersistenceException("Fallo"));
        assertTrue(dao.findByFactura(UUID.randomUUID()).isEmpty());
    }

    @Test
    void findByFacturaErrorAlEjecutarConsulta() {
        UUID id = UUID.randomUUID();
        when(em.createQuery(FacturaOrdenProductoDAO.JPQL_POR_FACTURA, FacturaOrdenProducto.class)).thenReturn(consulta);
        when(consulta.setParameter("id", id)).thenReturn(consulta);
        when(consulta.getResultList()).thenThrow(new PersistenceException("Fallo"));
        assertTrue(dao.findByFactura(id).isEmpty());
    }
}
