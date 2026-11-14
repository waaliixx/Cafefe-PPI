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
import sv.edu.ues.occ.ingenieria.ppi115_2026.cafefe.entity.PagoDetalle;

/**
 *
 * @author walixx
 */
/** Pruebas unitarias: EntityManager y consultas simulados con Mockito. */
@ExtendWith(MockitoExtension.class)
public class PagoDetalleDAOTest {
    @Mock
    EntityManager em;

    @Mock
    TypedQuery<PagoDetalle> consulta;

    @InjectMocks
    PagoDetalleDAO dao;

    @Test
    void entityManagerEsElInyectado() {
        assertSame(em, dao.getEntityManager());
    }

    @Test
    void buscarPorIdUsaLaClaseDeLaEntidad() {
        UUID id = UUID.randomUUID();
        PagoDetalle entidad = new PagoDetalle(id);
        when(em.find(PagoDetalle.class, id)).thenReturn(entidad);
        assertSame(entidad, dao.buscarPorId(id));
    }

    @Test
    void findByPagoUsaConsultaYParametroCorrectos() {
        UUID id = UUID.randomUUID();
        when(em.createQuery(PagoDetalleDAO.JPQL_POR_PAGO, PagoDetalle.class)).thenReturn(consulta);
        when(consulta.setParameter("id", id)).thenReturn(consulta);
        List<PagoDetalle> esperado = List.of(new PagoDetalle(UUID.randomUUID()));
        when(consulta.getResultList()).thenReturn(esperado);
        assertSame(esperado, dao.findByPago(id));
        verify(consulta).setParameter("id", id);
    }

    @Test
    void findByPagoConNullNoConsulta() {
        assertTrue(dao.findByPago(null).isEmpty());
        verifyNoInteractions(em);
    }

    @Test
    void findByPagoSinEntityManagerDevuelveListaVacia() {
        assertTrue(new PagoDetalleDAO().findByPago(UUID.randomUUID()).isEmpty());
        verifyNoInteractions(em);
    }

    @Test
    void findByPagoSinResultados() {
        UUID id = UUID.randomUUID();
        when(em.createQuery(PagoDetalleDAO.JPQL_POR_PAGO, PagoDetalle.class)).thenReturn(consulta);
        when(consulta.setParameter("id", id)).thenReturn(consulta);
        when(consulta.getResultList()).thenReturn(List.of());
        assertTrue(dao.findByPago(id).isEmpty());
    }

    @Test
    void findByPagoErrorAlCrearConsulta() {
        when(em.createQuery(PagoDetalleDAO.JPQL_POR_PAGO, PagoDetalle.class)).thenThrow(new PersistenceException("Fallo"));
        assertTrue(dao.findByPago(UUID.randomUUID()).isEmpty());
    }

    @Test
    void findByPagoErrorAlEjecutarConsulta() {
        UUID id = UUID.randomUUID();
        when(em.createQuery(PagoDetalleDAO.JPQL_POR_PAGO, PagoDetalle.class)).thenReturn(consulta);
        when(consulta.setParameter("id", id)).thenReturn(consulta);
        when(consulta.getResultList()).thenThrow(new PersistenceException("Fallo"));
        assertTrue(dao.findByPago(id).isEmpty());
    }
}
