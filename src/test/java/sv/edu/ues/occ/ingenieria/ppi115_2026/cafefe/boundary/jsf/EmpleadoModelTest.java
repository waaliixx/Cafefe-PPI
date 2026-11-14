package sv.edu.ues.occ.ingenieria.ppi115_2026.cafefe.boundary.jsf;

import sv.edu.ues.occ.ingenieria.ppi115_2026.cafefe.boundary.jsf.EmpleadoModel;
import sv.edu.ues.occ.ingenieria.ppi115_2026.cafefe.boundary.jsf.Estado;
import jakarta.faces.application.FacesMessage;
import jakarta.persistence.PersistenceException;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.InOrder;
import org.mockito.Mock;
import sv.edu.ues.occ.ingenieria.ppi115_2026.cafefe.entity.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import sv.edu.ues.occ.ingenieria.ppi115_2026.cafefe.control.EmpleadoDAO;
import sv.edu.ues.occ.ingenieria.ppi115_2026.cafefe.control.EmpleadoRolDAO;
import sv.edu.ues.occ.ingenieria.ppi115_2026.cafefe.control.RolDAO;

/** Pruebas de comportamiento de EmpleadoModel, con DAO y contexto JSF simulados. */
class EmpleadoModelTest extends ModelTestSupport {

    @Mock EmpleadoDAO empleadoDAO;
    @Mock EmpleadoRolDAO empleadoRolDAO;
    @Mock RolDAO rolDAO;
    @InjectMocks EmpleadoModel model;

    private Empleado prepararEmpleado() {
        model.nuevo();
        model.getRegistro().setNombre("Ana");
        model.getRegistro().setApellido("Pérez");
        return model.getRegistro();
    }

    private Rol rol(boolean activo) {
        Rol rol = new Rol(UUID.randomUUID());
        rol.setNombre("Camarero");
        rol.setActivo(activo);
        return rol;
    }

    private EmpleadoRol asignacion(Empleado emp, Rol rol) {
        EmpleadoRol er = new EmpleadoRol(UUID.randomUUID());
        er.setIdEmpleado(emp.getIdEmpleado());
        er.setIdRol(rol);
        return er;
    }

    @Test
    void usaElDAOInyectado() {
        assertSame(empleadoDAO, model.getDAO());
    }

    @Test
    void nuevoRegistroGeneraIdentificadoresDiferentes() {
        Empleado primero = model.nuevoRegistro();
        Empleado segundo = model.nuevoRegistro();
        assertNotNull(primero.getIdEmpleado());
        assertNotEquals(primero.getIdEmpleado(), segundo.getIdEmpleado());
        assertEquals(primero.getIdEmpleado(), model.getIdRegistro(primero));
    }

    @Test
    void nuevoRegistroEstaActivo() {
        assertTrue(model.nuevoRegistro().getActivo());
    }

    @Test
    void cancelarVuelveAlListado() {
        model.nuevo();
        model.cancelar();
        assertNull(model.getRegistro());
        assertEquals(Estado.NINGUNO, model.getEstado());
        assertTrue(model.isListado());
        assertFalse(model.isMostrarFormulario());
    }

    @Test
    void registroNuloNoEsValido() {
        assertFalse(model.validarRegistro());
    }

    @Test
    void nombreNuloImpideGuardar() {
        model.nuevo();
        model.getRegistro().setNombre(null);
        model.guardar();
        verify(empleadoDAO, never()).crear(any());
        verify(empleadoDAO, never()).modificar(any());
        assertTrue(model.isCreando());
        mensaje(FacesMessage.SEVERITY_WARN, "nombre");
    }

    @Test
    void nombreVacioImpideGuardar() {
        model.nuevo();
        model.getRegistro().setNombre("");
        model.guardar();
        verify(empleadoDAO, never()).crear(any());
        verify(empleadoDAO, never()).modificar(any());
        assertTrue(model.isCreando());
        mensaje(FacesMessage.SEVERITY_WARN, "nombre");
    }

    @Test
    void nombreEnBlancoImpideGuardar() {
        model.nuevo();
        model.getRegistro().setNombre("   ");
        model.guardar();
        verify(empleadoDAO, never()).crear(any());
        verify(empleadoDAO, never()).modificar(any());
        assertTrue(model.isCreando());
        mensaje(FacesMessage.SEVERITY_WARN, "nombre");
    }

    @Test
    void apellidoNuloImpideGuardar() {
        prepararEmpleado();
        model.getRegistro().setApellido(null);
        model.guardar();
        verify(empleadoDAO, never()).crear(any());
        mensaje(FacesMessage.SEVERITY_WARN, "apellido");
    }

    @Test
    void apellidoVacioImpideGuardar() {
        prepararEmpleado();
        model.getRegistro().setApellido("");
        model.guardar();
        verify(empleadoDAO, never()).crear(any());
        mensaje(FacesMessage.SEVERITY_WARN, "apellido");
    }

    @Test
    void apellidoEnBlancoImpideGuardar() {
        prepararEmpleado();
        model.getRegistro().setApellido("  ");
        model.guardar();
        verify(empleadoDAO, never()).crear(any());
        mensaje(FacesMessage.SEVERITY_WARN, "apellido");
    }

    @Test
    void empleadoSinRolPuedeGuardarse() {
        Empleado e = prepararEmpleado();
        when(empleadoDAO.crear(e)).thenReturn(true);
        model.guardar();
        assertTrue(model.isListado());
        verify(empleadoRolDAO, never()).crear(any());
        assertEquals("Sin rol", model.getRolTexto(e));
    }

    @Test
    void guardarConRolActivoCreaAsignacion() {
        Empleado e = prepararEmpleado();
        Rol r = rol(true);
        model.setIdRolSeleccionado("  " + r.getIdRol() + "  ");
        when(rolDAO.buscarPorId(r.getIdRol())).thenReturn(r);
        when(empleadoDAO.crear(e)).thenReturn(true);
        when(empleadoRolDAO.crear(any())).thenReturn(true);
        model.guardar();
        ArgumentCaptor<EmpleadoRol> asignacion = ArgumentCaptor.forClass(EmpleadoRol.class);
        verify(empleadoRolDAO).crear(asignacion.capture());
        assertEquals(e.getIdEmpleado(), asignacion.getValue().getIdEmpleado());
        assertSame(r, asignacion.getValue().getIdRol());
        assertTrue(asignacion.getValue().getActivo());
        assertNotNull(asignacion.getValue().getIdEmpleadoRol());
        assertTrue(model.isListado());
    }

    @Test
    void rolInactivoImpideGuardar() {
        prepararEmpleado();
        Rol r = rol(false);
        model.setIdRolSeleccionado(r.getIdRol().toString());
        when(rolDAO.buscarPorId(r.getIdRol())).thenReturn(r);
        model.guardar();
        verify(empleadoDAO, never()).crear(any());
        verify(empleadoRolDAO, never()).crear(any());
        mensaje(FacesMessage.SEVERITY_ERROR, "rol");
    }

    @Test
    void rolInexistenteImpideGuardar() {
        prepararEmpleado();
        Rol r = rol(false);
        model.setIdRolSeleccionado(r.getIdRol().toString());
        model.guardar();
        verify(empleadoDAO, never()).crear(any());
        verify(empleadoRolDAO, never()).crear(any());
        mensaje(FacesMessage.SEVERITY_ERROR, "rol");
    }

    @Test
    void rolUUIDInvalidoImpideGuardar() {
        prepararEmpleado();
        model.setIdRolSeleccionado("incorrecto");
        model.guardar();
        verify(empleadoDAO, never()).crear(any());
        mensaje(FacesMessage.SEVERITY_ERROR, "validar el rol");
    }

    @Test
    void conservarRolActualNoLoRecreaAunqueInactivo() {
        Empleado e = model.nuevoRegistro();
        e.setNombre("Ana");
        e.setApellido("Pérez");
        Rol r = rol(false);
        EmpleadoRol er = asignacion(e, r);
        when(empleadoDAO.buscarPorId(e.getIdEmpleado())).thenReturn(e);
        when(empleadoRolDAO.findByEmpleado(e.getIdEmpleado())).thenReturn(List.of(er));
        when(empleadoDAO.modificar(e)).thenReturn(true);
        model.editar(e);
        assertEquals(r.getIdRol().toString(), model.getIdRolSeleccionado());
        model.guardar();
        verify(empleadoRolDAO, never()).crear(any());
        verify(empleadoRolDAO, never()).eliminar(any());
        verify(rolDAO, never()).buscarPorId(any());
    }

    @Test
    void guardarSinRolQuitaAsignacionesAnteriores() {
        Empleado e = prepararEmpleado();
        EmpleadoRol er = asignacion(e, rol(true));
        when(empleadoDAO.crear(e)).thenReturn(true);
        when(empleadoRolDAO.findByEmpleado(e.getIdEmpleado())).thenReturn(List.of(er));
        when(empleadoRolDAO.eliminar(er)).thenReturn(true);
        model.guardar();
        verify(empleadoRolDAO).eliminar(er);
        verify(empleadoRolDAO, never()).crear(any());
    }

    @Test
    void falloAlGuardarNoSincronizaRoles() {
        Empleado e = prepararEmpleado();
        model.guardar();
        assertSame(e, model.getRegistro());
        verify(empleadoRolDAO, never()).findByEmpleado(any());
    }

    @Test
    void falloAlAsignarRolSeInformaTrasGuardarEmpleado() {
        Empleado e = prepararEmpleado();
        Rol r = rol(true);
        model.setIdRolSeleccionado(r.getIdRol().toString());
        when(rolDAO.buscarPorId(r.getIdRol())).thenReturn(r);
        when(empleadoDAO.crear(e)).thenReturn(true);
        model.guardar();
        assertTrue(model.isListado());
        mensaje(FacesMessage.SEVERITY_ERROR, "asignar el rol");
    }

    @Test
    void eliminarEmpleadoConRolesExito() {
        Empleado e = model.nuevoRegistro();
        EmpleadoRol er = asignacion(e, rol(true));
        when(empleadoRolDAO.findByEmpleado(e.getIdEmpleado())).thenReturn(List.of(er));
        when(empleadoRolDAO.eliminar(er)).thenReturn(true);
        when(empleadoDAO.eliminar(e)).thenReturn(true);
        model.eliminar(e);
        InOrder orden = inOrder(empleadoRolDAO, empleadoDAO);
        orden.verify(empleadoRolDAO).eliminar(er);
        orden.verify(empleadoDAO).eliminar(e);
    }

    @Test
    void eliminarEmpleadoConRolesFalse() {
        Empleado e = model.nuevoRegistro();
        EmpleadoRol er = asignacion(e, rol(true));
        when(empleadoRolDAO.findByEmpleado(e.getIdEmpleado())).thenReturn(List.of(er));
        model.eliminar(e);
        verify(empleadoDAO, never()).eliminar(any());
        mensaje(FacesMessage.SEVERITY_ERROR, "quitar");
    }

    @Test
    void eliminarEmpleadoConRolesExcepcion() {
        Empleado e = model.nuevoRegistro();
        EmpleadoRol er = asignacion(e, rol(true));
        when(empleadoRolDAO.findByEmpleado(e.getIdEmpleado())).thenReturn(List.of(er));
        when(empleadoRolDAO.eliminar(er)).thenThrow(new PersistenceException("Fallo"));
        model.eliminar(e);
        verify(empleadoDAO, never()).eliminar(any());
        mensaje(FacesMessage.SEVERITY_ERROR, "eliminar");
    }

    @Test
    void resumenConcatenaRolesPorEmpleado() {
        Empleado e = model.nuevoRegistro();
        Rol a = rol(true);
        Rol b = rol(true);
        b.setNombre("Cajero");
        when(empleadoRolDAO.findAll()).thenReturn(List.of(asignacion(e, a), asignacion(e, b)));
        model.cargarRegistros();
        assertEquals("Camarero, Cajero", model.getRolTexto(e));
        assertEquals("Sin rol", model.getRolTexto(null));
        assertEquals("Sin rol", model.getRolTexto(model.nuevoRegistro()));
    }

    @Test
    void cancelarLimpiaRolElegido() {
        prepararEmpleado();
        model.setIdRolSeleccionado(UUID.randomUUID().toString());
        model.cancelar();
        assertNull(model.getIdRolSeleccionado());
    }

    @Test
    void nuevoCargaRolesDisponiblesYLimpiaSeleccion() {
        Rol r = rol(true);
        when(rolDAO.findAll()).thenReturn(List.of(r));
        model.setSeleccionado(model.nuevoRegistro());
        model.nuevo();
        assertNull(model.getSeleccionado());
        assertEquals(List.of(r), model.getRolesDisponibles());
    }

    @Test
    void cargarRegistrosEntregaListadoDelDAO() {
        Empleado e = model.nuevoRegistro();
        when(empleadoDAO.findAll()).thenReturn(List.of(e));
        model.cargarRegistros();
        assertEquals(List.of(e), model.getRegistros());
    }

}
