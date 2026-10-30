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
import sv.edu.ues.occ.ingenieria.ppi115_2026.cafefe.entity.EmpleadoRol;

/** Pruebas unitarias: EntityManager y consultas simulados con Mockito. */
@ExtendWith(MockitoExtension.class)
class EmpleadoRolDAOTest {

    @Mock
    EntityManager em;

    @Mock
    TypedQuery<EmpleadoRol> consulta;

    @Mock
    TypedQuery<Long> conteo;

    @InjectMocks
    EmpleadoRolDAO dao;

    @Test
    void entityManagerEsElInyectado() {
        assertSame(em, dao.getEntityManager());
    }

    @Test
    void buscarPorIdUsaLaClaseDeLaEntidad() {
        UUID id = UUID.randomUUID();
        EmpleadoRol entidad = new EmpleadoRol(id);
        when(em.find(EmpleadoRol.class, id)).thenReturn(entidad);
        assertSame(entidad, dao.buscarPorId(id));
        verify(em).find(EmpleadoRol.class, id);
    }

    @Test
    void findByEmpleadoUsaConsultaYParametroCorrectos() {
        UUID valor = UUID.randomUUID();
        when(em.createQuery("SELECT er FROM EmpleadoRol er WHERE er.idEmpleado = :id", EmpleadoRol.class)).thenReturn(consulta);
        when(consulta.setParameter("id", valor)).thenReturn(consulta);
        List<EmpleadoRol> esperado = List.of(new EmpleadoRol(UUID.randomUUID()));
        when(consulta.getResultList()).thenReturn(esperado);
        assertSame(esperado, dao.findByEmpleado(valor));
        verify(consulta).setParameter("id", valor);
        verify(consulta).getResultList();
    }

    @Test
    void findByEmpleadoConNullNoConsulta() {
        assertTrue(dao.findByEmpleado(null).isEmpty());
        verifyNoInteractions(em);
    }

    @Test
    void findByEmpleadoConErrorDevuelveListaVacia() {
        UUID valor = UUID.randomUUID();
        PersistenceException error = new PersistenceException("Fallo simulado");
        when(em.createQuery("SELECT er FROM EmpleadoRol er WHERE er.idEmpleado = :id", EmpleadoRol.class)).thenThrow(error);
        assertTrue(dao.findByEmpleado(valor).isEmpty());
    }

    @Test
    void findByEmpleadoConErrorAlEjecutar() {
        UUID valor = UUID.randomUUID();
        when(em.createQuery("SELECT er FROM EmpleadoRol er WHERE er.idEmpleado = :id", EmpleadoRol.class)).thenReturn(consulta);
        when(consulta.setParameter("id", valor)).thenReturn(consulta);
        PersistenceException error = new PersistenceException("Fallo al ejecutar");
        when(consulta.getResultList()).thenThrow(error);
        assertTrue(dao.findByEmpleado(valor).isEmpty());
    }

    @Test
    void findByEmpleadoSinEntityManagerDevuelveListaVacia() {
        UUID valor = UUID.randomUUID();
        assertTrue(new EmpleadoRolDAO().findByEmpleado(valor).isEmpty());
        verifyNoInteractions(em);
    }

    @Test
    void findByEmpleadoSinCoincidenciasDevuelveListaVacia() {
        UUID valor = UUID.randomUUID();
        when(em.createQuery("SELECT er FROM EmpleadoRol er WHERE er.idEmpleado = :id", EmpleadoRol.class)).thenReturn(consulta);
        when(consulta.setParameter("id", valor)).thenReturn(consulta);
        when(consulta.getResultList()).thenReturn(List.of());
        assertTrue(dao.findByEmpleado(valor).isEmpty());
    }

    @Test
    void contarPorRolUsaConsultaYParametroCorrectos() {
        UUID valor = UUID.randomUUID();
        when(em.createQuery("SELECT COUNT(er) FROM EmpleadoRol er WHERE er.idRol.idRol = :id", Long.class)).thenReturn(conteo);
        when(conteo.setParameter("id", valor)).thenReturn(conteo);
        when(conteo.getSingleResult()).thenReturn(7L);
        assertEquals(7L, dao.contarPorRol(valor));
        verify(conteo).setParameter("id", valor);
        verify(conteo).getSingleResult();
    }

    @Test
    void contarPorRolConNullNoConsulta() {
        assertEquals(0L, dao.contarPorRol(null));
        verifyNoInteractions(em);
    }

    @Test
    void contarPorRolConErrorPropagaExcepcion() {
        UUID valor = UUID.randomUUID();
        PersistenceException error = new PersistenceException("Fallo simulado");
        when(em.createQuery("SELECT COUNT(er) FROM EmpleadoRol er WHERE er.idRol.idRol = :id", Long.class)).thenThrow(error);
        assertSame(error, assertThrows(PersistenceException.class, () -> dao.contarPorRol(valor)));
    }

    @Test
    void contarPorRolConErrorAlEjecutar() {
        UUID valor = UUID.randomUUID();
        when(em.createQuery("SELECT COUNT(er) FROM EmpleadoRol er WHERE er.idRol.idRol = :id", Long.class)).thenReturn(conteo);
        when(conteo.setParameter("id", valor)).thenReturn(conteo);
        PersistenceException error = new PersistenceException("Fallo al ejecutar");
        when(conteo.getSingleResult()).thenThrow(error);
        assertSame(error, assertThrows(PersistenceException.class, () -> dao.contarPorRol(valor)));
    }

    @Test
    void findActivosPorNombreRolNormalizaYFiltraActivos() {
        when(em.createQuery("SELECT er FROM EmpleadoRol er WHERE LOWER(er.idRol.nombre) = :nombre AND er.activo = :activo", EmpleadoRol.class)).thenReturn(consulta);
        when(consulta.setParameter("nombre", "camarero")).thenReturn(consulta);
        when(consulta.setParameter("activo", Boolean.TRUE)).thenReturn(consulta);
        List<EmpleadoRol> datos = List.of(new EmpleadoRol(UUID.randomUUID()));
        when(consulta.getResultList()).thenReturn(datos);
        assertSame(datos, dao.findActivosPorNombreRol("  CaMaReRo  "));
        verify(consulta).setParameter("nombre", "camarero");
        verify(consulta).setParameter("activo", Boolean.TRUE);
    }

    @Test
    void findActivosPorNombreRolInvalidoNoConsulta() {
        assertTrue(dao.findActivosPorNombreRol(null).isEmpty());
        assertTrue(dao.findActivosPorNombreRol("").isEmpty());
        assertTrue(dao.findActivosPorNombreRol("  ").isEmpty());
        verifyNoInteractions(em);
    }

    @Test
    void findActivosPorNombreRolSinEntityManager() {
        assertTrue(new EmpleadoRolDAO().findActivosPorNombreRol("Camarero").isEmpty());
        verifyNoInteractions(em);
    }

    @Test
    void findActivosPorNombreRolSinResultados() {
        when(em.createQuery("SELECT er FROM EmpleadoRol er WHERE LOWER(er.idRol.nombre) = :nombre AND er.activo = :activo", EmpleadoRol.class)).thenReturn(consulta);
        when(consulta.setParameter("nombre", "camarero")).thenReturn(consulta);
        when(consulta.setParameter("activo", Boolean.TRUE)).thenReturn(consulta);
        when(consulta.getResultList()).thenReturn(List.of());
        assertTrue(dao.findActivosPorNombreRol("Camarero").isEmpty());
    }

    @Test
    void findActivosPorNombreRolErrorConstruccion() {
        when(em.createQuery("SELECT er FROM EmpleadoRol er WHERE LOWER(er.idRol.nombre) = :nombre AND er.activo = :activo", EmpleadoRol.class)).thenThrow(new PersistenceException("Fallo"));
        assertTrue(dao.findActivosPorNombreRol("Camarero").isEmpty());
    }

    @Test
    void findActivosPorNombreRolErrorEjecucion() {
        when(em.createQuery("SELECT er FROM EmpleadoRol er WHERE LOWER(er.idRol.nombre) = :nombre AND er.activo = :activo", EmpleadoRol.class)).thenReturn(consulta);
        when(consulta.setParameter("nombre", "camarero")).thenReturn(consulta);
        when(consulta.setParameter("activo", Boolean.TRUE)).thenReturn(consulta);
        when(consulta.getResultList()).thenThrow(new PersistenceException("Fallo"));
        assertTrue(dao.findActivosPorNombreRol("Camarero").isEmpty());
    }
}
