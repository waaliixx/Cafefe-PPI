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
import sv.edu.ues.occ.ingenieria.ppi115_2026.cafefe.entity.TipoCaracteristica;

/** Pruebas unitarias: EntityManager y consultas simulados con Mockito. */
@ExtendWith(MockitoExtension.class)
class TipoCaracteristicaDAOTest {

    @Mock
    EntityManager em;

    @Mock
    TypedQuery<TipoCaracteristica> consulta;

    @InjectMocks
    TipoCaracteristicaDAO dao;

    @Test
    void entityManagerEsElInyectado() {
        assertSame(em, dao.getEntityManager());
    }

    @Test
    void buscarPorIdUsaLaClaseDeLaEntidad() {
        UUID id = UUID.randomUUID();
        TipoCaracteristica entidad = new TipoCaracteristica(id);
        when(em.find(TipoCaracteristica.class, id)).thenReturn(entidad);
        assertSame(entidad, dao.buscarPorId(id));
        verify(em).find(TipoCaracteristica.class, id);
    }

    @Test
    void findByNombreUsaConsultaYParametroCorrectos() {
        String valor = "Café";
        when(em.createNamedQuery("TipoCaracteristica.findByNombre", TipoCaracteristica.class)).thenReturn(consulta);
        List<TipoCaracteristica> esperado = List.of(new TipoCaracteristica(UUID.randomUUID()));
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
        when(em.createNamedQuery("TipoCaracteristica.findByNombre", TipoCaracteristica.class)).thenThrow(error);
        assertTrue(dao.findByNombre(valor).isEmpty());
    }

    @Test
    void findByNombreConErrorAlEjecutar() {
        String valor = "Café";
        when(em.createNamedQuery("TipoCaracteristica.findByNombre", TipoCaracteristica.class)).thenReturn(consulta);
        PersistenceException error = new PersistenceException("Fallo al ejecutar");
        when(consulta.getResultList()).thenThrow(error);
        assertTrue(dao.findByNombre(valor).isEmpty());
    }

    @Test
    void findByNombreSinEntityManagerDevuelveListaVacia() {
        String valor = "Café";
        assertTrue(new TipoCaracteristicaDAO().findByNombre(valor).isEmpty());
        verifyNoInteractions(em);
    }

    @Test
    void findByNombreSinCoincidenciasDevuelveListaVacia() {
        String valor = "Café";
        when(em.createNamedQuery("TipoCaracteristica.findByNombre", TipoCaracteristica.class)).thenReturn(consulta);
        when(consulta.getResultList()).thenReturn(List.of());
        assertTrue(dao.findByNombre(valor).isEmpty());
    }

    @Test
    void findByExpresionRegularUsaConsultaYParametroCorrectos() {
        String valor = "Café";
        when(em.createNamedQuery("TipoCaracteristica.findByExpresionRegular", TipoCaracteristica.class)).thenReturn(consulta);
        List<TipoCaracteristica> esperado = List.of(new TipoCaracteristica(UUID.randomUUID()));
        when(consulta.getResultList()).thenReturn(esperado);
        assertSame(esperado, dao.findByExpresionRegular(valor));
        verify(consulta).setParameter("expresionRegular", valor);
        verify(consulta).getResultList();
    }

    @Test
    void findByExpresionRegularConNullNoConsulta() {
        assertTrue(dao.findByExpresionRegular(null).isEmpty());
        verifyNoInteractions(em);
    }

    @Test
    void findByExpresionRegularConErrorDevuelveListaVacia() {
        String valor = "Café";
        PersistenceException error = new PersistenceException("Fallo simulado");
        when(em.createNamedQuery("TipoCaracteristica.findByExpresionRegular", TipoCaracteristica.class)).thenThrow(error);
        assertTrue(dao.findByExpresionRegular(valor).isEmpty());
    }

    @Test
    void findByExpresionRegularConErrorAlEjecutar() {
        String valor = "Café";
        when(em.createNamedQuery("TipoCaracteristica.findByExpresionRegular", TipoCaracteristica.class)).thenReturn(consulta);
        PersistenceException error = new PersistenceException("Fallo al ejecutar");
        when(consulta.getResultList()).thenThrow(error);
        assertTrue(dao.findByExpresionRegular(valor).isEmpty());
    }

    @Test
    void findByExpresionRegularSinEntityManagerDevuelveListaVacia() {
        String valor = "Café";
        assertTrue(new TipoCaracteristicaDAO().findByExpresionRegular(valor).isEmpty());
        verifyNoInteractions(em);
    }

    @Test
    void findByExpresionRegularSinCoincidenciasDevuelveListaVacia() {
        String valor = "Café";
        when(em.createNamedQuery("TipoCaracteristica.findByExpresionRegular", TipoCaracteristica.class)).thenReturn(consulta);
        when(consulta.getResultList()).thenReturn(List.of());
        assertTrue(dao.findByExpresionRegular(valor).isEmpty());
    }

    @Test
    void findByActivoUsaConsultaYParametroCorrectos() {
        Boolean valor = Boolean.FALSE;
        when(em.createNamedQuery("TipoCaracteristica.findByActivo", TipoCaracteristica.class)).thenReturn(consulta);
        List<TipoCaracteristica> esperado = List.of(new TipoCaracteristica(UUID.randomUUID()));
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
        when(em.createNamedQuery("TipoCaracteristica.findByActivo", TipoCaracteristica.class)).thenThrow(error);
        assertTrue(dao.findByActivo(valor).isEmpty());
    }

    @Test
    void findByActivoConErrorAlEjecutar() {
        Boolean valor = Boolean.FALSE;
        when(em.createNamedQuery("TipoCaracteristica.findByActivo", TipoCaracteristica.class)).thenReturn(consulta);
        PersistenceException error = new PersistenceException("Fallo al ejecutar");
        when(consulta.getResultList()).thenThrow(error);
        assertTrue(dao.findByActivo(valor).isEmpty());
    }

    @Test
    void findByActivoSinEntityManagerDevuelveListaVacia() {
        Boolean valor = Boolean.FALSE;
        assertTrue(new TipoCaracteristicaDAO().findByActivo(valor).isEmpty());
        verifyNoInteractions(em);
    }

    @Test
    void findByActivoSinCoincidenciasDevuelveListaVacia() {
        Boolean valor = Boolean.FALSE;
        when(em.createNamedQuery("TipoCaracteristica.findByActivo", TipoCaracteristica.class)).thenReturn(consulta);
        when(consulta.getResultList()).thenReturn(List.of());
        assertTrue(dao.findByActivo(valor).isEmpty());
    }
}
