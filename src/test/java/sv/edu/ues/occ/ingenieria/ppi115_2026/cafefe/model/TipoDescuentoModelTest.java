package sv.edu.ues.occ.ingenieria.ppi115_2026.cafefe.model;

import jakarta.faces.application.FacesMessage;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.primefaces.model.LazyDataModel;
import sv.edu.ues.occ.ingenieria.ppi115_2026.cafefe.dao.*;
import sv.edu.ues.occ.ingenieria.ppi115_2026.cafefe.entity.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/** Pruebas de comportamiento de TipoDescuentoModel, con DAO y contexto JSF simulados. */
class TipoDescuentoModelTest extends ModelTestSupport {

    @Mock TipoDescuentoDAO tipoDescuentoDAO;
    @Mock DescuentoDAO descuentoDAO;
    @InjectMocks TipoDescuentoModel model;


    @Test
    void usaElDAOInyectado() {
        assertSame(tipoDescuentoDAO, model.getDAO());
    }

    @Test
    void nuevoRegistroGeneraIdentificadoresDiferentes() {
        TipoDescuento primero = model.nuevoRegistro();
        TipoDescuento segundo = model.nuevoRegistro();
        assertNotNull(primero.getIdTipoDescuento());
        assertNotEquals(primero.getIdTipoDescuento(), segundo.getIdTipoDescuento());
        assertEquals(primero.getIdTipoDescuento(), model.getIdRegistro(primero));
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
        verify(tipoDescuentoDAO, never()).crear(any());
        verify(tipoDescuentoDAO, never()).modificar(any());
        assertTrue(model.isCreando());
        mensaje(FacesMessage.SEVERITY_WARN, "nombre");
    }

    @Test
    void nombreVacioImpideGuardar() {
        model.nuevo();
        model.getRegistro().setNombre("");
        model.guardar();
        verify(tipoDescuentoDAO, never()).crear(any());
        verify(tipoDescuentoDAO, never()).modificar(any());
        assertTrue(model.isCreando());
        mensaje(FacesMessage.SEVERITY_WARN, "nombre");
    }

    @Test
    void nombreEnBlancoImpideGuardar() {
        model.nuevo();
        model.getRegistro().setNombre("   ");
        model.guardar();
        verify(tipoDescuentoDAO, never()).crear(any());
        verify(tipoDescuentoDAO, never()).modificar(any());
        assertTrue(model.isCreando());
        mensaje(FacesMessage.SEVERITY_WARN, "nombre");
    }

    @Test
    void registroValidoSeGuardaYRegresaAlListado() {
        model.nuevo();
        model.getRegistro().setNombre("  Café  ");
        when(tipoDescuentoDAO.crear(model.getRegistro())).thenReturn(true);
        model.guardar();
        assertTrue(model.isListado());
        verify(tipoDescuentoDAO).crear(argThat(x -> x.getNombre().equals("Café")));
        mensaje(FacesMessage.SEVERITY_INFO, "guardado");
    }

    @Test
    void desactivacionConAsociados() {
        TipoDescuento original = model.nuevoRegistro();
        original.setNombre("Café");
        TipoDescuento editado = model.nuevoRegistro();
        editado.setIdTipoDescuento(original.getIdTipoDescuento());
        editado.setNombre("Café");
        editado.setActivo(false);
        when(tipoDescuentoDAO.buscarPorId(original.getIdTipoDescuento())).thenReturn(editado, original);
        model.editar(original);
        when(descuentoDAO.contarPorTipo(original.getIdTipoDescuento())).thenReturn(2L);
        model.guardar();
        verify(tipoDescuentoDAO, never()).modificar(any());
        assertTrue(model.isEditando());
        mensaje(FacesMessage.SEVERITY_ERROR, "desactivar");
    }

    @Test
    void desactivacionSinAsociados() {
        TipoDescuento original = model.nuevoRegistro();
        original.setNombre("Café");
        TipoDescuento editado = model.nuevoRegistro();
        editado.setIdTipoDescuento(original.getIdTipoDescuento());
        editado.setNombre("Café");
        editado.setActivo(false);
        when(tipoDescuentoDAO.buscarPorId(original.getIdTipoDescuento())).thenReturn(editado, original);
        model.editar(original);
        when(descuentoDAO.contarPorTipo(original.getIdTipoDescuento())).thenReturn(0L);
        when(tipoDescuentoDAO.modificar(editado)).thenReturn(true);
        model.guardar();
        verify(tipoDescuentoDAO).modificar(editado);
        assertTrue(model.isListado());
    }

    @Test
    void activoNuloImpideGuardar() {
        model.nuevo();
        model.getRegistro().setNombre("  Café  ");
        model.getRegistro().setActivo(null);
        model.guardar();
        verify(tipoDescuentoDAO, never()).crear(any());
        mensaje(FacesMessage.SEVERITY_WARN, "activo");
    }

    @Test
    void descuentoMaximoNegativoEsRechazado() {
        model.nuevo();
        model.getRegistro().setNombre("  Café  ");
        model.getRegistro().setDescuentoMaximo(-1);
        model.guardar();
        verify(tipoDescuentoDAO, never()).crear(any());
        mensaje(FacesMessage.SEVERITY_WARN, "negativo");
    }

    @Test
    void descuentoMaximoCeroEsValido() {
        model.nuevo();
        model.getRegistro().setNombre("  Café  ");
        model.getRegistro().setDescuentoMaximo(0);
        assertTrue(model.validarRegistro());
    }

    @Test
    void edicionDeRegistroEliminadoEsRechazada() {
        TipoDescuento p = model.nuevoRegistro();
        p.setNombre("Café");
        when(tipoDescuentoDAO.buscarPorId(p.getIdTipoDescuento())).thenReturn(p, null);
        model.editar(p);
        model.guardar();
        verify(tipoDescuentoDAO, never()).modificar(any());
        mensaje(FacesMessage.SEVERITY_ERROR, "ya no existe");
    }

    @Test
    void cancelarLimpiaSeleccion() {
        model.setSeleccionado(model.nuevoRegistro());
        model.cancelar();
        assertNull(model.getSeleccionado());
    }

    @Test
    void inicializarCreaLazySinCargarTodaLaTabla() {
        model.inicializar();
        LazyDataModel<TipoDescuento> lazy = model.getRegistrosLazy();
        assertNotNull(lazy);
        model.cargarRegistros();
        assertSame(lazy, model.getRegistrosLazy());
        verify(tipoDescuentoDAO, never()).findAll();
    }

    @Test
    void lazyConteoUsaDAO() {
        model.inicializar();
        when(tipoDescuentoDAO.count()).thenReturn(37);
        assertEquals(37, model.getRegistrosLazy().count(Map.of()));
    }

    @Test
    void lazyPaginaUsaInicioYLimite() {
        model.inicializar();
        model.getRegistrosLazy().setRowCount(50);
        List<TipoDescuento> datos = List.of(model.nuevoRegistro());
        when(tipoDescuentoDAO.findRange(new int[]{10, 20})).thenReturn(datos);
        assertSame(datos, model.getRegistrosLazy().load(10, 10, Map.of(), Map.of()));
    }

    @Test
    void lazyReajustaPaginaTrasEliminarUltimosRegistros() {
        model.inicializar();
        model.getRegistrosLazy().setRowCount(15);
        when(tipoDescuentoDAO.findRange(new int[]{10, 20})).thenReturn(List.of());
        model.getRegistrosLazy().load(20, 10, Map.of(), Map.of());
        verify(tipoDescuentoDAO).findRange(new int[]{10, 20});
    }

    @Test
    void lazyRecuperaFilaPorUUID() {
        model.inicializar();
        TipoDescuento p = model.nuevoRegistro();
        when(tipoDescuentoDAO.buscarPorId(p.getIdTipoDescuento())).thenReturn(p);
        assertEquals(p.getIdTipoDescuento().toString(), model.getRegistrosLazy().getRowKey(p));
        assertSame(p, model.getRegistrosLazy().getRowData(p.getIdTipoDescuento().toString()));
    }

    @Test
    void lazyClaveInvalidaNoConsulta() {
        model.inicializar();
        assertNull(model.getRegistrosLazy().getRowData("incorrecto"));
        verify(tipoDescuentoDAO, never()).buscarPorId(any());
    }

    @Test
    void nuevoNoDescartaFormularioAbierto() {
        model.nuevo();
        TipoDescuento p = model.getRegistro();
        model.nuevo();
        assertSame(p, model.getRegistro());
    }

    @Test
    void modificarSeleccionadoRecuperaRegistro() {
        TipoDescuento fila = model.nuevoRegistro();
        TipoDescuento recuperado = model.nuevoRegistro();
        when(tipoDescuentoDAO.buscarPorId(fila.getIdTipoDescuento())).thenReturn(recuperado);
        model.setSeleccionado(fila);
        model.modificarSeleccionado();
        assertSame(recuperado, model.getRegistro());
        assertTrue(model.isEditando());
    }

    @Test
    void modificarSeleccionadoConFormularioAbiertoNoConsulta() {
        model.nuevo();
        model.setSeleccionado(model.nuevoRegistro());
        model.modificarSeleccionado();
        verify(tipoDescuentoDAO, never()).buscarPorId(any());
        assertTrue(model.isCreando());
    }

    @Test
    void eliminarSeleccionadoSinSeleccionNoConsulta() {
        model.eliminarSeleccionado();
        model.setSeleccionado(new TipoDescuento());
        model.eliminarSeleccionado();
        verify(tipoDescuentoDAO, never()).eliminar(any());
        mensaje(FacesMessage.SEVERITY_WARN, "Selecciona");
    }

    @Test
    void eliminarSeleccionadoConFormularioAbiertoNoConsulta() {
        model.nuevo();
        model.setSeleccionado(model.nuevoRegistro());
        model.eliminarSeleccionado();
        verify(tipoDescuentoDAO, never()).buscarPorId(any());
        verify(tipoDescuentoDAO, never()).eliminar(any());
        mensaje(FacesMessage.SEVERITY_WARN, "formulario");
    }

    @Test
    void eliminarSeleccionadoRecuperaEntidadActual() {
        TipoDescuento fila = model.nuevoRegistro();
        TipoDescuento actual = model.nuevoRegistro();
        model.setSeleccionado(fila);
        when(tipoDescuentoDAO.buscarPorId(fila.getIdTipoDescuento())).thenReturn(actual);
        when(tipoDescuentoDAO.eliminar(actual)).thenReturn(true);
        model.eliminarSeleccionado();
        verify(tipoDescuentoDAO).eliminar(same(actual));
        assertNull(model.getSeleccionado());
        assertTrue(model.isListado());
    }

    @Test
    void eliminarSeleccionadoInexistenteNoElimina() {
        model.setSeleccionado(model.nuevoRegistro());
        model.eliminarSeleccionado();
        verify(tipoDescuentoDAO, never()).eliminar(any());
        mensaje(FacesMessage.SEVERITY_ERROR, "recuperar");
    }
}
