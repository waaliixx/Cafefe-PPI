package sv.edu.ues.occ.ingenieria.ppi115_2026.cafefe.control;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceException;
import jakarta.persistence.TypedQuery;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import sv.edu.ues.occ.ingenieria.ppi115_2026.cafefe.entity.Factura;
import sv.edu.ues.occ.ingenieria.ppi115_2026.cafefe.entity.Pago;

/** Pruebas unitarias: EntityManager y consultas simulados con Mockito. */
@ExtendWith(MockitoExtension.class)
class PagoDAOTest {

    @Mock
    EntityManager em;

    @Mock
    TypedQuery<Pago> consulta;

    @InjectMocks
    PagoDAO dao;

    @Test
    void entityManagerEsElInyectado() {
        assertSame(em, dao.getEntityManager());
    }

    @Test
    void buscarPorIdUsaLaClaseDeLaEntidad() {
        UUID id = UUID.randomUUID();
        Pago entidad = new Pago(id);
        when(em.find(Pago.class, id)).thenReturn(entidad);
        assertSame(entidad, dao.buscarPorId(id));
        verify(em).find(Pago.class, id);
    }

    @Test
    void findByEstadoUsaConsultaYParametroCorrectos() {
        String valor = "Café";
        when(em.createNamedQuery("Pago.findByEstado", Pago.class)).thenReturn(consulta);
        List<Pago> esperado = List.of(new Pago(UUID.randomUUID()));
        when(consulta.getResultList()).thenReturn(esperado);
        assertSame(esperado, dao.findByEstado(valor));
        verify(consulta).setParameter("estado", valor);
        verify(consulta).getResultList();
    }

    @Test
    void findByEstadoConNullNoConsulta() {
        assertTrue(dao.findByEstado(null).isEmpty());
        verifyNoInteractions(em);
    }

    @Test
    void findByEstadoConTextoVacioNoConsulta() {
        assertTrue(dao.findByEstado("").isEmpty());
        assertTrue(dao.findByEstado("   ").isEmpty());
        verifyNoInteractions(em);
    }

    @Test
    void findByEstadoConErrorDevuelveListaVacia() {
        String valor = "Café";
        PersistenceException error = new PersistenceException("Fallo simulado");
        when(em.createNamedQuery("Pago.findByEstado", Pago.class)).thenThrow(error);
        assertTrue(dao.findByEstado(valor).isEmpty());
    }

    @Test
    void findByEstadoConErrorAlEjecutar() {
        String valor = "Café";
        when(em.createNamedQuery("Pago.findByEstado", Pago.class)).thenReturn(consulta);
        PersistenceException error = new PersistenceException("Fallo al ejecutar");
        when(consulta.getResultList()).thenThrow(error);
        assertTrue(dao.findByEstado(valor).isEmpty());
    }

    @Test
    void findByEstadoSinEntityManagerDevuelveListaVacia() {
        String valor = "Café";
        assertTrue(new PagoDAO().findByEstado(valor).isEmpty());
        verifyNoInteractions(em);
    }

    @Test
    void findByEstadoSinCoincidenciasDevuelveListaVacia() {
        String valor = "Café";
        when(em.createNamedQuery("Pago.findByEstado", Pago.class)).thenReturn(consulta);
        when(consulta.getResultList()).thenReturn(List.of());
        assertTrue(dao.findByEstado(valor).isEmpty());
    }

    @Test
    void findByFacturaUsaConsultaYParametroCorrectos() {
        Factura valor = new Factura(UUID.randomUUID());
        when(em.createQuery("SELECT p FROM Pago p WHERE p.idFactura = :factura", Pago.class)).thenReturn(consulta);
        List<Pago> esperado = List.of(new Pago(UUID.randomUUID()));
        when(consulta.getResultList()).thenReturn(esperado);
        assertSame(esperado, dao.findByFactura(valor));
        verify(consulta).setParameter("factura", valor);
        verify(consulta).getResultList();
    }

    @Test
    void findByFacturaConNullNoConsulta() {
        assertTrue(dao.findByFactura(null).isEmpty());
        verifyNoInteractions(em);
    }

    @Test
    void findByFacturaConErrorDevuelveListaVacia() {
        Factura valor = new Factura(UUID.randomUUID());
        PersistenceException error = new PersistenceException("Fallo simulado");
        when(em.createQuery("SELECT p FROM Pago p WHERE p.idFactura = :factura", Pago.class)).thenThrow(error);
        assertTrue(dao.findByFactura(valor).isEmpty());
    }

    @Test
    void findByFacturaConErrorAlEjecutar() {
        Factura valor = new Factura(UUID.randomUUID());
        when(em.createQuery("SELECT p FROM Pago p WHERE p.idFactura = :factura", Pago.class)).thenReturn(consulta);
        PersistenceException error = new PersistenceException("Fallo al ejecutar");
        when(consulta.getResultList()).thenThrow(error);
        assertTrue(dao.findByFactura(valor).isEmpty());
    }

    @Test
    void findByFacturaSinEntityManagerDevuelveListaVacia() {
        Factura valor = new Factura(UUID.randomUUID());
        assertTrue(new PagoDAO().findByFactura(valor).isEmpty());
        verifyNoInteractions(em);
    }

    @Test
    void findByFacturaSinCoincidenciasDevuelveListaVacia() {
        Factura valor = new Factura(UUID.randomUUID());
        when(em.createQuery("SELECT p FROM Pago p WHERE p.idFactura = :factura", Pago.class)).thenReturn(consulta);
        when(consulta.getResultList()).thenReturn(List.of());
        assertTrue(dao.findByFactura(valor).isEmpty());
    }
}
