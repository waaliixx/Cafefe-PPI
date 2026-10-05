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
import sv.edu.ues.occ.ingenieria.ppi115_2026.cafefe.entity.TipoProducto;

/** Pruebas unitarias: EntityManager y consultas simulados con Mockito. */
@ExtendWith(MockitoExtension.class)
class TipoProductoDAOTest {

    @Mock
    EntityManager em;

    @Mock
    TypedQuery<TipoProducto> consulta;

    @Mock
    TypedQuery<Long> conteo;

    @InjectMocks
    TipoProductoDAO dao;

    @Test
    void entityManagerEsElInyectado() {
        assertSame(em, dao.getEntityManager());
    }

    @Test
    void buscarPorIdUsaLaClaseDeLaEntidad() {
        UUID id = UUID.randomUUID();
        TipoProducto entidad = new TipoProducto(id);
        when(em.find(TipoProducto.class, id)).thenReturn(entidad);
        assertSame(entidad, dao.buscarPorId(id));
        verify(em).find(TipoProducto.class, id);
    }

    @Test
    void findByNombreUsaConsultaYParametroCorrectos() {
        String valor = "Café";
        when(em.createNamedQuery("TipoProducto.findByNombre", TipoProducto.class)).thenReturn(consulta);
        List<TipoProducto> esperado = List.of(new TipoProducto(UUID.randomUUID()));
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
        when(em.createNamedQuery("TipoProducto.findByNombre", TipoProducto.class)).thenThrow(error);
        assertTrue(dao.findByNombre(valor).isEmpty());
    }

    @Test
    void findByNombreConErrorAlEjecutar() {
        String valor = "Café";
        when(em.createNamedQuery("TipoProducto.findByNombre", TipoProducto.class)).thenReturn(consulta);
        PersistenceException error = new PersistenceException("Fallo al ejecutar");
        when(consulta.getResultList()).thenThrow(error);
        assertTrue(dao.findByNombre(valor).isEmpty());
    }

    @Test
    void findByNombreSinEntityManagerDevuelveListaVacia() {
        String valor = "Café";
        assertTrue(new TipoProductoDAO().findByNombre(valor).isEmpty());
        verifyNoInteractions(em);
    }

    @Test
    void findByNombreSinCoincidenciasDevuelveListaVacia() {
        String valor = "Café";
        when(em.createNamedQuery("TipoProducto.findByNombre", TipoProducto.class)).thenReturn(consulta);
        when(consulta.getResultList()).thenReturn(List.of());
        assertTrue(dao.findByNombre(valor).isEmpty());
    }

    @Test
    void findByActivoUsaConsultaYParametroCorrectos() {
        Boolean valor = Boolean.FALSE;
        when(em.createNamedQuery("TipoProducto.findByActivo", TipoProducto.class)).thenReturn(consulta);
        List<TipoProducto> esperado = List.of(new TipoProducto(UUID.randomUUID()));
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
        when(em.createNamedQuery("TipoProducto.findByActivo", TipoProducto.class)).thenThrow(error);
        assertTrue(dao.findByActivo(valor).isEmpty());
    }

    @Test
    void findByActivoConErrorAlEjecutar() {
        Boolean valor = Boolean.FALSE;
        when(em.createNamedQuery("TipoProducto.findByActivo", TipoProducto.class)).thenReturn(consulta);
        PersistenceException error = new PersistenceException("Fallo al ejecutar");
        when(consulta.getResultList()).thenThrow(error);
        assertTrue(dao.findByActivo(valor).isEmpty());
    }

    @Test
    void findByActivoSinEntityManagerDevuelveListaVacia() {
        Boolean valor = Boolean.FALSE;
        assertTrue(new TipoProductoDAO().findByActivo(valor).isEmpty());
        verifyNoInteractions(em);
    }

    @Test
    void findByActivoSinCoincidenciasDevuelveListaVacia() {
        Boolean valor = Boolean.FALSE;
        when(em.createNamedQuery("TipoProducto.findByActivo", TipoProducto.class)).thenReturn(consulta);
        when(consulta.getResultList()).thenReturn(List.of());
        assertTrue(dao.findByActivo(valor).isEmpty());
    }

    @Test
    void contarProductosUsaConsultaYParametroCorrectos() {
        UUID valor = UUID.randomUUID();
        when(em.createQuery("SELECT COUNT(p) FROM ProductoTipoProducto p WHERE p.idTipoProducto.idTipoProducto = :id", Long.class)).thenReturn(conteo);
        when(conteo.setParameter("id", valor)).thenReturn(conteo);
        when(conteo.getSingleResult()).thenReturn(7L);
        assertEquals(7L, dao.contarProductos(valor));
        verify(conteo).setParameter("id", valor);
        verify(conteo).getSingleResult();
    }

    @Test
    void contarProductosConNullNoConsulta() {
        assertEquals(0L, dao.contarProductos(null));
        verifyNoInteractions(em);
    }

    @Test
    void contarProductosConErrorPropagaExcepcion() {
        UUID valor = UUID.randomUUID();
        PersistenceException error = new PersistenceException("Fallo simulado");
        when(em.createQuery("SELECT COUNT(p) FROM ProductoTipoProducto p WHERE p.idTipoProducto.idTipoProducto = :id", Long.class)).thenThrow(error);
        assertSame(error, assertThrows(PersistenceException.class, () -> dao.contarProductos(valor)));
    }

    @Test
    void contarProductosConErrorAlEjecutar() {
        UUID valor = UUID.randomUUID();
        when(em.createQuery("SELECT COUNT(p) FROM ProductoTipoProducto p WHERE p.idTipoProducto.idTipoProducto = :id", Long.class)).thenReturn(conteo);
        when(conteo.setParameter("id", valor)).thenReturn(conteo);
        PersistenceException error = new PersistenceException("Fallo al ejecutar");
        when(conteo.getSingleResult()).thenThrow(error);
        assertSame(error, assertThrows(PersistenceException.class, () -> dao.contarProductos(valor)));
    }
}
