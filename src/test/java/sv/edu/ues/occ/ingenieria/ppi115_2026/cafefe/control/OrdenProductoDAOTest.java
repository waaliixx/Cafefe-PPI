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
import sv.edu.ues.occ.ingenieria.ppi115_2026.cafefe.entity.OrdenProducto;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrdenProductoDAOTest {
    @Mock EntityManager em;
    @Mock TypedQuery<OrdenProducto> consulta;
    @InjectMocks OrdenProductoDAO dao;

    @Test
    void entityManagerEsElInyectado() {
        assertSame(em, dao.getEntityManager());
    }

    @Test
    void buscarPorIdUsaEntidadCorrecta() {
        UUID id = UUID.randomUUID();
        OrdenProducto p = new OrdenProducto(id);
        when(em.find(OrdenProducto.class, id)).thenReturn(p);
        assertSame(p, dao.buscarPorId(id));
        verify(em).find(OrdenProducto.class, id);
    }

    @Test
    void findByOrdenFiltraYOrdena() {
        UUID id = UUID.randomUUID();
        when(em.createQuery("SELECT op FROM OrdenProducto op WHERE op.idOrden.idOrden = :id ORDER BY op.idProducto.nombre, op.idOrdenProducto", OrdenProducto.class)).thenReturn(consulta);
        when(consulta.setParameter("id", id)).thenReturn(consulta);
        List<OrdenProducto> datos = List.of(new OrdenProducto(UUID.randomUUID()));
        when(consulta.getResultList()).thenReturn(datos);
        assertSame(datos, dao.findByOrden(id));
        verify(consulta).setParameter("id", id);
    }

    @Test
    void findByOrdenNullNoConsulta() {
        assertTrue(dao.findByOrden(null).isEmpty());
        verifyNoInteractions(em);
    }

    @Test
    void findByOrdenSinEntityManagerNoConsulta() {
        assertTrue(new OrdenProductoDAO().findByOrden(UUID.randomUUID()).isEmpty());
        verifyNoInteractions(em);
    }

    @Test
    void findByOrdenSinResultados() {
        UUID id = UUID.randomUUID();
        when(em.createQuery("SELECT op FROM OrdenProducto op WHERE op.idOrden.idOrden = :id ORDER BY op.idProducto.nombre, op.idOrdenProducto", OrdenProducto.class)).thenReturn(consulta);
        when(consulta.setParameter("id", id)).thenReturn(consulta);
        when(consulta.getResultList()).thenReturn(List.of());
        assertTrue(dao.findByOrden(id).isEmpty());
    }

    @Test
    void findByOrdenErrorAlCrearConsulta() {
        UUID id = UUID.randomUUID();
        when(em.createQuery("SELECT op FROM OrdenProducto op WHERE op.idOrden.idOrden = :id ORDER BY op.idProducto.nombre, op.idOrdenProducto", OrdenProducto.class)).thenThrow(new PersistenceException("Fallo"));
        assertTrue(dao.findByOrden(id).isEmpty());
    }

    @Test
    void findByOrdenErrorAlEjecutarConsulta() {
        UUID id = UUID.randomUUID();
        when(em.createQuery("SELECT op FROM OrdenProducto op WHERE op.idOrden.idOrden = :id ORDER BY op.idProducto.nombre, op.idOrdenProducto", OrdenProducto.class)).thenReturn(consulta);
        when(consulta.setParameter("id", id)).thenReturn(consulta);
        when(consulta.getResultList()).thenThrow(new PersistenceException("Fallo"));
        assertTrue(dao.findByOrden(id).isEmpty());
    }
}
