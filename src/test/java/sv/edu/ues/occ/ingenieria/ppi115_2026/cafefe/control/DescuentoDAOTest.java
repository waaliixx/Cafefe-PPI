package sv.edu.ues.occ.ingenieria.ppi115_2026.cafefe.control;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceException;
import jakarta.persistence.TypedQuery;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import sv.edu.ues.occ.ingenieria.ppi115_2026.cafefe.entity.Descuento;

/** Pruebas unitarias: EntityManager y consultas simulados con Mockito. */
@ExtendWith(MockitoExtension.class)
class DescuentoDAOTest {

    @Mock
    EntityManager em;

    @Mock
    TypedQuery<Descuento> consulta;

    @Mock
    TypedQuery<Long> conteo;

    @InjectMocks
    DescuentoDAO dao;

    @Test
    void entityManagerEsElInyectado() {
        assertSame(em, dao.getEntityManager());
    }

    @Test
    void buscarPorIdUsaLaClaseDeLaEntidad() {
        UUID id = UUID.randomUUID();
        Descuento entidad = new Descuento(id);
        when(em.find(Descuento.class, id)).thenReturn(entidad);
        assertSame(entidad, dao.buscarPorId(id));
        verify(em).find(Descuento.class, id);
    }

    @Test
    void findByNombreUsaConsultaYParametroCorrectos() {
        String valor = "Café";
        when(em.createNamedQuery("Descuento.findByNombre", Descuento.class)).thenReturn(consulta);
        List<Descuento> esperado = List.of(new Descuento(UUID.randomUUID()));
        when(consulta.getResultList()).thenReturn(esperado);
        assertSame(esperado, dao.findByNombre(valor));
        verify(consulta).setParameter("nombre", valor);
        verify(consulta).getResultList();
    }

    @Test
    void findByNombreConNullNoConsulta() {
        assertTrue(dao.findByNombre(null).isEmpty());
        verifyNoInteractions(em);
    }

    @Test
    void findByNombreConTextoVacioNoConsulta() {
        assertTrue(dao.findByNombre("").isEmpty());
        assertTrue(dao.findByNombre("   ").isEmpty());
        verifyNoInteractions(em);
    }

    @Test
    void findByNombreConErrorDevuelveListaVacia() {
        String valor = "Café";
        PersistenceException error = new PersistenceException("Fallo simulado");
        when(em.createNamedQuery("Descuento.findByNombre", Descuento.class)).thenThrow(error);
        assertTrue(dao.findByNombre(valor).isEmpty());
    }

    @Test
    void findByNombreConErrorAlEjecutar() {
        String valor = "Café";
        when(em.createNamedQuery("Descuento.findByNombre", Descuento.class)).thenReturn(consulta);
        PersistenceException error = new PersistenceException("Fallo al ejecutar");
        when(consulta.getResultList()).thenThrow(error);
        assertTrue(dao.findByNombre(valor).isEmpty());
    }

    @Test
    void findByNombreSinEntityManagerDevuelveListaVacia() {
        String valor = "Café";
        assertTrue(new DescuentoDAO().findByNombre(valor).isEmpty());
        verifyNoInteractions(em);
    }

    @Test
    void findByNombreSinCoincidenciasDevuelveListaVacia() {
        String valor = "Café";
        when(em.createNamedQuery("Descuento.findByNombre", Descuento.class)).thenReturn(consulta);
        when(consulta.getResultList()).thenReturn(List.of());
        assertTrue(dao.findByNombre(valor).isEmpty());
    }

    @Test
    void findByFechaDesdeUsaConsultaYParametroCorrectos() {
        Date valor = new Date(1700000000000L);
        when(em.createNamedQuery("Descuento.findByFechaDesde", Descuento.class)).thenReturn(consulta);
        List<Descuento> esperado = List.of(new Descuento(UUID.randomUUID()));
        when(consulta.getResultList()).thenReturn(esperado);
        assertSame(esperado, dao.findByFechaDesde(valor));
        verify(consulta).setParameter("fechaDesde", valor);
        verify(consulta).getResultList();
    }

    @Test
    void findByFechaDesdeConNullNoConsulta() {
        assertTrue(dao.findByFechaDesde(null).isEmpty());
        verifyNoInteractions(em);
    }

    @Test
    void findByFechaDesdeConErrorDevuelveListaVacia() {
        Date valor = new Date(1700000000000L);
        PersistenceException error = new PersistenceException("Fallo simulado");
        when(em.createNamedQuery("Descuento.findByFechaDesde", Descuento.class)).thenThrow(error);
        assertTrue(dao.findByFechaDesde(valor).isEmpty());
    }

    @Test
    void findByFechaDesdeConErrorAlEjecutar() {
        Date valor = new Date(1700000000000L);
        when(em.createNamedQuery("Descuento.findByFechaDesde", Descuento.class)).thenReturn(consulta);
        PersistenceException error = new PersistenceException("Fallo al ejecutar");
        when(consulta.getResultList()).thenThrow(error);
        assertTrue(dao.findByFechaDesde(valor).isEmpty());
    }

    @Test
    void findByFechaDesdeSinEntityManagerDevuelveListaVacia() {
        Date valor = new Date(1700000000000L);
        assertTrue(new DescuentoDAO().findByFechaDesde(valor).isEmpty());
        verifyNoInteractions(em);
    }

    @Test
    void findByFechaDesdeSinCoincidenciasDevuelveListaVacia() {
        Date valor = new Date(1700000000000L);
        when(em.createNamedQuery("Descuento.findByFechaDesde", Descuento.class)).thenReturn(consulta);
        when(consulta.getResultList()).thenReturn(List.of());
        assertTrue(dao.findByFechaDesde(valor).isEmpty());
    }

    @Test
    void findByFechaHastaUsaConsultaYParametroCorrectos() {
        Date valor = new Date(1700000000000L);
        when(em.createNamedQuery("Descuento.findByFechaHasta", Descuento.class)).thenReturn(consulta);
        List<Descuento> esperado = List.of(new Descuento(UUID.randomUUID()));
        when(consulta.getResultList()).thenReturn(esperado);
        assertSame(esperado, dao.findByFechaHasta(valor));
        verify(consulta).setParameter("fechaHasta", valor);
        verify(consulta).getResultList();
    }

    @Test
    void findByFechaHastaConNullNoConsulta() {
        assertTrue(dao.findByFechaHasta(null).isEmpty());
        verifyNoInteractions(em);
    }

    @Test
    void findByFechaHastaConErrorDevuelveListaVacia() {
        Date valor = new Date(1700000000000L);
        PersistenceException error = new PersistenceException("Fallo simulado");
        when(em.createNamedQuery("Descuento.findByFechaHasta", Descuento.class)).thenThrow(error);
        assertTrue(dao.findByFechaHasta(valor).isEmpty());
    }

    @Test
    void findByFechaHastaConErrorAlEjecutar() {
        Date valor = new Date(1700000000000L);
        when(em.createNamedQuery("Descuento.findByFechaHasta", Descuento.class)).thenReturn(consulta);
        PersistenceException error = new PersistenceException("Fallo al ejecutar");
        when(consulta.getResultList()).thenThrow(error);
        assertTrue(dao.findByFechaHasta(valor).isEmpty());
    }

    @Test
    void findByFechaHastaSinEntityManagerDevuelveListaVacia() {
        Date valor = new Date(1700000000000L);
        assertTrue(new DescuentoDAO().findByFechaHasta(valor).isEmpty());
        verifyNoInteractions(em);
    }

    @Test
    void findByFechaHastaSinCoincidenciasDevuelveListaVacia() {
        Date valor = new Date(1700000000000L);
        when(em.createNamedQuery("Descuento.findByFechaHasta", Descuento.class)).thenReturn(consulta);
        when(consulta.getResultList()).thenReturn(List.of());
        assertTrue(dao.findByFechaHasta(valor).isEmpty());
    }

    @Test
    void findVigentesUsaConsultaYParametroCorrectos() {
        long antes = System.currentTimeMillis();
        when(em.createQuery("SELECT d FROM Descuento d WHERE d.fechaDesde <= :hoy AND (d.fechaHasta IS NULL OR d.fechaHasta >= :hoy)", Descuento.class)).thenReturn(consulta);
        List<Descuento> esperado = List.of(new Descuento(UUID.randomUUID()));
        when(consulta.getResultList()).thenReturn(esperado);
        assertSame(esperado, dao.findVigentes());
        long despues = System.currentTimeMillis();
        ArgumentCaptor<Date> fecha = ArgumentCaptor.forClass(Date.class);
        verify(consulta).setParameter(eq("hoy"), fecha.capture());
        assertTrue(fecha.getValue().getTime() >= antes);
        assertTrue(fecha.getValue().getTime() <= despues);
        verify(consulta).getResultList();
    }

    @Test
    void findVigentesConErrorDevuelveListaVacia() {
        PersistenceException error = new PersistenceException("Fallo simulado");
        when(em.createQuery("SELECT d FROM Descuento d WHERE d.fechaDesde <= :hoy AND (d.fechaHasta IS NULL OR d.fechaHasta >= :hoy)", Descuento.class)).thenThrow(error);
        assertTrue(dao.findVigentes().isEmpty());
    }

    @Test
    void findVigentesConErrorAlEjecutar() {
        when(em.createQuery("SELECT d FROM Descuento d WHERE d.fechaDesde <= :hoy AND (d.fechaHasta IS NULL OR d.fechaHasta >= :hoy)", Descuento.class)).thenReturn(consulta);
        PersistenceException error = new PersistenceException("Fallo al ejecutar");
        when(consulta.getResultList()).thenThrow(error);
        assertTrue(dao.findVigentes().isEmpty());
    }

    @Test
    void findVigentesSinEntityManagerDevuelveListaVacia() {
        assertTrue(new DescuentoDAO().findVigentes().isEmpty());
        verifyNoInteractions(em);
    }

    @Test
    void findVigentesSinCoincidenciasDevuelveListaVacia() {
        when(em.createQuery("SELECT d FROM Descuento d WHERE d.fechaDesde <= :hoy AND (d.fechaHasta IS NULL OR d.fechaHasta >= :hoy)", Descuento.class)).thenReturn(consulta);
        when(consulta.getResultList()).thenReturn(List.of());
        assertTrue(dao.findVigentes().isEmpty());
    }

    @Test
    void contarPorTipoUsaConsultaYParametroCorrectos() {
        UUID valor = UUID.randomUUID();
        when(em.createQuery("SELECT COUNT(d) FROM Descuento d WHERE d.idTipoDescuento = :id", Long.class)).thenReturn(conteo);
        when(conteo.setParameter("id", valor)).thenReturn(conteo);
        when(conteo.getSingleResult()).thenReturn(7L);
        assertEquals(7L, dao.contarPorTipo(valor));
        verify(conteo).setParameter("id", valor);
        verify(conteo).getSingleResult();
    }

    @Test
    void contarPorTipoConNullNoConsulta() {
        assertEquals(0L, dao.contarPorTipo(null));
        verifyNoInteractions(em);
    }

    @Test
    void contarPorTipoConErrorPropagaExcepcion() {
        UUID valor = UUID.randomUUID();
        PersistenceException error = new PersistenceException("Fallo simulado");
        when(em.createQuery("SELECT COUNT(d) FROM Descuento d WHERE d.idTipoDescuento = :id", Long.class)).thenThrow(error);
        assertSame(error, assertThrows(PersistenceException.class, () -> dao.contarPorTipo(valor)));
    }

    @Test
    void contarPorTipoConErrorAlEjecutar() {
        UUID valor = UUID.randomUUID();
        when(em.createQuery("SELECT COUNT(d) FROM Descuento d WHERE d.idTipoDescuento = :id", Long.class)).thenReturn(conteo);
        when(conteo.setParameter("id", valor)).thenReturn(conteo);
        PersistenceException error = new PersistenceException("Fallo al ejecutar");
        when(conteo.getSingleResult()).thenThrow(error);
        assertSame(error, assertThrows(PersistenceException.class, () -> dao.contarPorTipo(valor)));
    }

    @Test
    void findByTipoUsaConsultaYParametroCorrectos() {
        UUID valor = UUID.randomUUID();
        when(em.createQuery("SELECT d FROM Descuento d WHERE d.idTipoDescuento = :id ORDER BY d.nombre", Descuento.class)).thenReturn(consulta);
        when(consulta.setParameter("id", valor)).thenReturn(consulta);
        List<Descuento> esperado = List.of(new Descuento(UUID.randomUUID()));
        when(consulta.getResultList()).thenReturn(esperado);
        assertSame(esperado, dao.findByTipo(valor));
        verify(consulta).setParameter("id", valor);
        verify(consulta).getResultList();
    }

    @Test
    void findByTipoConNullNoConsulta() {
        assertTrue(dao.findByTipo(null).isEmpty());
        verifyNoInteractions(em);
    }

    @Test
    void findByTipoConErrorPropagaExcepcion() {
        UUID valor = UUID.randomUUID();
        PersistenceException error = new PersistenceException("Fallo simulado");
        when(em.createQuery("SELECT d FROM Descuento d WHERE d.idTipoDescuento = :id ORDER BY d.nombre", Descuento.class)).thenThrow(error);
        assertSame(error, assertThrows(PersistenceException.class, () -> dao.findByTipo(valor)));
    }

    @Test
    void findByTipoConErrorAlEjecutar() {
        UUID valor = UUID.randomUUID();
        when(em.createQuery("SELECT d FROM Descuento d WHERE d.idTipoDescuento = :id ORDER BY d.nombre", Descuento.class)).thenReturn(consulta);
        when(consulta.setParameter("id", valor)).thenReturn(consulta);
        PersistenceException error = new PersistenceException("Fallo al ejecutar");
        when(consulta.getResultList()).thenThrow(error);
        assertSame(error, assertThrows(PersistenceException.class, () -> dao.findByTipo(valor)));
    }

    @Test
    void findByTipoSinCoincidenciasDevuelveListaVacia() {
        UUID valor = UUID.randomUUID();
        when(em.createQuery("SELECT d FROM Descuento d WHERE d.idTipoDescuento = :id ORDER BY d.nombre", Descuento.class)).thenReturn(consulta);
        when(consulta.setParameter("id", valor)).thenReturn(consulta);
        when(consulta.getResultList()).thenReturn(List.of());
        assertTrue(dao.findByTipo(valor).isEmpty());
    }
}
