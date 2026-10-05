package sv.edu.ues.occ.ingenieria.ppi115_2026.cafefe.dao;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceException;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Root;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import sv.edu.ues.occ.ingenieria.ppi115_2026.cafefe.entity.Producto;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/** Verifica el comportamiento compartido por todos los DAO. */
@ExtendWith(MockitoExtension.class)
class AbstraccionDAOTest {

    @Mock EntityManager em;
    @Mock CriteriaBuilder builder;
    @Mock CriteriaQuery<Producto> criterio;
    @Mock Root<Producto> raiz;
    @Mock TypedQuery<Producto> consulta;
    @Mock CriteriaQuery<Long> criterioConteo;
    @Mock Expression<Long> expresionConteo;
    @Mock TypedQuery<Long> consultaConteo;

    AbstraccionDAO<Producto> dao;
    Producto producto;

    @BeforeEach
    void preparar() {
        dao = crearDAO(em);
        producto = new Producto(UUID.randomUUID());
    }

    private AbstraccionDAO<Producto> crearDAO(EntityManager manager) {
        return new AbstraccionDAO<Producto>(Producto.class) {
            @Override
            protected EntityManager getEntityManager() {
                return manager;
            }
        };
    }

    private void prepararConsulta() {
        when(em.getCriteriaBuilder()).thenReturn(builder);
        when(builder.createQuery(Producto.class)).thenReturn(criterio);
        when(criterio.from(Producto.class)).thenReturn(raiz);
        when(em.createQuery(criterio)).thenReturn(consulta);
    }

    private void prepararConteo() {
        when(em.getCriteriaBuilder()).thenReturn(builder);
        when(builder.createQuery(Long.class)).thenReturn(criterioConteo);
        when(criterioConteo.from(Producto.class)).thenReturn(raiz);
        when(builder.count(raiz)).thenReturn(expresionConteo);
        when(em.createQuery(criterioConteo)).thenReturn(consultaConteo);
    }

    @Test
    void crearUsaEntityManager() {
        assertTrue(dao.crear(producto));
        verify(em).persist(producto);
        verifyNoMoreInteractions(em);
    }

    @Test
    void crearConNullNoAccedeAPersistencia() {
        assertFalse(dao.crear(null));
        verifyNoInteractions(em);
    }

    @Test
    void crearSinEntityManagerDevuelveFalse() {
        assertFalse(crearDAO(null).crear(producto));
        verifyNoInteractions(em);
    }

    @Test
    void crearConErrorDevuelveFalse() {
        doThrow(new PersistenceException("Fallo simulado")).when(em).persist(producto);
        assertFalse(dao.crear(producto));
        verify(em).persist(producto);
    }

    @Test
    void modificarUsaEntityManager() {
        assertTrue(dao.modificar(producto));
        verify(em).merge(producto);
        verifyNoMoreInteractions(em);
    }

    @Test
    void modificarConNullNoAccedeAPersistencia() {
        assertFalse(dao.modificar(null));
        verifyNoInteractions(em);
    }

    @Test
    void modificarSinEntityManagerDevuelveFalse() {
        assertFalse(crearDAO(null).modificar(producto));
        verifyNoInteractions(em);
    }

    @Test
    void modificarConErrorDevuelveFalse() {
        doThrow(new PersistenceException("Fallo simulado")).when(em).merge(producto);
        assertFalse(dao.modificar(producto));
        verify(em).merge(producto);
    }

    @Test
    void eliminarFusionaYEliminaLaInstanciaAdministrada() {
        Producto administrado = new Producto(producto.getIdProducto());
        when(em.merge(producto)).thenReturn(administrado);
        assertTrue(dao.eliminar(producto));
        InOrder orden = inOrder(em);
        orden.verify(em).merge(producto);
        orden.verify(em).remove(same(administrado));
        verify(em, never()).remove(same(producto));
    }

    @Test
    void eliminarConNullNoAccedeAPersistencia() {
        assertFalse(dao.eliminar(null));
        verifyNoInteractions(em);
    }

    @Test
    void eliminarSinEntityManagerDevuelveFalse() {
        assertFalse(crearDAO(null).eliminar(producto));
        verifyNoInteractions(em);
    }

    @Test
    void eliminarSiMergeFallaNoElimina() {
        when(em.merge(producto)).thenThrow(new PersistenceException("Fallo al fusionar"));
        assertFalse(dao.eliminar(producto));
        verify(em, never()).remove(any());
    }

    @Test
    void eliminarSiRemoveFallaDevuelveFalse() {
        when(em.merge(producto)).thenReturn(producto);
        doThrow(new PersistenceException("Fallo al eliminar")).when(em).remove(producto);
        assertFalse(dao.eliminar(producto));
    }

    @Test
    void buscarPorIdDevuelveEntidadEncontrada() {
        UUID id = producto.getIdProducto();
        when(em.find(Producto.class, id)).thenReturn(producto);
        assertSame(producto, dao.buscarPorId(id));
        verify(em).find(Producto.class, id);
    }

    @Test
    void buscarPorIdSinCoincidenciaDevuelveNull() {
        assertNull(dao.buscarPorId(producto.getIdProducto()));
        verify(em).find(Producto.class, producto.getIdProducto());
    }

    @Test
    void buscarPorIdConNullNoConsulta() {
        assertNull(dao.buscarPorId(null));
        verifyNoInteractions(em);
    }

    @Test
    void buscarPorIdSinEntityManagerDevuelveNull() {
        assertNull(crearDAO(null).buscarPorId(producto.getIdProducto()));
        verifyNoInteractions(em);
    }

    @Test
    void buscarPorIdConErrorDevuelveNull() {
        when(em.find(Producto.class, producto.getIdProducto())).thenThrow(new PersistenceException("Fallo simulado"));
        assertNull(dao.buscarPorId(producto.getIdProducto()));
    }

    @Test
    void findAllConstruyeConsultaCorrecta() {
        prepararConsulta();
        List<Producto> esperado = List.of(producto);
        when(consulta.getResultList()).thenReturn(esperado);
        assertSame(esperado, dao.findAll());
        verify(criterio).select(raiz);
        verify(consulta, never()).setFirstResult(anyInt());
        verify(consulta, never()).setMaxResults(anyInt());
    }

    @Test
    void findAllSinCoincidenciasDevuelveListaVacia() {
        prepararConsulta();
        when(consulta.getResultList()).thenReturn(List.of());
        assertTrue(dao.findAll().isEmpty());
    }

    @Test
    void findAllSinEntityManagerDevuelveListaVacia() {
        assertTrue(crearDAO(null).findAll().isEmpty());
        verifyNoInteractions(em);
    }

    @Test
    void findAllConErrorAlConstruirConsulta() {
        when(em.getCriteriaBuilder()).thenThrow(new PersistenceException("Fallo de criterios"));
        assertTrue(dao.findAll().isEmpty());
    }

    @Test
    void findAllConErrorAlEjecutarConsulta() {
        prepararConsulta();
        when(consulta.getResultList()).thenThrow(new PersistenceException("Fallo de consulta"));
        assertTrue(dao.findAll().isEmpty());
    }

    @Test
    void findRangeConstruyeConsultaCorrecta() {
        prepararConsulta();
        List<Producto> esperado = List.of(producto);
        when(consulta.getResultList()).thenReturn(esperado);
        assertSame(esperado, dao.findRange(new int[]{5, 15}));
        verify(criterio).select(raiz);
        verify(consulta).setFirstResult(5);
        verify(consulta).setMaxResults(10);
    }

    @Test
    void findRangeSinCoincidenciasDevuelveListaVacia() {
        prepararConsulta();
        when(consulta.getResultList()).thenReturn(List.of());
        assertTrue(dao.findRange(new int[]{5, 15}).isEmpty());
    }

    @Test
    void findRangeSinEntityManagerDevuelveListaVacia() {
        assertTrue(crearDAO(null).findRange(new int[]{5, 15}).isEmpty());
        verifyNoInteractions(em);
    }

    @Test
    void findRangeConErrorAlConstruirConsulta() {
        when(em.getCriteriaBuilder()).thenThrow(new PersistenceException("Fallo de criterios"));
        assertTrue(dao.findRange(new int[]{5, 15}).isEmpty());
    }

    @Test
    void findRangeConErrorAlEjecutarConsulta() {
        prepararConsulta();
        when(consulta.getResultList()).thenThrow(new PersistenceException("Fallo de consulta"));
        assertTrue(dao.findRange(new int[]{5, 15}).isEmpty());
    }

    @Test
    void findRangeConRangoIncompletoNoConsulta() {
        assertTrue(dao.findRange(null).isEmpty());
        assertTrue(dao.findRange(new int[]{}).isEmpty());
        assertTrue(dao.findRange(new int[]{0}).isEmpty());
        verifyNoInteractions(em);
    }

    @Test
    void findRangeSiPaginacionEsRechazadaDevuelveListaVacia() {
        prepararConsulta();
        when(consulta.setMaxResults(-5)).thenThrow(new IllegalArgumentException("Rango invertido"));
        assertTrue(dao.findRange(new int[]{10, 5}).isEmpty());
        verify(consulta, never()).getResultList();
    }

    @Test
    void countConstruyeConsultaDeConteo() {
        prepararConteo();
        when(consultaConteo.getSingleResult()).thenReturn(7L);
        assertEquals(7, dao.count());
        verify(builder).count(raiz);
        verify(criterioConteo).select(expresionConteo);
    }

    @Test
    void countSinRegistrosDevuelveCero() {
        prepararConteo();
        when(consultaConteo.getSingleResult()).thenReturn(0L);
        assertEquals(0, dao.count());
    }

    @Test
    void countSinEntityManagerDevuelveCero() {
        assertEquals(0, crearDAO(null).count());
        verifyNoInteractions(em);
    }

    @Test
    void countConErrorAlConstruirDevuelveCero() {
        when(em.getCriteriaBuilder()).thenThrow(new PersistenceException("Fallo de criterios"));
        assertEquals(0, dao.count());
    }

    @Test
    void countConErrorAlEjecutarDevuelveCero() {
        prepararConteo();
        when(consultaConteo.getSingleResult()).thenThrow(new PersistenceException("Fallo de conteo"));
        assertEquals(0, dao.count());
    }
}
