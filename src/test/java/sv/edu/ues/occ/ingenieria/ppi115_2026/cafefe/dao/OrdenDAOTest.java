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
import sv.edu.ues.occ.ingenieria.ppi115_2026.cafefe.entity.Orden;

/** Pruebas unitarias: EntityManager y consultas simulados con Mockito. */
@ExtendWith(MockitoExtension.class)
class OrdenDAOTest {

    @Mock
    EntityManager em;

    @Mock
    TypedQuery<Orden> consulta;

    @InjectMocks
    OrdenDAO dao;

    @Test
    void entityManagerEsElInyectado() {
        assertSame(em, dao.getEntityManager());
    }

    @Test
    void buscarPorIdUsaLaClaseDeLaEntidad() {
        UUID id = UUID.randomUUID();
        Orden entidad = new Orden(id);
        when(em.find(Orden.class, id)).thenReturn(entidad);
        assertSame(entidad, dao.buscarPorId(id));
        verify(em).find(Orden.class, id);
    }

    @Test
    void findByFechaCreacionUsaConsultaYParametroCorrectos() {
        Date valor = new Date(1700000000000L);
        when(em.createNamedQuery("Orden.findByFechaCreacion", Orden.class)).thenReturn(consulta);
        List<Orden> esperado = List.of(new Orden(UUID.randomUUID()));
        when(consulta.getResultList()).thenReturn(esperado);
        assertSame(esperado, dao.findByFechaCreacion(valor));
        verify(consulta).setParameter("fechaCreacion", valor);
        verify(consulta).getResultList();
    }

    @Test
    void findByFechaCreacionConNullNoConsulta() {
        assertTrue(dao.findByFechaCreacion(null).isEmpty());
        verifyNoInteractions(em);
    }

    @Test
    void findByFechaCreacionConErrorDevuelveListaVacia() {
        Date valor = new Date(1700000000000L);
        PersistenceException error = new PersistenceException("Fallo simulado");
        when(em.createNamedQuery("Orden.findByFechaCreacion", Orden.class)).thenThrow(error);
        assertTrue(dao.findByFechaCreacion(valor).isEmpty());
    }

    @Test
    void findByFechaCreacionConErrorAlEjecutar() {
        Date valor = new Date(1700000000000L);
        when(em.createNamedQuery("Orden.findByFechaCreacion", Orden.class)).thenReturn(consulta);
        PersistenceException error = new PersistenceException("Fallo al ejecutar");
        when(consulta.getResultList()).thenThrow(error);
        assertTrue(dao.findByFechaCreacion(valor).isEmpty());
    }

    @Test
    void findByFechaCreacionSinEntityManagerDevuelveListaVacia() {
        Date valor = new Date(1700000000000L);
        assertTrue(new OrdenDAO().findByFechaCreacion(valor).isEmpty());
        verifyNoInteractions(em);
    }

    @Test
    void findByFechaCreacionSinCoincidenciasDevuelveListaVacia() {
        Date valor = new Date(1700000000000L);
        when(em.createNamedQuery("Orden.findByFechaCreacion", Orden.class)).thenReturn(consulta);
        when(consulta.getResultList()).thenReturn(List.of());
        assertTrue(dao.findByFechaCreacion(valor).isEmpty());
    }
}
