package sv.edu.ues.occ.ingenieria.ppi115_2026.cafefe.dao;

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
import sv.edu.ues.occ.ingenieria.ppi115_2026.cafefe.entity.Caracteristica;
import sv.edu.ues.occ.ingenieria.ppi115_2026.cafefe.entity.TipoCaracteristica;

/** Pruebas unitarias: EntityManager y consultas simulados con Mockito. */
@ExtendWith(MockitoExtension.class)
class CaracteristicaDAOTest {

    @Mock
    EntityManager em;

    @Mock
    TypedQuery<Caracteristica> consulta;

    @Mock
    TypedQuery<Long> conteo;

    @InjectMocks
    CaracteristicaDAO dao;

    @Test
    void entityManagerEsElInyectado() {
        assertSame(em, dao.getEntityManager());
    }

    @Test
    void buscarPorIdUsaLaClaseDeLaEntidad() {
        UUID id = UUID.randomUUID();
        Caracteristica entidad = new Caracteristica(id);
        when(em.find(Caracteristica.class, id)).thenReturn(entidad);
        assertSame(entidad, dao.buscarPorId(id));
        verify(em).find(Caracteristica.class, id);
    }

    @Test
    void findByNombreUsaConsultaYParametroCorrectos() {
        String valor = "Café";
        when(em.createNamedQuery("Caracteristica.findByNombre", Caracteristica.class)).thenReturn(consulta);
        List<Caracteristica> esperado = List.of(new Caracteristica(UUID.randomUUID()));
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
    void findByNombreConErrorDevuelveListaVacia() {
        String valor = "Café";
        PersistenceException error = new PersistenceException("Fallo simulado");
        when(em.createNamedQuery("Caracteristica.findByNombre", Caracteristica.class)).thenThrow(error);
        assertTrue(dao.findByNombre(valor).isEmpty());
    }

    @Test
    void findByNombreConErrorAlEjecutar() {
        String valor = "Café";
        when(em.createNamedQuery("Caracteristica.findByNombre", Caracteristica.class)).thenReturn(consulta);
        PersistenceException error = new PersistenceException("Fallo al ejecutar");
        when(consulta.getResultList()).thenThrow(error);
        assertTrue(dao.findByNombre(valor).isEmpty());
    }

    @Test
    void findByNombreSinEntityManagerDevuelveListaVacia() {
        String valor = "Café";
        assertTrue(new CaracteristicaDAO().findByNombre(valor).isEmpty());
        verifyNoInteractions(em);
    }

    @Test
    void findByNombreSinCoincidenciasDevuelveListaVacia() {
        String valor = "Café";
        when(em.createNamedQuery("Caracteristica.findByNombre", Caracteristica.class)).thenReturn(consulta);
        when(consulta.getResultList()).thenReturn(List.of());
        assertTrue(dao.findByNombre(valor).isEmpty());
    }

    @Test
    void findByActivoUsaConsultaYParametroCorrectos() {
        Boolean valor = Boolean.FALSE;
        when(em.createNamedQuery("Caracteristica.findByActivo", Caracteristica.class)).thenReturn(consulta);
        List<Caracteristica> esperado = List.of(new Caracteristica(UUID.randomUUID()));
        when(consulta.getResultList()).thenReturn(esperado);
        assertSame(esperado, dao.findByActivo(valor));
        verify(consulta).setParameter("activo", valor);
        verify(consulta).getResultList();
    }

    @Test
    void findByActivoConNullNoConsulta() {
        assertTrue(dao.findByActivo(null).isEmpty());
        verifyNoInteractions(em);
    }

    @Test
    void findByActivoConErrorDevuelveListaVacia() {
        Boolean valor = Boolean.FALSE;
        PersistenceException error = new PersistenceException("Fallo simulado");
        when(em.createNamedQuery("Caracteristica.findByActivo", Caracteristica.class)).thenThrow(error);
        assertTrue(dao.findByActivo(valor).isEmpty());
    }

    @Test
    void findByActivoConErrorAlEjecutar() {
        Boolean valor = Boolean.FALSE;
        when(em.createNamedQuery("Caracteristica.findByActivo", Caracteristica.class)).thenReturn(consulta);
        PersistenceException error = new PersistenceException("Fallo al ejecutar");
        when(consulta.getResultList()).thenThrow(error);
        assertTrue(dao.findByActivo(valor).isEmpty());
    }

    @Test
    void findByActivoSinEntityManagerDevuelveListaVacia() {
        Boolean valor = Boolean.FALSE;
        assertTrue(new CaracteristicaDAO().findByActivo(valor).isEmpty());
        verifyNoInteractions(em);
    }

    @Test
    void findByActivoSinCoincidenciasDevuelveListaVacia() {
        Boolean valor = Boolean.FALSE;
        when(em.createNamedQuery("Caracteristica.findByActivo", Caracteristica.class)).thenReturn(consulta);
        when(consulta.getResultList()).thenReturn(List.of());
        assertTrue(dao.findByActivo(valor).isEmpty());
    }

    @Test
    void findByTipoCaracteristicaUsaConsultaYParametroCorrectos() {
        TipoCaracteristica valor = new TipoCaracteristica(UUID.randomUUID());
        when(em.createQuery("SELECT c FROM Caracteristica c WHERE c.idTipoCaracteristica = :tipoCaracteristica", Caracteristica.class)).thenReturn(consulta);
        List<Caracteristica> esperado = List.of(new Caracteristica(UUID.randomUUID()));
        when(consulta.getResultList()).thenReturn(esperado);
        assertSame(esperado, dao.findByTipoCaracteristica(valor));
        verify(consulta).setParameter("tipoCaracteristica", valor);
        verify(consulta).getResultList();
    }

    @Test
    void findByTipoCaracteristicaConNullNoConsulta() {
        assertTrue(dao.findByTipoCaracteristica(null).isEmpty());
        verifyNoInteractions(em);
    }

    @Test
    void findByTipoCaracteristicaConErrorDevuelveListaVacia() {
        TipoCaracteristica valor = new TipoCaracteristica(UUID.randomUUID());
        PersistenceException error = new PersistenceException("Fallo simulado");
        when(em.createQuery("SELECT c FROM Caracteristica c WHERE c.idTipoCaracteristica = :tipoCaracteristica", Caracteristica.class)).thenThrow(error);
        assertTrue(dao.findByTipoCaracteristica(valor).isEmpty());
    }

    @Test
    void findByTipoCaracteristicaConErrorAlEjecutar() {
        TipoCaracteristica valor = new TipoCaracteristica(UUID.randomUUID());
        when(em.createQuery("SELECT c FROM Caracteristica c WHERE c.idTipoCaracteristica = :tipoCaracteristica", Caracteristica.class)).thenReturn(consulta);
        PersistenceException error = new PersistenceException("Fallo al ejecutar");
        when(consulta.getResultList()).thenThrow(error);
        assertTrue(dao.findByTipoCaracteristica(valor).isEmpty());
    }

    @Test
    void findByTipoCaracteristicaSinEntityManagerDevuelveListaVacia() {
        TipoCaracteristica valor = new TipoCaracteristica(UUID.randomUUID());
        assertTrue(new CaracteristicaDAO().findByTipoCaracteristica(valor).isEmpty());
        verifyNoInteractions(em);
    }

    @Test
    void findByTipoCaracteristicaSinCoincidenciasDevuelveListaVacia() {
        TipoCaracteristica valor = new TipoCaracteristica(UUID.randomUUID());
        when(em.createQuery("SELECT c FROM Caracteristica c WHERE c.idTipoCaracteristica = :tipoCaracteristica", Caracteristica.class)).thenReturn(consulta);
        when(consulta.getResultList()).thenReturn(List.of());
        assertTrue(dao.findByTipoCaracteristica(valor).isEmpty());
    }

    @Test
    void contarPorTipoUsaConsultaYParametroCorrectos() {
        UUID valor = UUID.randomUUID();
        when(em.createQuery("SELECT COUNT(c) FROM Caracteristica c WHERE c.idTipoCaracteristica.idTipoCaracteristica = :id", Long.class)).thenReturn(conteo);
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
        when(em.createQuery("SELECT COUNT(c) FROM Caracteristica c WHERE c.idTipoCaracteristica.idTipoCaracteristica = :id", Long.class)).thenThrow(error);
        assertSame(error, assertThrows(PersistenceException.class, () -> dao.contarPorTipo(valor)));
    }

    @Test
    void contarPorTipoConErrorAlEjecutar() {
        UUID valor = UUID.randomUUID();
        when(em.createQuery("SELECT COUNT(c) FROM Caracteristica c WHERE c.idTipoCaracteristica.idTipoCaracteristica = :id", Long.class)).thenReturn(conteo);
        when(conteo.setParameter("id", valor)).thenReturn(conteo);
        PersistenceException error = new PersistenceException("Fallo al ejecutar");
        when(conteo.getSingleResult()).thenThrow(error);
        assertSame(error, assertThrows(PersistenceException.class, () -> dao.contarPorTipo(valor)));
    }
}
