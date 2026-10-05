package sv.edu.ues.occ.ingenieria.ppi115_2026.cafefe.dao;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceException;
import jakarta.persistence.TypedQuery;
import java.util.Date;
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

/** Pruebas unitarias: EntityManager y consultas simulados con Mockito. */
@ExtendWith(MockitoExtension.class)
class FacturaDAOTest {

    @Mock
    EntityManager em;

    @Mock
    TypedQuery<Factura> consulta;

    @InjectMocks
    FacturaDAO dao;

    @Test
    void entityManagerEsElInyectado() {
        assertSame(em, dao.getEntityManager());
    }

    @Test
    void buscarPorIdUsaLaClaseDeLaEntidad() {
        UUID id = UUID.randomUUID();
        Factura entidad = new Factura(id);
        when(em.find(Factura.class, id)).thenReturn(entidad);
        assertSame(entidad, dao.buscarPorId(id));
        verify(em).find(Factura.class, id);
    }

    @Test
    void findByClienteUsaConsultaYParametroCorrectos() {
        String valor = "Café";
        when(em.createNamedQuery("Factura.findByCliente", Factura.class)).thenReturn(consulta);
        List<Factura> esperado = List.of(new Factura(UUID.randomUUID()));
        when(consulta.getResultList()).thenReturn(esperado);
        assertSame(esperado, dao.findByCliente(valor));
        verify(consulta).setParameter("cliente", valor);
        verify(consulta).getResultList();
    }

    @Test
    void findByClienteConNullNoConsulta() {
        assertTrue(dao.findByCliente(null).isEmpty());
        verifyNoInteractions(em);
    }

    @Test
    void findByClienteConTextoVacioNoConsulta() {
        assertTrue(dao.findByCliente("").isEmpty());
        assertTrue(dao.findByCliente("   ").isEmpty());
        verifyNoInteractions(em);
    }

    @Test
    void findByClienteConErrorDevuelveListaVacia() {
        String valor = "Café";
        PersistenceException error = new PersistenceException("Fallo simulado");
        when(em.createNamedQuery("Factura.findByCliente", Factura.class)).thenThrow(error);
        assertTrue(dao.findByCliente(valor).isEmpty());
    }

    @Test
    void findByClienteConErrorAlEjecutar() {
        String valor = "Café";
        when(em.createNamedQuery("Factura.findByCliente", Factura.class)).thenReturn(consulta);
        PersistenceException error = new PersistenceException("Fallo al ejecutar");
        when(consulta.getResultList()).thenThrow(error);
        assertTrue(dao.findByCliente(valor).isEmpty());
    }

    @Test
    void findByClienteSinEntityManagerDevuelveListaVacia() {
        String valor = "Café";
        assertTrue(new FacturaDAO().findByCliente(valor).isEmpty());
        verifyNoInteractions(em);
    }

    @Test
    void findByClienteSinCoincidenciasDevuelveListaVacia() {
        String valor = "Café";
        when(em.createNamedQuery("Factura.findByCliente", Factura.class)).thenReturn(consulta);
        when(consulta.getResultList()).thenReturn(List.of());
        assertTrue(dao.findByCliente(valor).isEmpty());
    }

    @Test
    void findByFechaFacturacionUsaConsultaYParametroCorrectos() {
        Date valor = new Date(1700000000000L);
        when(em.createNamedQuery("Factura.findByFechaFacturacion", Factura.class)).thenReturn(consulta);
        List<Factura> esperado = List.of(new Factura(UUID.randomUUID()));
        when(consulta.getResultList()).thenReturn(esperado);
        assertSame(esperado, dao.findByFechaFacturacion(valor));
        verify(consulta).setParameter("fechaFacturacion", valor);
        verify(consulta).getResultList();
    }

    @Test
    void findByFechaFacturacionConNullNoConsulta() {
        assertTrue(dao.findByFechaFacturacion(null).isEmpty());
        verifyNoInteractions(em);
    }

    @Test
    void findByFechaFacturacionConErrorDevuelveListaVacia() {
        Date valor = new Date(1700000000000L);
        PersistenceException error = new PersistenceException("Fallo simulado");
        when(em.createNamedQuery("Factura.findByFechaFacturacion", Factura.class)).thenThrow(error);
        assertTrue(dao.findByFechaFacturacion(valor).isEmpty());
    }

    @Test
    void findByFechaFacturacionConErrorAlEjecutar() {
        Date valor = new Date(1700000000000L);
        when(em.createNamedQuery("Factura.findByFechaFacturacion", Factura.class)).thenReturn(consulta);
        PersistenceException error = new PersistenceException("Fallo al ejecutar");
        when(consulta.getResultList()).thenThrow(error);
        assertTrue(dao.findByFechaFacturacion(valor).isEmpty());
    }

    @Test
    void findByFechaFacturacionSinEntityManagerDevuelveListaVacia() {
        Date valor = new Date(1700000000000L);
        assertTrue(new FacturaDAO().findByFechaFacturacion(valor).isEmpty());
        verifyNoInteractions(em);
    }

    @Test
    void findByFechaFacturacionSinCoincidenciasDevuelveListaVacia() {
        Date valor = new Date(1700000000000L);
        when(em.createNamedQuery("Factura.findByFechaFacturacion", Factura.class)).thenReturn(consulta);
        when(consulta.getResultList()).thenReturn(List.of());
        assertTrue(dao.findByFechaFacturacion(valor).isEmpty());
    }

    @Test
    void findByEstadoUsaConsultaYParametroCorrectos() {
        String valor = "Café";
        when(em.createNamedQuery("Factura.findByEstado", Factura.class)).thenReturn(consulta);
        List<Factura> esperado = List.of(new Factura(UUID.randomUUID()));
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
        when(em.createNamedQuery("Factura.findByEstado", Factura.class)).thenThrow(error);
        assertTrue(dao.findByEstado(valor).isEmpty());
    }

    @Test
    void findByEstadoConErrorAlEjecutar() {
        String valor = "Café";
        when(em.createNamedQuery("Factura.findByEstado", Factura.class)).thenReturn(consulta);
        PersistenceException error = new PersistenceException("Fallo al ejecutar");
        when(consulta.getResultList()).thenThrow(error);
        assertTrue(dao.findByEstado(valor).isEmpty());
    }

    @Test
    void findByEstadoSinEntityManagerDevuelveListaVacia() {
        String valor = "Café";
        assertTrue(new FacturaDAO().findByEstado(valor).isEmpty());
        verifyNoInteractions(em);
    }

    @Test
    void findByEstadoSinCoincidenciasDevuelveListaVacia() {
        String valor = "Café";
        when(em.createNamedQuery("Factura.findByEstado", Factura.class)).thenReturn(consulta);
        when(consulta.getResultList()).thenReturn(List.of());
        assertTrue(dao.findByEstado(valor).isEmpty());
    }
}
