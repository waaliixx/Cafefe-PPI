package sv.edu.ues.occ.ingenieria.ppi115_2026.cafefe.boundary.jsf;

import sv.edu.ues.occ.ingenieria.ppi115_2026.cafefe.boundary.jsf.DescuentoModel;
import sv.edu.ues.occ.ingenieria.ppi115_2026.cafefe.boundary.jsf.Estado;
import jakarta.faces.application.FacesMessage;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.primefaces.model.LazyDataModel;
import sv.edu.ues.occ.ingenieria.ppi115_2026.cafefe.entity.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import sv.edu.ues.occ.ingenieria.ppi115_2026.cafefe.control.DescuentoDAO;
import sv.edu.ues.occ.ingenieria.ppi115_2026.cafefe.control.TipoDescuentoDAO;

/** Pruebas de comportamiento de DescuentoModel, con DAO y contexto JSF simulados. */
class DescuentoModelTest extends ModelTestSupport {

    @Mock DescuentoDAO descuentoDAO;
    @Mock TipoDescuentoDAO tipoDescuentoDAO;
    @InjectMocks DescuentoModel model;

    private TipoDescuento prepararValido() {
        model.nuevo();
        model.getRegistro().setNombre("  Promoción  ");
        model.getRegistro().setFechaDesde(fecha("2026-01-10"));
        TipoDescuento tipo = new TipoDescuento(UUID.randomUUID());
        model.getRegistro().setIdTipoDescuento(tipo.getIdTipoDescuento());
        when(tipoDescuentoDAO.buscarPorId(tipo.getIdTipoDescuento())).thenReturn(tipo);
        return tipo;
    }

    @Test
    void usaElDAOInyectado() {
        assertSame(descuentoDAO, model.getDAO());
    }

    @Test
    void nuevoRegistroGeneraIdentificadoresDiferentes() {
        Descuento primero = model.nuevoRegistro();
        Descuento segundo = model.nuevoRegistro();
        assertNotNull(primero.getIdDescuento());
        assertNotEquals(primero.getIdDescuento(), segundo.getIdDescuento());
        assertEquals(primero.getIdDescuento(), model.getIdRegistro(primero));
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
        verify(descuentoDAO, never()).crear(any());
        verify(descuentoDAO, never()).modificar(any());
        assertTrue(model.isCreando());
        mensaje(FacesMessage.SEVERITY_WARN, "nombre");
    }

    @Test
    void nombreVacioImpideGuardar() {
        model.nuevo();
        model.getRegistro().setNombre("");
        model.guardar();
        verify(descuentoDAO, never()).crear(any());
        verify(descuentoDAO, never()).modificar(any());
        assertTrue(model.isCreando());
        mensaje(FacesMessage.SEVERITY_WARN, "nombre");
    }

    @Test
    void nombreEnBlancoImpideGuardar() {
        model.nuevo();
        model.getRegistro().setNombre("   ");
        model.guardar();
        verify(descuentoDAO, never()).crear(any());
        verify(descuentoDAO, never()).modificar(any());
        assertTrue(model.isCreando());
        mensaje(FacesMessage.SEVERITY_WARN, "nombre");
    }

    @Test
    void inicializarCreaLazySinCargarTodaLaTabla() {
        model.inicializar();
        LazyDataModel<Descuento> lazy = model.getRegistrosLazy();
        assertNotNull(lazy);
        model.cargarRegistros();
        assertSame(lazy, model.getRegistrosLazy());
        verify(descuentoDAO, never()).findAll();
    }

    @Test
    void lazyConteoUsaDAO() {
        model.inicializar();
        when(descuentoDAO.count()).thenReturn(37);
        assertEquals(37, model.getRegistrosLazy().count(Map.of()));
    }

    @Test
    void lazyPaginaUsaInicioYLimite() {
        model.inicializar();
        model.getRegistrosLazy().setRowCount(50);
        List<Descuento> datos = List.of(model.nuevoRegistro());
        when(descuentoDAO.findRange(new int[]{10, 20})).thenReturn(datos);
        assertSame(datos, model.getRegistrosLazy().load(10, 10, Map.of(), Map.of()));
    }

    @Test
    void lazyReajustaPaginaTrasEliminarUltimosRegistros() {
        model.inicializar();
        model.getRegistrosLazy().setRowCount(15);
        when(descuentoDAO.findRange(new int[]{10, 20})).thenReturn(List.of());
        model.getRegistrosLazy().load(20, 10, Map.of(), Map.of());
        verify(descuentoDAO).findRange(new int[]{10, 20});
    }

    @Test
    void lazyRecuperaFilaPorUUID() {
        model.inicializar();
        Descuento p = model.nuevoRegistro();
        when(descuentoDAO.buscarPorId(p.getIdDescuento())).thenReturn(p);
        assertEquals(p.getIdDescuento().toString(), model.getRegistrosLazy().getRowKey(p));
        assertSame(p, model.getRegistrosLazy().getRowData(p.getIdDescuento().toString()));
    }

    @Test
    void lazyClaveInvalidaNoConsulta() {
        model.inicializar();
        assertNull(model.getRegistrosLazy().getRowData("incorrecto"));
        verify(descuentoDAO, never()).buscarPorId(any());
    }

    @Test
    void nuevoNoDescartaFormularioAbierto() {
        model.nuevo();
        Descuento p = model.getRegistro();
        model.nuevo();
        assertSame(p, model.getRegistro());
    }

    @Test
    void modificarSeleccionadoRecuperaRegistro() {
        Descuento fila = model.nuevoRegistro();
        Descuento recuperado = model.nuevoRegistro();
        when(descuentoDAO.buscarPorId(fila.getIdDescuento())).thenReturn(recuperado);
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
        verify(descuentoDAO, never()).buscarPorId(any());
        assertTrue(model.isCreando());
    }

    @Test
    void eliminarSeleccionadoSinSeleccionNoConsulta() {
        model.eliminarSeleccionado();
        model.setSeleccionado(new Descuento());
        model.eliminarSeleccionado();
        verify(descuentoDAO, never()).eliminar(any());
        mensaje(FacesMessage.SEVERITY_WARN, "Selecciona");
    }

    @Test
    void eliminarSeleccionadoConFormularioAbiertoNoConsulta() {
        model.nuevo();
        model.setSeleccionado(model.nuevoRegistro());
        model.eliminarSeleccionado();
        verify(descuentoDAO, never()).buscarPorId(any());
        verify(descuentoDAO, never()).eliminar(any());
        mensaje(FacesMessage.SEVERITY_WARN, "formulario");
    }

    @Test
    void eliminarSeleccionadoRecuperaEntidadActual() {
        Descuento fila = model.nuevoRegistro();
        Descuento actual = model.nuevoRegistro();
        model.setSeleccionado(fila);
        when(descuentoDAO.buscarPorId(fila.getIdDescuento())).thenReturn(actual);
        when(descuentoDAO.eliminar(actual)).thenReturn(true);
        model.eliminarSeleccionado();
        verify(descuentoDAO).eliminar(same(actual));
        assertNull(model.getSeleccionado());
        assertTrue(model.isListado());
    }

    @Test
    void eliminarSeleccionadoInexistenteNoElimina() {
        model.setSeleccionado(model.nuevoRegistro());
        model.eliminarSeleccionado();
        verify(descuentoDAO, never()).eliminar(any());
        mensaje(FacesMessage.SEVERITY_ERROR, "recuperar");
    }

    @Test
    void guardarNormalizaNombreYPermiteFinAbierto() {
        prepararValido();
        Descuento d = model.getRegistro();
        when(descuentoDAO.crear(d)).thenReturn(true);
        model.guardar();
        assertEquals("Promoción", d.getNombre());
        assertTrue(model.isListado());
        verify(descuentoDAO).crear(d);
    }

    @Test
    void fechaFinalAnterior() {
        prepararValido();
        model.getRegistro().setFechaHasta(fecha("2026-01-09"));
        assertFalse(model.validarRegistro());
    }

    @Test
    void fechaFinalMismoDia() {
        prepararValido();
        model.getRegistro().setFechaHasta(fecha("2026-01-10"));
        assertTrue(model.validarRegistro());
    }

    @Test
    void fechaFinalPosterior() {
        prepararValido();
        model.getRegistro().setFechaHasta(fecha("2026-01-11"));
        assertTrue(model.validarRegistro());
    }

    @Test
    void fechaInicioObligatoria() {
        prepararValido();
        model.getRegistro().setFechaDesde(null);
        assertFalse(model.validarRegistro());
        mensaje(FacesMessage.SEVERITY_WARN, "inicio");
    }

    @Test
    void tipoObligatorio() {
        model.nuevo();
        model.getRegistro().setNombre("Promoción");
        assertFalse(model.validarRegistro());
        verifyNoInteractions(tipoDescuentoDAO);
    }

    @Test
    void tipoInexistenteImpideGuardar() {
        model.nuevo();
        model.getRegistro().setNombre("Promoción");
        model.getRegistro().setIdTipoDescuento(UUID.randomUUID());
        assertFalse(model.validarRegistro());
    }

    @Test
    void nombreExcesivoImpideGuardar() {
        model.nuevo();
        model.getRegistro().setNombre("a".repeat(156));
        assertFalse(model.validarRegistro());
        verifyNoInteractions(tipoDescuentoDAO);
    }

    @Test
    void tiposSeCarganUnaSolaVez() {
        List<TipoDescuento> tipos = List.of(new TipoDescuento(UUID.randomUUID()));
        when(tipoDescuentoDAO.findAll()).thenReturn(tipos);
        assertSame(tipos, model.getTiposDescuento());
        assertSame(tipos, model.getTiposDescuento());
        verify(tipoDescuentoDAO, times(1)).findAll();
    }

    @Test
    void nombreTipoSeResuelveOSaleVacio() {
        TipoDescuento tipo = new TipoDescuento(UUID.randomUUID());
        tipo.setNombre("Promoción");
        when(tipoDescuentoDAO.findAll()).thenReturn(List.of(tipo));
        assertEquals("Promoción", model.nombreTipoDescuento(tipo.getIdTipoDescuento()));
        assertEquals("", model.nombreTipoDescuento(UUID.randomUUID()));
        assertEquals("", model.nombreTipoDescuento(null));
    }

    @Test
    void seleccionTipoConvierteUUIDYLimpiaBlancos() {
        assertNull(model.getIdTipoDescuentoSeleccionado());
        model.setIdTipoDescuentoSeleccionado("incorrecto");
        model.nuevo();
        UUID id = UUID.randomUUID();
        model.setIdTipoDescuentoSeleccionado(id.toString());
        assertEquals(id, model.getRegistro().getIdTipoDescuento());
        assertEquals(id.toString(), model.getIdTipoDescuentoSeleccionado());
        model.setIdTipoDescuentoSeleccionado("   ");
        assertNull(model.getRegistro().getIdTipoDescuento());
        model.setIdTipoDescuentoSeleccionado(null);
        assertNull(model.getIdTipoDescuentoSeleccionado());
    }

    @Test
    void seleccionTipoInvalidaLanzaExcepcion() {
        model.nuevo();
        assertThrows(IllegalArgumentException.class, () -> model.setIdTipoDescuentoSeleccionado("incorrecto"));
    }

    @Test
    void edicionDeRegistroEliminadoEsRechazada() {
        prepararValido();
        Descuento d = model.getRegistro();
        model.cancelar();
        when(descuentoDAO.buscarPorId(d.getIdDescuento())).thenReturn(d, null);
        model.editar(d);
        assertFalse(model.validarRegistro());
        mensaje(FacesMessage.SEVERITY_ERROR, "ya no existe");
    }
}
