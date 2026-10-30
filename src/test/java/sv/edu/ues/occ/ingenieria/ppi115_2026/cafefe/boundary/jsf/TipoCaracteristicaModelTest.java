package sv.edu.ues.occ.ingenieria.ppi115_2026.cafefe.boundary.jsf;

import sv.edu.ues.occ.ingenieria.ppi115_2026.cafefe.boundary.jsf.TipoCaracteristicaModel;
import sv.edu.ues.occ.ingenieria.ppi115_2026.cafefe.boundary.jsf.Estado;
import jakarta.faces.application.FacesMessage;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import sv.edu.ues.occ.ingenieria.ppi115_2026.cafefe.entity.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import sv.edu.ues.occ.ingenieria.ppi115_2026.cafefe.control.CaracteristicaDAO;
import sv.edu.ues.occ.ingenieria.ppi115_2026.cafefe.control.TipoCaracteristicaDAO;

/** Pruebas de comportamiento de TipoCaracteristicaModel, con DAO y contexto JSF simulados. */
class TipoCaracteristicaModelTest extends ModelTestSupport {

    @Mock TipoCaracteristicaDAO tipoCaracteristicaDAO;
    @Mock CaracteristicaDAO caracteristicaDAO;
    @InjectMocks TipoCaracteristicaModel model;


    @Test
    void usaElDAOInyectado() {
        assertSame(tipoCaracteristicaDAO, model.getDAO());
    }

    @Test
    void nuevoRegistroGeneraIdentificadoresDiferentes() {
        TipoCaracteristica primero = model.nuevoRegistro();
        TipoCaracteristica segundo = model.nuevoRegistro();
        assertNotNull(primero.getIdTipoCaracteristica());
        assertNotEquals(primero.getIdTipoCaracteristica(), segundo.getIdTipoCaracteristica());
        assertEquals(primero.getIdTipoCaracteristica(), model.getIdRegistro(primero));
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
        verify(tipoCaracteristicaDAO, never()).crear(any());
        verify(tipoCaracteristicaDAO, never()).modificar(any());
        assertTrue(model.isCreando());
        mensaje(FacesMessage.SEVERITY_WARN, "nombre");
    }

    @Test
    void nombreVacioImpideGuardar() {
        model.nuevo();
        model.getRegistro().setNombre("");
        model.guardar();
        verify(tipoCaracteristicaDAO, never()).crear(any());
        verify(tipoCaracteristicaDAO, never()).modificar(any());
        assertTrue(model.isCreando());
        mensaje(FacesMessage.SEVERITY_WARN, "nombre");
    }

    @Test
    void nombreEnBlancoImpideGuardar() {
        model.nuevo();
        model.getRegistro().setNombre("   ");
        model.guardar();
        verify(tipoCaracteristicaDAO, never()).crear(any());
        verify(tipoCaracteristicaDAO, never()).modificar(any());
        assertTrue(model.isCreando());
        mensaje(FacesMessage.SEVERITY_WARN, "nombre");
    }

    @Test
    void registroValidoSeGuardaYRegresaAlListado() {
        model.nuevo();
        model.getRegistro().setNombre("  Café  ");
        when(tipoCaracteristicaDAO.crear(model.getRegistro())).thenReturn(true);
        model.guardar();
        assertTrue(model.isListado());
        verify(tipoCaracteristicaDAO).crear(argThat(x -> x.getNombre().equals("  Café  ")));
        mensaje(FacesMessage.SEVERITY_INFO, "guardado");
    }

    @Test
    void desactivacionConAsociados() {
        TipoCaracteristica original = model.nuevoRegistro();
        original.setNombre("Café");
        TipoCaracteristica editado = model.nuevoRegistro();
        editado.setIdTipoCaracteristica(original.getIdTipoCaracteristica());
        editado.setNombre("Café");
        editado.setActivo(false);
        when(tipoCaracteristicaDAO.buscarPorId(original.getIdTipoCaracteristica())).thenReturn(editado, original);
        model.editar(original);
        when(caracteristicaDAO.contarPorTipo(original.getIdTipoCaracteristica())).thenReturn(2L);
        model.guardar();
        verify(tipoCaracteristicaDAO, never()).modificar(any());
        assertTrue(model.isEditando());
        mensaje(FacesMessage.SEVERITY_ERROR, "desactivar");
    }

    @Test
    void desactivacionSinAsociados() {
        TipoCaracteristica original = model.nuevoRegistro();
        original.setNombre("Café");
        TipoCaracteristica editado = model.nuevoRegistro();
        editado.setIdTipoCaracteristica(original.getIdTipoCaracteristica());
        editado.setNombre("Café");
        editado.setActivo(false);
        when(tipoCaracteristicaDAO.buscarPorId(original.getIdTipoCaracteristica())).thenReturn(editado, original);
        model.editar(original);
        when(caracteristicaDAO.contarPorTipo(original.getIdTipoCaracteristica())).thenReturn(0L);
        when(tipoCaracteristicaDAO.modificar(editado)).thenReturn(true);
        model.guardar();
        verify(tipoCaracteristicaDAO).modificar(editado);
        assertTrue(model.isListado());
    }

    @Test
    void nuevaExpresionAceptaCualquierValor() {
        assertEquals(".*", model.nuevoRegistro().getExpresionRegular());
    }

    @Test
    void expresionNulaImpideGuardar() {
        model.nuevo();
        model.getRegistro().setNombre("  Café  ");
        model.getRegistro().setExpresionRegular(null);
        model.guardar();
        verify(tipoCaracteristicaDAO, never()).crear(any());
        assertTrue(model.isCreando());
    }

    @Test
    void expresionVaciaImpideGuardar() {
        model.nuevo();
        model.getRegistro().setNombre("  Café  ");
        model.getRegistro().setExpresionRegular("");
        model.guardar();
        verify(tipoCaracteristicaDAO, never()).crear(any());
        assertTrue(model.isCreando());
    }

    @Test
    void expresionInvalidaImpideGuardar() {
        model.nuevo();
        model.getRegistro().setNombre("  Café  ");
        model.getRegistro().setExpresionRegular("[");
        model.guardar();
        verify(tipoCaracteristicaDAO, never()).crear(any());
        assertTrue(model.isCreando());
    }

    @Test
    void nuevoLimpiaSeleccion() {
        model.setSeleccionado(model.nuevoRegistro());
        model.nuevo();
        assertNull(model.getSeleccionado());
    }

    @Test
    void cancelarLimpiaSeleccion() {
        model.setSeleccionado(model.nuevoRegistro());
        model.cancelar();
        assertNull(model.getSeleccionado());
    }
}
