package sv.edu.ues.occ.ingenieria.ppi115_2026.cafefe.boundary.jsf;

import sv.edu.ues.occ.ingenieria.ppi115_2026.cafefe.boundary.jsf.CaracteristicaModel;
import sv.edu.ues.occ.ingenieria.ppi115_2026.cafefe.boundary.jsf.Estado;
import jakarta.faces.application.FacesMessage;
import jakarta.persistence.PersistenceException;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import sv.edu.ues.occ.ingenieria.ppi115_2026.cafefe.entity.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import sv.edu.ues.occ.ingenieria.ppi115_2026.cafefe.control.CaracteristicaDAO;
import sv.edu.ues.occ.ingenieria.ppi115_2026.cafefe.control.TipoCaracteristicaDAO;

/** Pruebas de comportamiento de CaracteristicaModel, con DAO y contexto JSF simulados. */
class CaracteristicaModelTest extends ModelTestSupport {

    @Mock CaracteristicaDAO caracteristicaDAO;
    @Mock TipoCaracteristicaDAO tipoCaracteristicaDAO;
    @InjectMocks CaracteristicaModel model;

    private TipoCaracteristica tipoValido() {
        TipoCaracteristica tipo = new TipoCaracteristica(UUID.randomUUID());
        tipo.setActivo(true);
        tipo.setNombre("Tamaño");
        tipo.setExpresionRegular(".*");
        return tipo;
    }

    private void prepararValido(TipoCaracteristica tipo) {
        model.nuevo();
        model.getRegistro().setNombre("Grande");
        model.setIdTipoSeleccionado(tipo.getIdTipoCaracteristica().toString());
        when(tipoCaracteristicaDAO.buscarPorId(tipo.getIdTipoCaracteristica())).thenReturn(tipo);
    }

    @Test
    void usaElDAOInyectado() {
        assertSame(caracteristicaDAO, model.getDAO());
    }

    @Test
    void nuevoRegistroGeneraIdentificadoresDiferentes() {
        Caracteristica primero = model.nuevoRegistro();
        Caracteristica segundo = model.nuevoRegistro();
        assertNotNull(primero.getIdCaracteristica());
        assertNotEquals(primero.getIdCaracteristica(), segundo.getIdCaracteristica());
        assertEquals(primero.getIdCaracteristica(), model.getIdRegistro(primero));
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
        verify(caracteristicaDAO, never()).crear(any());
        verify(caracteristicaDAO, never()).modificar(any());
        assertTrue(model.isCreando());
        mensaje(FacesMessage.SEVERITY_WARN, "nombre");
    }

    @Test
    void nombreVacioImpideGuardar() {
        model.nuevo();
        model.getRegistro().setNombre("");
        model.guardar();
        verify(caracteristicaDAO, never()).crear(any());
        verify(caracteristicaDAO, never()).modificar(any());
        assertTrue(model.isCreando());
        mensaje(FacesMessage.SEVERITY_WARN, "nombre");
    }

    @Test
    void nombreEnBlancoImpideGuardar() {
        model.nuevo();
        model.getRegistro().setNombre("   ");
        model.guardar();
        verify(caracteristicaDAO, never()).crear(any());
        verify(caracteristicaDAO, never()).modificar(any());
        assertTrue(model.isCreando());
        mensaje(FacesMessage.SEVERITY_WARN, "nombre");
    }

    @Test
    void guardarResuelveTipoYGuarda() {
        TipoCaracteristica tipo = tipoValido();
        prepararValido(tipo);
        Caracteristica c = model.getRegistro();
        when(caracteristicaDAO.crear(c)).thenReturn(true);
        model.guardar();
        assertSame(tipo, c.getIdTipoCaracteristica());
        assertTrue(model.isListado());
        assertNull(model.getIdTipoSeleccionado());
    }

    @Test
    void expresionNulaImpideGuardar() {
        TipoCaracteristica tipo = tipoValido();
        tipo.setExpresionRegular(null);
        prepararValido(tipo);
        model.guardar();
        verify(caracteristicaDAO, never()).crear(any());
        mensaje(FacesMessage.SEVERITY_ERROR, "expresión regular");
    }

    @Test
    void expresionVaciaImpideGuardar() {
        TipoCaracteristica tipo = tipoValido();
        tipo.setExpresionRegular("");
        prepararValido(tipo);
        model.guardar();
        verify(caracteristicaDAO, never()).crear(any());
        mensaje(FacesMessage.SEVERITY_ERROR, "expresión regular");
    }

    @Test
    void expresionInvalidaImpideGuardar() {
        TipoCaracteristica tipo = tipoValido();
        tipo.setExpresionRegular("[");
        prepararValido(tipo);
        model.guardar();
        verify(caracteristicaDAO, never()).crear(any());
        mensaje(FacesMessage.SEVERITY_ERROR, "expresión regular");
    }

    @Test
    void tipoInactivoNoPuedeAsignarse() {
        TipoCaracteristica tipo = tipoValido();
        tipo.setActivo(false);
        prepararValido(tipo);
        model.guardar();
        verify(caracteristicaDAO, never()).crear(any());
        mensaje(FacesMessage.SEVERITY_ERROR, "inactivo");
    }

    @Test
    void tipoInactivoOriginalPuedeConservarse() {
        TipoCaracteristica tipo = tipoValido();
        tipo.setActivo(false);
        Caracteristica c = model.nuevoRegistro();
        c.setNombre("Grande");
        c.setIdTipoCaracteristica(tipo);
        when(caracteristicaDAO.buscarPorId(c.getIdCaracteristica())).thenReturn(c);
        when(tipoCaracteristicaDAO.buscarPorId(tipo.getIdTipoCaracteristica())).thenReturn(tipo);
        model.editar(c);
        assertEquals(tipo.getIdTipoCaracteristica().toString(), model.getIdTipoSeleccionado());
        when(caracteristicaDAO.modificar(c)).thenReturn(true);
        model.guardar();
        verify(caracteristicaDAO).modificar(c);
        assertTrue(model.isListado());
    }

    @Test
    void tipoNoSeleccionadoImpideGuardar() {
        model.nuevo();
        model.getRegistro().setNombre("Grande");
        model.setIdTipoSeleccionado(null);
        model.guardar();
        verify(caracteristicaDAO, never()).crear(any());
        verify(tipoCaracteristicaDAO, never()).buscarPorId(any());
        mensaje(FacesMessage.SEVERITY_WARN, "tipo");
    }

    @Test
    void tipoVacioImpideGuardar() {
        model.nuevo();
        model.getRegistro().setNombre("Grande");
        model.setIdTipoSeleccionado("  ");
        model.guardar();
        verify(caracteristicaDAO, never()).crear(any());
        verify(tipoCaracteristicaDAO, never()).buscarPorId(any());
        mensaje(FacesMessage.SEVERITY_WARN, "tipo");
    }

    @Test
    void tipoInvalidoImpideGuardar() {
        model.nuevo();
        model.getRegistro().setNombre("Grande");
        model.setIdTipoSeleccionado("incorrecto");
        model.guardar();
        verify(caracteristicaDAO, never()).crear(any());
        verify(tipoCaracteristicaDAO, never()).buscarPorId(any());
        mensaje(FacesMessage.SEVERITY_WARN, "tipo");
    }

    @Test
    void nuevoCargaTiposYLimpiaSeleccion() {
        TipoCaracteristica tipo = tipoValido();
        when(tipoCaracteristicaDAO.findAll()).thenReturn(List.of(tipo));
        model.setSeleccionado(model.nuevoRegistro());
        model.nuevo();
        assertEquals(List.of(tipo), model.getTiposDisponibles());
        assertNull(model.getSeleccionado());
        assertNull(model.getIdTipoSeleccionado());
    }

    @Test
    void falloAlCargarTiposMuestraError() {
        when(tipoCaracteristicaDAO.findAll()).thenThrow(new PersistenceException("Fallo"));
        model.nuevo();
        assertTrue(model.isCreando());
        mensaje(FacesMessage.SEVERITY_ERROR, "cargar los tipos");
    }

    @Test
    void falloAlResolverTipoImpideGuardar() {
        TipoCaracteristica tipo = tipoValido();
        model.nuevo();
        model.getRegistro().setNombre("Grande");
        model.setIdTipoSeleccionado(tipo.getIdTipoCaracteristica().toString());
        when(tipoCaracteristicaDAO.buscarPorId(tipo.getIdTipoCaracteristica())).thenThrow(new PersistenceException("Fallo"));
        model.guardar();
        verify(caracteristicaDAO, never()).crear(any());
    }
}
