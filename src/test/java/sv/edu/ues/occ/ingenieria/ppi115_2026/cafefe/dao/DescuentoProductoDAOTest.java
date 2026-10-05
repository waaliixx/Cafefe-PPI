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
import sv.edu.ues.occ.ingenieria.ppi115_2026.cafefe.entity.DescuentoProducto;

/** Pruebas unitarias: EntityManager y consultas simulados con Mockito. */
@ExtendWith(MockitoExtension.class)
class DescuentoProductoDAOTest {

    @Mock
    EntityManager em;

    @Mock
    TypedQuery<DescuentoProducto> consulta;

    @InjectMocks
    DescuentoProductoDAO dao;

    @Test
    void entityManagerEsElInyectado() {
        assertSame(em, dao.getEntityManager());
    }

    @Test
    void buscarPorIdUsaLaClaseDeLaEntidad() {
        UUID id = UUID.randomUUID();
        DescuentoProducto entidad = new DescuentoProducto(id);
        when(em.find(DescuentoProducto.class, id)).thenReturn(entidad);
        assertSame(entidad, dao.buscarPorId(id));
        verify(em).find(DescuentoProducto.class, id);
    }

    @Test
    void findByProductoUsaConsultaYParametroCorrectos() {
        UUID valor = UUID.randomUUID();
        when(em.createQuery("SELECT d FROM DescuentoProducto d WHERE d.idProducto.idProducto = :id ORDER BY d.fechaDesde", DescuentoProducto.class)).thenReturn(consulta);
        when(consulta.setParameter("id", valor)).thenReturn(consulta);
        List<DescuentoProducto> esperado = List.of(new DescuentoProducto(UUID.randomUUID()));
        when(consulta.getResultList()).thenReturn(esperado);
        assertSame(esperado, dao.findByProducto(valor));
        verify(consulta).setParameter("id", valor);
        verify(consulta).getResultList();
    }

    @Test
    void findByProductoConNullNoConsulta() {
        assertTrue(dao.findByProducto(null).isEmpty());
        verifyNoInteractions(em);
    }

    @Test
    void findByProductoConErrorPropagaExcepcion() {
        UUID valor = UUID.randomUUID();
        PersistenceException error = new PersistenceException("Fallo simulado");
        when(em.createQuery("SELECT d FROM DescuentoProducto d WHERE d.idProducto.idProducto = :id ORDER BY d.fechaDesde", DescuentoProducto.class)).thenThrow(error);
        assertSame(error, assertThrows(PersistenceException.class, () -> dao.findByProducto(valor)));
    }

    @Test
    void findByProductoConErrorAlEjecutar() {
        UUID valor = UUID.randomUUID();
        when(em.createQuery("SELECT d FROM DescuentoProducto d WHERE d.idProducto.idProducto = :id ORDER BY d.fechaDesde", DescuentoProducto.class)).thenReturn(consulta);
        when(consulta.setParameter("id", valor)).thenReturn(consulta);
        PersistenceException error = new PersistenceException("Fallo al ejecutar");
        when(consulta.getResultList()).thenThrow(error);
        assertSame(error, assertThrows(PersistenceException.class, () -> dao.findByProducto(valor)));
    }

    @Test
    void findByProductoSinCoincidenciasDevuelveListaVacia() {
        UUID valor = UUID.randomUUID();
        when(em.createQuery("SELECT d FROM DescuentoProducto d WHERE d.idProducto.idProducto = :id ORDER BY d.fechaDesde", DescuentoProducto.class)).thenReturn(consulta);
        when(consulta.setParameter("id", valor)).thenReturn(consulta);
        when(consulta.getResultList()).thenReturn(List.of());
        assertTrue(dao.findByProducto(valor).isEmpty());
    }
}
