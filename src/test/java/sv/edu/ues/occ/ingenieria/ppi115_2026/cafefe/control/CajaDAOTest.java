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
import sv.edu.ues.occ.ingenieria.ppi115_2026.cafefe.entity.Caja;

/** Pruebas unitarias: EntityManager y consultas simulados con Mockito. */
@ExtendWith(MockitoExtension.class)
class CajaDAOTest {

    @Mock
    EntityManager em;

    @Mock
    TypedQuery<Caja> consulta;

    @InjectMocks
    CajaDAO dao;

    @Test
    void entityManagerEsElInyectado() {
        assertSame(em, dao.getEntityManager());
    }

    @Test
    void buscarPorIdUsaLaClaseDeLaEntidad() {
        UUID id = UUID.randomUUID();
        Caja entidad = new Caja(id);
        when(em.find(Caja.class, id)).thenReturn(entidad);
        assertSame(entidad, dao.buscarPorId(id));
        verify(em).find(Caja.class, id);
    }

    @Test
    void findByNombreUsaConsultaYParametroCorrectos() {
        String valor = "Café";
        when(em.createNamedQuery("Caja.findByNombre", Caja.class)).thenReturn(consulta);
        List<Caja> esperado = List.of(new Caja(UUID.randomUUID()));
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
        when(em.createNamedQuery("Caja.findByNombre", Caja.class)).thenThrow(error);
        assertTrue(dao.findByNombre(valor).isEmpty());
    }

    @Test
    void findByNombreConErrorAlEjecutar() {
        String valor = "Café";
        when(em.createNamedQuery("Caja.findByNombre", Caja.class)).thenReturn(consulta);
        PersistenceException error = new PersistenceException("Fallo al ejecutar");
        when(consulta.getResultList()).thenThrow(error);
        assertTrue(dao.findByNombre(valor).isEmpty());
    }

    @Test
    void findByNombreSinEntityManagerDevuelveListaVacia() {
        String valor = "Café";
        assertTrue(new CajaDAO().findByNombre(valor).isEmpty());
        verifyNoInteractions(em);
    }

    @Test
    void findByNombreSinCoincidenciasDevuelveListaVacia() {
        String valor = "Café";
        when(em.createNamedQuery("Caja.findByNombre", Caja.class)).thenReturn(consulta);
        when(consulta.getResultList()).thenReturn(List.of());
        assertTrue(dao.findByNombre(valor).isEmpty());
    }

    @Test
    void findByActivoUsaConsultaYParametroCorrectos() {
        Boolean valor = Boolean.FALSE;
        when(em.createNamedQuery("Caja.findByActivo", Caja.class)).thenReturn(consulta);
        List<Caja> esperado = List.of(new Caja(UUID.randomUUID()));
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
        when(em.createNamedQuery("Caja.findByActivo", Caja.class)).thenThrow(error);
        assertTrue(dao.findByActivo(valor).isEmpty());
    }

    @Test
    void findByActivoConErrorAlEjecutar() {
        Boolean valor = Boolean.FALSE;
        when(em.createNamedQuery("Caja.findByActivo", Caja.class)).thenReturn(consulta);
        PersistenceException error = new PersistenceException("Fallo al ejecutar");
        when(consulta.getResultList()).thenThrow(error);
        assertTrue(dao.findByActivo(valor).isEmpty());
    }

    @Test
    void findByActivoSinEntityManagerDevuelveListaVacia() {
        Boolean valor = Boolean.FALSE;
        assertTrue(new CajaDAO().findByActivo(valor).isEmpty());
        verifyNoInteractions(em);
    }

    @Test
    void findByActivoSinCoincidenciasDevuelveListaVacia() {
        Boolean valor = Boolean.FALSE;
        when(em.createNamedQuery("Caja.findByActivo", Caja.class)).thenReturn(consulta);
        when(consulta.getResultList()).thenReturn(List.of());
        assertTrue(dao.findByActivo(valor).isEmpty());
    }
}
