package sv.edu.ues.occ.ingenieria.ppi115_2026.cafefe.boundary.jsf;

import sv.edu.ues.occ.ingenieria.ppi115_2026.cafefe.boundary.jsf.RolModel;
import sv.edu.ues.occ.ingenieria.ppi115_2026.cafefe.boundary.jsf.Estado;
import jakarta.faces.application.FacesMessage;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import sv.edu.ues.occ.ingenieria.ppi115_2026.cafefe.entity.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import sv.edu.ues.occ.ingenieria.ppi115_2026.cafefe.control.EmpleadoRolDAO;
import sv.edu.ues.occ.ingenieria.ppi115_2026.cafefe.control.RolDAO;

/** Pruebas de comportamiento de RolModel, con DAO y contexto JSF simulados. */
class RolModelTest extends ModelTestSupport {

    @Mock RolDAO rolDAO;
    @Mock EmpleadoRolDAO empleadoRolDAO;
    @InjectMocks RolModel model;


    @Test
    void usaElDAOInyectado() {
        assertSame(rolDAO, model.getDAO());
    }

    @Test
    void nuevoRegistroGeneraIdentificadoresDiferentes() {
        Rol primero = model.nuevoRegistro();
        Rol segundo = model.nuevoRegistro();
        assertNotNull(primero.getIdRol());
        assertNotEquals(primero.getIdRol(), segundo.getIdRol());
        assertEquals(primero.getIdRol(), model.getIdRegistro(primero));
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
        verify(rolDAO, never()).crear(any());
        verify(rolDAO, never()).modificar(any());
        assertTrue(model.isCreando());
        mensaje(FacesMessage.SEVERITY_WARN, "nombre");
    }

    @Test
    void nombreVacioImpideGuardar() {
        model.nuevo();
        model.getRegistro().setNombre("");
        model.guardar();
        verify(rolDAO, never()).crear(any());
        verify(rolDAO, never()).modificar(any());
        assertTrue(model.isCreando());
        mensaje(FacesMessage.SEVERITY_WARN, "nombre");
    }

    @Test
    void nombreEnBlancoImpideGuardar() {
        model.nuevo();
        model.getRegistro().setNombre("   ");
        model.guardar();
        verify(rolDAO, never()).crear(any());
        verify(rolDAO, never()).modificar(any());
        assertTrue(model.isCreando());
        mensaje(FacesMessage.SEVERITY_WARN, "nombre");
    }

    @Test
    void registroValidoSeGuardaYRegresaAlListado() {
        model.nuevo();
        model.getRegistro().setNombre("  Café  ");
        when(rolDAO.crear(model.getRegistro())).thenReturn(true);
        model.guardar();
        assertTrue(model.isListado());
        verify(rolDAO).crear(argThat(x -> x.getNombre().equals("  Café  ")));
        mensaje(FacesMessage.SEVERITY_INFO, "guardado");
    }

    @Test
    void desactivacionConAsociados() {
        Rol original = model.nuevoRegistro();
        original.setNombre("Café");
        Rol editado = model.nuevoRegistro();
        editado.setIdRol(original.getIdRol());
        editado.setNombre("Café");
        editado.setActivo(false);
        when(rolDAO.buscarPorId(original.getIdRol())).thenReturn(editado, original);
        model.editar(original);
        when(empleadoRolDAO.contarPorRol(original.getIdRol())).thenReturn(2L);
        model.guardar();
        verify(rolDAO, never()).modificar(any());
        assertTrue(model.isEditando());
        mensaje(FacesMessage.SEVERITY_ERROR, "desactivar");
    }

    @Test
    void desactivacionSinAsociados() {
        Rol original = model.nuevoRegistro();
        original.setNombre("Café");
        Rol editado = model.nuevoRegistro();
        editado.setIdRol(original.getIdRol());
        editado.setNombre("Café");
        editado.setActivo(false);
        when(rolDAO.buscarPorId(original.getIdRol())).thenReturn(editado, original);
        model.editar(original);
        when(empleadoRolDAO.contarPorRol(original.getIdRol())).thenReturn(0L);
        when(rolDAO.modificar(editado)).thenReturn(true);
        model.guardar();
        verify(rolDAO).modificar(editado);
        assertTrue(model.isListado());
    }

    @Test
    void cancelarLimpiaSeleccion() {
        model.setSeleccionado(model.nuevoRegistro());
        model.cancelar();
        assertNull(model.getSeleccionado());
    }
}
