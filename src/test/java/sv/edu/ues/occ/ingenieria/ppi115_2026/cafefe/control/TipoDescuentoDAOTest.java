package sv.edu.ues.occ.ingenieria.ppi115_2026.cafefe.control;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceException;
import jakarta.persistence.TypedQuery;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import sv.edu.ues.occ.ingenieria.ppi115_2026.cafefe.entity.TipoDescuento;

/** Pruebas unitarias: EntityManager y consultas simulados con Mockito. */
@ExtendWith(MockitoExtension.class)
class TipoDescuentoDAOTest {

    @Mock
    EntityManager em;

    @Mock
    TypedQuery<TipoDescuento> consulta;

    @InjectMocks
    TipoDescuentoDAO dao;

    @Test
    void entityManagerEsElInyectado() {
        assertSame(em, dao.getEntityManager());
    }

    @Test
    void buscarPorIdUsaLaClaseDeLaEntidad() {
        UUID id = UUID.randomUUID();
        TipoDescuento entidad = new TipoDescuento(id);
        when(em.find(TipoDescuento.class, id)).thenReturn(entidad);
        assertSame(entidad, dao.buscarPorId(id));
        verify(em).find(TipoDescuento.class, id);
    }

    @Test
    void findByNombreUsaConsultaYParametroCorrectos() {
        String valor = "Café";
        when(em.createNamedQuery("TipoDescuento.findByNombre", TipoDescuento.class)).thenReturn(consulta);
        List<TipoDescuento> esperado = List.of(new TipoDescuento(UUID.randomUUID()));
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
        when(em.createNamedQuery("TipoDescuento.findByNombre", TipoDescuento.class)).thenThrow(error);
        assertTrue(dao.findByNombre(valor).isEmpty());
    }

    @Test
    void findByNombreConErrorAlEjecutar() {
        String valor = "Café";
        when(em.createNamedQuery("TipoDescuento.findByNombre", TipoDescuento.class)).thenReturn(consulta);
        PersistenceException error = new PersistenceException("Fallo al ejecutar");
        when(consulta.getResultList()).thenThrow(error);
        assertTrue(dao.findByNombre(valor).isEmpty());
    }

    @Test
    void findByNombreSinEntityManagerDevuelveListaVacia() {
        String valor = "Café";
        assertTrue(new TipoDescuentoDAO().findByNombre(valor).isEmpty());
        verifyNoInteractions(em);
    }

    @Test
    void findByNombreSinCoincidenciasDevuelveListaVacia() {
        String valor = "Café";
        when(em.createNamedQuery("TipoDescuento.findByNombre", TipoDescuento.class)).thenReturn(consulta);
        when(consulta.getResultList()).thenReturn(List.of());
        assertTrue(dao.findByNombre(valor).isEmpty());
    }

    @Test
    void findByActivoUsaConsultaYParametroCorrectos() {
        Boolean valor = Boolean.FALSE;
        when(em.createNamedQuery("TipoDescuento.findByActivo", TipoDescuento.class)).thenReturn(consulta);
        List<TipoDescuento> esperado = List.of(new TipoDescuento(UUID.randomUUID()));
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
        when(em.createNamedQuery("TipoDescuento.findByActivo", TipoDescuento.class)).thenThrow(error);
        assertTrue(dao.findByActivo(valor).isEmpty());
    }

    @Test
    void findByActivoConErrorAlEjecutar() {
        Boolean valor = Boolean.FALSE;
        when(em.createNamedQuery("TipoDescuento.findByActivo", TipoDescuento.class)).thenReturn(consulta);
        PersistenceException error = new PersistenceException("Fallo al ejecutar");
        when(consulta.getResultList()).thenThrow(error);
        assertTrue(dao.findByActivo(valor).isEmpty());
    }

    @Test
    void findByActivoSinEntityManagerDevuelveListaVacia() {
        Boolean valor = Boolean.FALSE;
        assertTrue(new TipoDescuentoDAO().findByActivo(valor).isEmpty());
        verifyNoInteractions(em);
    }

    @Test
    void findByActivoSinCoincidenciasDevuelveListaVacia() {
        Boolean valor = Boolean.FALSE;
        when(em.createNamedQuery("TipoDescuento.findByActivo", TipoDescuento.class)).thenReturn(consulta);
        when(consulta.getResultList()).thenReturn(List.of());
        assertTrue(dao.findByActivo(valor).isEmpty());
    }

    @Test
    void findByDescuentoMaximoUsaConsultaYParametroCorrectos() {
        Integer valor = 15;
        when(em.createNamedQuery("TipoDescuento.findByDescuentoMaximo", TipoDescuento.class)).thenReturn(consulta);
        List<TipoDescuento> esperado = List.of(new TipoDescuento(UUID.randomUUID()));
        when(consulta.getResultList()).thenReturn(esperado);
        assertSame(esperado, dao.findByDescuentoMaximo(valor));
        verify(consulta).setParameter("descuentoMaximo", valor);
        verify(consulta).getResultList();
    }

    @Test
    void findByDescuentoMaximoConNullNoConsulta() {
        assertTrue(dao.findByDescuentoMaximo(null).isEmpty());
        verifyNoInteractions(em);
    }

    @Test
    void findByDescuentoMaximoConErrorDevuelveListaVacia() {
        Integer valor = 15;
        PersistenceException error = new PersistenceException("Fallo simulado");
        when(em.createNamedQuery("TipoDescuento.findByDescuentoMaximo", TipoDescuento.class)).thenThrow(error);
        assertTrue(dao.findByDescuentoMaximo(valor).isEmpty());
    }

    @Test
    void findByDescuentoMaximoConErrorAlEjecutar() {
        Integer valor = 15;
        when(em.createNamedQuery("TipoDescuento.findByDescuentoMaximo", TipoDescuento.class)).thenReturn(consulta);
        PersistenceException error = new PersistenceException("Fallo al ejecutar");
        when(consulta.getResultList()).thenThrow(error);
        assertTrue(dao.findByDescuentoMaximo(valor).isEmpty());
    }

    @Test
    void findByDescuentoMaximoSinEntityManagerDevuelveListaVacia() {
        Integer valor = 15;
        assertTrue(new TipoDescuentoDAO().findByDescuentoMaximo(valor).isEmpty());
        verifyNoInteractions(em);
    }

    @Test
    void findByDescuentoMaximoSinCoincidenciasDevuelveListaVacia() {
        Integer valor = 15;
        when(em.createNamedQuery("TipoDescuento.findByDescuentoMaximo", TipoDescuento.class)).thenReturn(consulta);
        when(consulta.getResultList()).thenReturn(List.of());
        assertTrue(dao.findByDescuentoMaximo(valor).isEmpty());
    }

    @Test
    void findRangeAplicaOrdenYPaginacion() {
        when(em.createQuery("SELECT t FROM TipoDescuento t ORDER BY t.nombre, t.idTipoDescuento", TipoDescuento.class)).thenReturn(consulta);
        when(consulta.setFirstResult(5)).thenReturn(consulta);
        when(consulta.setMaxResults(10)).thenReturn(consulta);
        List<TipoDescuento> esperado = List.of(new TipoDescuento(UUID.randomUUID()));
        when(consulta.getResultList()).thenReturn(esperado);
        assertSame(esperado, dao.findRange(new int[]{5, 15}));
        InOrder orden = inOrder(consulta);
        orden.verify(consulta).setFirstResult(5);
        orden.verify(consulta).setMaxResults(10);
        orden.verify(consulta).getResultList();
    }

    @Test
    void findRangeInvalidoNoConsulta() {
        assertTrue(dao.findRange(null).isEmpty());
        assertTrue(dao.findRange(new int[]{}).isEmpty());
        assertTrue(dao.findRange(new int[]{0}).isEmpty());
        assertTrue(dao.findRange(new int[]{-1, 10}).isEmpty());
        assertTrue(dao.findRange(new int[]{5, 5}).isEmpty());
        assertTrue(dao.findRange(new int[]{10, 5}).isEmpty());
        verifyNoInteractions(em);
    }

    @Test
    void findRangePropagaErrorDePersistencia() {
        PersistenceException error = new PersistenceException("Fallo simulado");
        when(em.createQuery("SELECT t FROM TipoDescuento t ORDER BY t.nombre, t.idTipoDescuento", TipoDescuento.class)).thenThrow(error);
        assertSame(error, assertThrows(PersistenceException.class, () -> dao.findRange(new int[]{0, 10})));
    }
}
