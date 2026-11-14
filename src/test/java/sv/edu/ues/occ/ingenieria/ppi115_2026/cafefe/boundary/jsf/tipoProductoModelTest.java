package sv.edu.ues.occ.ingenieria.ppi115_2026.cafefe.boundary.jsf;

import sv.edu.ues.occ.ingenieria.ppi115_2026.cafefe.boundary.jsf.Estado;
import sv.edu.ues.occ.ingenieria.ppi115_2026.cafefe.boundary.jsf.tipoProductoModel;
import jakarta.faces.application.FacesMessage;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.primefaces.model.LazyDataModel;
import sv.edu.ues.occ.ingenieria.ppi115_2026.cafefe.entity.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import sv.edu.ues.occ.ingenieria.ppi115_2026.cafefe.control.TipoProductoDAO;

/** Pruebas de comportamiento de tipoProductoModel, con DAO y contexto JSF simulados. */
class tipoProductoModelTest extends ModelTestSupport {

    @Mock TipoProductoDAO tipoProductoDAO;
    @InjectMocks tipoProductoModel model;


    @Test
    void usaElDAOInyectado() {
        assertSame(tipoProductoDAO, model.getDAO());
    }

    @Test
    void nuevoRegistroGeneraIdentificadoresDiferentes() {
        TipoProducto primero = model.nuevoRegistro();
        TipoProducto segundo = model.nuevoRegistro();
        assertNotNull(primero.getIdTipoProducto());
        assertNotEquals(primero.getIdTipoProducto(), segundo.getIdTipoProducto());
        assertEquals(primero.getIdTipoProducto(), model.getIdRegistro(primero));
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
        verify(tipoProductoDAO, never()).crear(any());
        verify(tipoProductoDAO, never()).modificar(any());
        assertTrue(model.isCreando());
        mensaje(FacesMessage.SEVERITY_WARN, "nombre");
    }

    @Test
    void nombreVacioImpideGuardar() {
        model.nuevo();
        model.getRegistro().setNombre("");
        model.guardar();
        verify(tipoProductoDAO, never()).crear(any());
        verify(tipoProductoDAO, never()).modificar(any());
        assertTrue(model.isCreando());
        mensaje(FacesMessage.SEVERITY_WARN, "nombre");
    }

    @Test
    void nombreEnBlancoImpideGuardar() {
        model.nuevo();
        model.getRegistro().setNombre("   ");
        model.guardar();
        verify(tipoProductoDAO, never()).crear(any());
        verify(tipoProductoDAO, never()).modificar(any());
        assertTrue(model.isCreando());
        mensaje(FacesMessage.SEVERITY_WARN, "nombre");
    }

    @Test
    void registroValidoSeGuardaYRegresaAlListado() {
        model.nuevo();
        model.getRegistro().setNombre("  Café  ");
        when(tipoProductoDAO.crear(model.getRegistro())).thenReturn(true);
        model.guardar();
        assertTrue(model.isListado());
        verify(tipoProductoDAO).crear(argThat(x -> x.getNombre().equals("Café")));
        mensaje(FacesMessage.SEVERITY_INFO, "guardado");
    }

    @Test
    void desactivacionConAsociados() {
        TipoProducto original = model.nuevoRegistro();
        original.setNombre("Café");
        TipoProducto editado = model.nuevoRegistro();
        editado.setIdTipoProducto(original.getIdTipoProducto());
        editado.setNombre("Café");
        editado.setActivo(false);
        when(tipoProductoDAO.buscarPorId(original.getIdTipoProducto())).thenReturn(editado, original);
        model.editar(original);
        when(tipoProductoDAO.contarProductos(original.getIdTipoProducto())).thenReturn(2L);
        model.guardar();
        verify(tipoProductoDAO, never()).modificar(any());
        assertTrue(model.isEditando());
        mensaje(FacesMessage.SEVERITY_ERROR, "desactivar");
    }

    @Test
    void desactivacionSinAsociados() {
        TipoProducto original = model.nuevoRegistro();
        original.setNombre("Café");
        TipoProducto editado = model.nuevoRegistro();
        editado.setIdTipoProducto(original.getIdTipoProducto());
        editado.setNombre("Café");
        editado.setActivo(false);
        when(tipoProductoDAO.buscarPorId(original.getIdTipoProducto())).thenReturn(editado, original);
        model.editar(original);
        when(tipoProductoDAO.contarProductos(original.getIdTipoProducto())).thenReturn(0L);
        when(tipoProductoDAO.modificar(editado)).thenReturn(true);
        model.guardar();
        verify(tipoProductoDAO).modificar(editado);
        assertTrue(model.isListado());
    }

    @Test
    void activoNuloImpideGuardar() {
        model.nuevo();
        model.getRegistro().setNombre("  Café  ");
        model.getRegistro().setActivo(null);
        model.guardar();
        verify(tipoProductoDAO, never()).crear(any());
        mensaje(FacesMessage.SEVERITY_WARN, "activo");
    }

    @Test
    void nombreLimite() {
        model.nuevo();
        model.getRegistro().setNombre("  Café  ");
        model.getRegistro().setNombre("a".repeat(155));
        assertTrue(model.validarRegistro());
    }

    @Test
    void nombreExcesivo() {
        model.nuevo();
        model.getRegistro().setNombre("  Café  ");
        model.getRegistro().setNombre("a".repeat(156));
        assertFalse(model.validarRegistro());
    }

    @Test
    void edicionDeRegistroEliminadoEsRechazada() {
        TipoProducto p = model.nuevoRegistro();
        p.setNombre("Café");
        when(tipoProductoDAO.buscarPorId(p.getIdTipoProducto())).thenReturn(p, null);
        model.editar(p);
        model.guardar();
        verify(tipoProductoDAO, never()).modificar(any());
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
        LazyDataModel<TipoProducto> lazy = model.getRegistrosLazy();
        assertNotNull(lazy);
        model.cargarRegistros();
        assertSame(lazy, model.getRegistrosLazy());
        verify(tipoProductoDAO, never()).findAll();
    }

    @Test
    void lazyConteoUsaDAO() {
        model.inicializar();
        when(tipoProductoDAO.count()).thenReturn(37);
        assertEquals(37, model.getRegistrosLazy().count(Map.of()));
    }

    @Test
    void lazyPaginaUsaInicioYLimite() {
        model.inicializar();
        model.getRegistrosLazy().setRowCount(50);
        List<TipoProducto> datos = List.of(model.nuevoRegistro());
        when(tipoProductoDAO.findRange(new int[]{10, 20})).thenReturn(datos);
        assertSame(datos, model.getRegistrosLazy().load(10, 10, Map.of(), Map.of()));
    }

    @Test
    void lazyReajustaPaginaTrasEliminarUltimosRegistros() {
        model.inicializar();
        model.getRegistrosLazy().setRowCount(15);
        when(tipoProductoDAO.findRange(new int[]{10, 20})).thenReturn(List.of());
        model.getRegistrosLazy().load(20, 10, Map.of(), Map.of());
        verify(tipoProductoDAO).findRange(new int[]{10, 20});
    }

    @Test
    void lazyRecuperaFilaPorUUID() {
        model.inicializar();
        TipoProducto p = model.nuevoRegistro();
        when(tipoProductoDAO.buscarPorId(p.getIdTipoProducto())).thenReturn(p);
        assertEquals(p.getIdTipoProducto().toString(), model.getRegistrosLazy().getRowKey(p));
        assertSame(p, model.getRegistrosLazy().getRowData(p.getIdTipoProducto().toString()));
    }

    @Test
    void lazyClaveInvalidaNoConsulta() {
        model.inicializar();
        assertNull(model.getRegistrosLazy().getRowData("incorrecto"));
        verify(tipoProductoDAO, never()).buscarPorId(any());
    }

    @Test
    void nuevoNoDescartaFormularioAbierto() {
        model.nuevo();
        TipoProducto p = model.getRegistro();
        model.nuevo();
        assertSame(p, model.getRegistro());
    }

    @Test
    void modificarSeleccionadoRecuperaRegistro() {
        TipoProducto fila = model.nuevoRegistro();
        TipoProducto recuperado = model.nuevoRegistro();
        when(tipoProductoDAO.buscarPorId(fila.getIdTipoProducto())).thenReturn(recuperado);
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
        verify(tipoProductoDAO, never()).buscarPorId(any());
        assertTrue(model.isCreando());
    }

    @Test
    void eliminarSeleccionadoSinSeleccionNoConsulta() {
        model.eliminarSeleccionado();
        model.setSeleccionado(new TipoProducto());
        model.eliminarSeleccionado();
        verify(tipoProductoDAO, never()).eliminar(any());
        mensaje(FacesMessage.SEVERITY_WARN, "Selecciona");
    }

    @Test
    void eliminarSeleccionadoConFormularioAbiertoNoConsulta() {
        model.nuevo();
        model.setSeleccionado(model.nuevoRegistro());
        model.eliminarSeleccionado();
        verify(tipoProductoDAO, never()).buscarPorId(any());
        verify(tipoProductoDAO, never()).eliminar(any());
        mensaje(FacesMessage.SEVERITY_WARN, "formulario");
    }

    @Test
    void eliminarSeleccionadoRecuperaEntidadActual() {
        TipoProducto fila = model.nuevoRegistro();
        TipoProducto actual = model.nuevoRegistro();
        model.setSeleccionado(fila);
        when(tipoProductoDAO.buscarPorId(fila.getIdTipoProducto())).thenReturn(actual);
        when(tipoProductoDAO.eliminar(actual)).thenReturn(true);
        model.eliminarSeleccionado();
        verify(tipoProductoDAO).eliminar(same(actual));
        assertNull(model.getSeleccionado());
        assertTrue(model.isListado());
    }

    @Test
    void eliminarSeleccionadoInexistenteNoElimina() {
        model.setSeleccionado(model.nuevoRegistro());
        model.eliminarSeleccionado();
        verify(tipoProductoDAO, never()).eliminar(any());
        mensaje(FacesMessage.SEVERITY_ERROR, "recuperar");
    }
}
