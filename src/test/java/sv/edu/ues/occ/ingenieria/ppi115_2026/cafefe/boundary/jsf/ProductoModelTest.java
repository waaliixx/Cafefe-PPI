package sv.edu.ues.occ.ingenieria.ppi115_2026.cafefe.boundary.jsf;

import sv.edu.ues.occ.ingenieria.ppi115_2026.cafefe.boundary.jsf.ProductoModel;
import sv.edu.ues.occ.ingenieria.ppi115_2026.cafefe.boundary.jsf.Estado;
import jakarta.faces.application.FacesMessage;
import jakarta.persistence.PersistenceException;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.InOrder;
import org.mockito.Mock;
import sv.edu.ues.occ.ingenieria.ppi115_2026.cafefe.entity.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import sv.edu.ues.occ.ingenieria.ppi115_2026.cafefe.control.CaracteristicaDAO;
import sv.edu.ues.occ.ingenieria.ppi115_2026.cafefe.control.DescuentoDAO;
import sv.edu.ues.occ.ingenieria.ppi115_2026.cafefe.control.DescuentoProductoDAO;
import sv.edu.ues.occ.ingenieria.ppi115_2026.cafefe.control.ProductoCaracteristicaDAO;
import sv.edu.ues.occ.ingenieria.ppi115_2026.cafefe.control.ProductoDAO;
import sv.edu.ues.occ.ingenieria.ppi115_2026.cafefe.control.ProductoTipoProductoDAO;
import sv.edu.ues.occ.ingenieria.ppi115_2026.cafefe.control.TipoCaracteristicaDAO;
import sv.edu.ues.occ.ingenieria.ppi115_2026.cafefe.control.TipoDescuentoDAO;
import sv.edu.ues.occ.ingenieria.ppi115_2026.cafefe.control.TipoProductoDAO;

/** Pruebas de comportamiento de ProductoModel, con DAO y contexto JSF simulados. */
class ProductoModelTest extends ModelTestSupport {

    @Mock ProductoDAO productoDAO;
    @Mock ProductoTipoProductoDAO productoTipoProductoDAO;
    @Mock ProductoCaracteristicaDAO productoCaracteristicaDAO;
    @Mock DescuentoProductoDAO descuentoProductoDAO;
    @Mock TipoProductoDAO tipoProductoDAO;
    @Mock TipoCaracteristicaDAO tipoCaracteristicaDAO;
    @Mock CaracteristicaDAO caracteristicaDAO;
    @Mock TipoDescuentoDAO tipoDescuentoDAO;
    @Mock DescuentoDAO descuentoDAO;
    @InjectMocks ProductoModel model;

    private Producto editarProducto() {
        Producto p = model.nuevoRegistro();
        p.setNombre("Café");
        p.setPrecioSugerido(new BigDecimal("2.50"));
        when(productoDAO.buscarPorId(p.getIdProducto())).thenReturn(p);
        model.editar(p);
        return p;
    }

    private TipoProducto prepararTipo() {
        model.abrirFormTipo();
        TipoProducto t = new TipoProducto(UUID.randomUUID());
        t.setNombre("Bebida");
        t.setActivo(true);
        model.setIdTipoProductoElegido(t.getIdTipoProducto().toString());
        when(tipoProductoDAO.buscarPorId(t.getIdTipoProducto())).thenReturn(t);
        return t;
    }

    private TipoCaracteristica prepararCaracteristica() {
        model.abrirFormCaracteristica();
        TipoCaracteristica t = new TipoCaracteristica(UUID.randomUUID());
        t.setNombre("Cantidad");
        t.setActivo(true);
        t.setExpresionRegular("[0-9]+");
        Caracteristica c = new Caracteristica(UUID.randomUUID());
        c.setNombre("Mililitros");
        c.setActivo(true);
        c.setIdTipoCaracteristica(t);
        model.setIdTipoCaracteristicaElegido(t.getIdTipoCaracteristica().toString());
        model.setIdCaracteristicaElegida(c.getIdCaracteristica().toString());
        model.getNuevaCaracteristica().setValor("  250  ");
        when(tipoCaracteristicaDAO.buscarPorId(t.getIdTipoCaracteristica())).thenReturn(t);
        when(caracteristicaDAO.buscarPorId(c.getIdCaracteristica())).thenReturn(c);
        return t;
    }

    private Descuento prepararDescuento() {
        model.abrirFormDescuento();
        TipoDescuento t = new TipoDescuento(UUID.randomUUID());
        t.setNombre("Promoción");
        t.setActivo(true);
        t.setDescuentoMaximo(30);
        Descuento d = new Descuento(UUID.randomUUID());
        d.setNombre("Descuento");
        d.setIdTipoDescuento(t.getIdTipoDescuento());
        d.setFechaDesde(fecha("2026-01-01"));
        d.setFechaHasta(fecha("2026-01-31"));
        model.setIdTipoDescuentoElegido(t.getIdTipoDescuento().toString());
        model.setIdDescuentoElegido(d.getIdDescuento().toString());
        model.getNuevoDescuento().setValor(15);
        model.getNuevoDescuento().setFechaDesde(fecha("2026-01-10"));
        model.getNuevoDescuento().setFechaHasta(fecha("2026-01-20"));
        when(tipoDescuentoDAO.buscarPorId(t.getIdTipoDescuento())).thenReturn(t);
        when(descuentoDAO.buscarPorId(d.getIdDescuento())).thenReturn(d);
        return d;
    }

    @Test
    void usaElDAOInyectado() {
        assertSame(productoDAO, model.getDAO());
    }

    @Test
    void nuevoRegistroGeneraIdentificadoresDiferentes() {
        Producto primero = model.nuevoRegistro();
        Producto segundo = model.nuevoRegistro();
        assertNotNull(primero.getIdProducto());
        assertNotEquals(primero.getIdProducto(), segundo.getIdProducto());
        assertEquals(primero.getIdProducto(), model.getIdRegistro(primero));
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
        verify(productoDAO, never()).crear(any());
        verify(productoDAO, never()).modificar(any());
        assertTrue(model.isCreando());
        mensaje(FacesMessage.SEVERITY_WARN, "nombre");
    }

    @Test
    void nombreVacioImpideGuardar() {
        model.nuevo();
        model.getRegistro().setNombre("");
        model.guardar();
        verify(productoDAO, never()).crear(any());
        verify(productoDAO, never()).modificar(any());
        assertTrue(model.isCreando());
        mensaje(FacesMessage.SEVERITY_WARN, "nombre");
    }

    @Test
    void nombreEnBlancoImpideGuardar() {
        model.nuevo();
        model.getRegistro().setNombre("   ");
        model.guardar();
        verify(productoDAO, never()).crear(any());
        verify(productoDAO, never()).modificar(any());
        assertTrue(model.isCreando());
        mensaje(FacesMessage.SEVERITY_WARN, "nombre");
    }

    @Test
    void precioNulo() {
        model.nuevo();
        model.getRegistro().setNombre("  Café  ");
        model.getRegistro().setPrecioSugerido(null);
        assertFalse(model.validarRegistro());
    }

    @Test
    void precioNegativo() {
        model.nuevo();
        model.getRegistro().setNombre("  Café  ");
        model.getRegistro().setPrecioSugerido(new BigDecimal("-0.01"));
        assertFalse(model.validarRegistro());
    }

    @Test
    void precioCero() {
        model.nuevo();
        model.getRegistro().setNombre("  Café  ");
        model.getRegistro().setPrecioSugerido(BigDecimal.ZERO);
        assertTrue(model.validarRegistro());
    }

    @Test
    void precioLimite() {
        model.nuevo();
        model.getRegistro().setNombre("  Café  ");
        model.getRegistro().setPrecioSugerido(new BigDecimal("999999.99"));
        assertTrue(model.validarRegistro());
    }

    @Test
    void precioExcesivo() {
        model.nuevo();
        model.getRegistro().setNombre("  Café  ");
        model.getRegistro().setPrecioSugerido(new BigDecimal("1000000.00"));
        assertFalse(model.validarRegistro());
    }

    @Test
    void nombreLimite() {
        model.nuevo();
        model.getRegistro().setNombre("a".repeat(155));
        model.getRegistro().setPrecioSugerido(BigDecimal.ZERO);
        assertTrue(model.validarRegistro());
    }

    @Test
    void nombreExcesivo() {
        model.nuevo();
        model.getRegistro().setNombre("a".repeat(156));
        model.getRegistro().setPrecioSugerido(BigDecimal.ZERO);
        assertFalse(model.validarRegistro());
    }

    @Test
    void guardarNormalizaNombre() {
        model.nuevo();
        Producto p = model.getRegistro();
        p.setNombre("  Café  ");
        p.setPrecioSugerido(BigDecimal.ZERO);
        when(productoDAO.crear(p)).thenReturn(true);
        model.guardar();
        assertEquals("Café", p.getNombre());
        assertTrue(model.isListado());
    }

    @Test
    void sugerenciasFiltranPorPrefijo() {
        assertEquals(4, model.sugerirPrecios(null).size());
        assertEquals(4, model.sugerirPrecios("  ").size());
        assertEquals(List.of(new BigDecimal("2.50")), model.sugerirPrecios(" 2 "));
        assertTrue(model.sugerirPrecios("100").isEmpty());
    }

    @Test
    void editarCargaDetallesYNombresDeTipos() {
        Producto p = model.nuevoRegistro();
        ProductoTipoProducto pt = new ProductoTipoProducto(UUID.randomUUID());
        ProductoCaracteristica pc = new ProductoCaracteristica(UUID.randomUUID());
        TipoDescuento td = new TipoDescuento(UUID.randomUUID());
        td.setNombre("Promoción");
        Descuento d = new Descuento(UUID.randomUUID());
        d.setIdTipoDescuento(td.getIdTipoDescuento());
        DescuentoProducto dp = new DescuentoProducto(UUID.randomUUID());
        dp.setIdDescuento(d);
        when(productoDAO.buscarPorId(p.getIdProducto())).thenReturn(p);
        when(productoTipoProductoDAO.findByProducto(p.getIdProducto())).thenReturn(List.of(pt));
        when(productoCaracteristicaDAO.findByProducto(p.getIdProducto())).thenReturn(List.of(pc));
        when(descuentoProductoDAO.findByProducto(p.getIdProducto())).thenReturn(List.of(dp));
        when(tipoDescuentoDAO.findAll()).thenReturn(List.of(td));
        model.editar(p);
        assertEquals(List.of(pt), model.getTiposAsignados());
        assertEquals(List.of(pc), model.getCaracteristicasAsignadas());
        assertEquals(List.of(dp), model.getDescuentosAsignados());
        assertEquals("Promoción", model.getNombreTipoDescuento(dp));
        assertEquals("", model.getNombreTipoDescuento(null));
    }

    @Test
    void detallesRequierenProductoGuardado() {
        model.nuevo();
        model.abrirFormTipo();
        model.abrirFormCaracteristica();
        model.abrirFormDescuento();
        assertFalse(model.isSubFormularioAbierto());
        verifyNoInteractions(tipoProductoDAO, tipoCaracteristicaDAO, tipoDescuentoDAO);
        mensaje(FacesMessage.SEVERITY_WARN, "Guarde primero");
    }

    @Test
    void abrirYCerrarFormularioTipo() {
        editarProducto();
        model.abrirFormTipo();
        assertTrue(model.isFormTipo());
        assertTrue(model.isSubFormularioAbierto());
        assertNotNull(model.getNuevoTipo());
        model.cerrarFormTipo();
        assertNull(model.getNuevoTipo());
        assertFalse(model.isSubFormularioAbierto());
    }

    @Test
    void errorAlCargarCatalogoTipo() {
        editarProducto();
        when(tipoProductoDAO.findAll()).thenThrow(new PersistenceException("Fallo"));
        model.abrirFormTipo();
        assertFalse(model.isFormTipo());
        mensaje(FacesMessage.SEVERITY_ERROR, "cargar");
    }

    @Test
    void abrirYCerrarFormularioCaracteristica() {
        editarProducto();
        model.abrirFormCaracteristica();
        assertTrue(model.isFormCaracteristica());
        assertTrue(model.isSubFormularioAbierto());
        assertNotNull(model.getNuevaCaracteristica());
        model.cerrarFormCaracteristica();
        assertNull(model.getNuevaCaracteristica());
        assertFalse(model.isSubFormularioAbierto());
    }

    @Test
    void errorAlCargarCatalogoCaracteristica() {
        editarProducto();
        when(tipoCaracteristicaDAO.findAll()).thenThrow(new PersistenceException("Fallo"));
        model.abrirFormCaracteristica();
        assertFalse(model.isFormCaracteristica());
        mensaje(FacesMessage.SEVERITY_ERROR, "cargar");
    }

    @Test
    void abrirYCerrarFormularioDescuento() {
        editarProducto();
        model.abrirFormDescuento();
        assertTrue(model.isFormDescuento());
        assertTrue(model.isSubFormularioAbierto());
        assertNotNull(model.getNuevoDescuento());
        model.cerrarFormDescuento();
        assertNull(model.getNuevoDescuento());
        assertFalse(model.isSubFormularioAbierto());
    }

    @Test
    void errorAlCargarCatalogoDescuento() {
        editarProducto();
        when(tipoDescuentoDAO.findAll()).thenThrow(new PersistenceException("Fallo"));
        model.abrirFormDescuento();
        assertFalse(model.isFormDescuento());
        mensaje(FacesMessage.SEVERITY_ERROR, "cargar");
    }

    @Test
    void cancelarLimpiaTodosLosDetalles() {
        editarProducto();
        model.abrirFormTipo();
        model.abrirFormCaracteristica();
        model.abrirFormDescuento();
        model.cancelar();
        assertFalse(model.isSubFormularioAbierto());
        assertNull(model.getNuevoTipo());
        assertNull(model.getNuevaCaracteristica());
        assertNull(model.getNuevoDescuento());
        assertTrue(model.getTiposAsignados().isEmpty());
        assertTrue(model.getCaracteristicasAsignadas().isEmpty());
        assertTrue(model.getDescuentosAsignados().isEmpty());
    }

    @Test
    void guardarTipoAsignaProductoTipoYFecha() {
        Producto p = editarProducto();
        TipoProducto t = prepararTipo();
        ProductoTipoProducto nuevo = model.getNuevoTipo();
        when(productoTipoProductoDAO.crear(nuevo)).thenReturn(true);
        model.guardarTipo();
        assertSame(p, nuevo.getIdProducto());
        assertSame(t, nuevo.getIdTipoProducto());
        assertNotNull(nuevo.getFechaCreacion());
        assertFalse(model.isFormTipo());
        verify(productoTipoProductoDAO, times(2)).findByProducto(p.getIdProducto());
    }

    @Test
    void tipoInactivoNoSeAsigna() {
        editarProducto();
        TipoProducto t = prepararTipo();
        t.setActivo(false);
        model.guardarTipo();
        verify(productoTipoProductoDAO, never()).crear(any());
        mensaje(FacesMessage.SEVERITY_ERROR, "inactivo");
    }

    @Test
    void tipoDuplicadoNoSeAsigna() {
        editarProducto();
        TipoProducto t = prepararTipo();
        ProductoTipoProducto existente = new ProductoTipoProducto(UUID.randomUUID());
        existente.setIdTipoProducto(t);
        model.getTiposAsignados().add(existente);
        model.guardarTipo();
        verify(productoTipoProductoDAO, never()).crear(any());
        mensaje(FacesMessage.SEVERITY_WARN, "ya tiene");
    }

    @Test
    void tipoUUIDInvalidoNoConsulta() {
        editarProducto();
        model.abrirFormTipo();
        model.setIdTipoProductoElegido("incorrecto");
        model.guardarTipo();
        verify(tipoProductoDAO, never()).buscarPorId(any());
        verify(productoTipoProductoDAO, never()).crear(any());
    }

    @Test
    void guardarTipoFallidoConservaFormulario() {
        editarProducto();
        prepararTipo();
        ProductoTipoProducto nuevo = model.getNuevoTipo();
        model.guardarTipo();
        assertTrue(model.isFormTipo());
        assertSame(nuevo, model.getNuevoTipo());
        mensaje(FacesMessage.SEVERITY_ERROR, "guardar");
    }

    @Test
    void guardarCaracteristicaValidaNormalizaValor() {
        Producto p = editarProducto();
        TipoCaracteristica t = prepararCaracteristica();
        ProductoCaracteristica nuevo = model.getNuevaCaracteristica();
        when(productoCaracteristicaDAO.crear(nuevo)).thenReturn(true);
        model.guardarCaracteristica();
        assertSame(p, nuevo.getIdProducto());
        assertEquals(t, nuevo.getIdCaracteristica().getIdTipoCaracteristica());
        assertEquals("250", nuevo.getValor());
        assertFalse(model.isFormCaracteristica());
    }

    @Test
    void valorCaracteristicaVacioEsRechazado() {
        editarProducto();
        prepararCaracteristica();
        model.getNuevaCaracteristica().setValor("  ");
        model.guardarCaracteristica();
        verify(productoCaracteristicaDAO, never()).crear(any());
        assertTrue(model.isFormCaracteristica());
    }

    @Test
    void valorCaracteristicaFueraDeFormatoEsRechazado() {
        editarProducto();
        prepararCaracteristica();
        model.getNuevaCaracteristica().setValor("abc");
        model.guardarCaracteristica();
        verify(productoCaracteristicaDAO, never()).crear(any());
        assertTrue(model.isFormCaracteristica());
    }

    @Test
    void expresionDeCaracteristicaNulaEsRechazada() {
        editarProducto();
        TipoCaracteristica t = prepararCaracteristica();
        t.setExpresionRegular(null);
        model.guardarCaracteristica();
        verify(productoCaracteristicaDAO, never()).crear(any());
        mensaje(FacesMessage.SEVERITY_ERROR, "formato");
    }

    @Test
    void expresionDeCaracteristicaVaciaEsRechazada() {
        editarProducto();
        TipoCaracteristica t = prepararCaracteristica();
        t.setExpresionRegular("");
        model.guardarCaracteristica();
        verify(productoCaracteristicaDAO, never()).crear(any());
        mensaje(FacesMessage.SEVERITY_ERROR, "formato");
    }

    @Test
    void expresionDeCaracteristicaInvalidaEsRechazada() {
        editarProducto();
        TipoCaracteristica t = prepararCaracteristica();
        t.setExpresionRegular("[");
        model.guardarCaracteristica();
        verify(productoCaracteristicaDAO, never()).crear(any());
        mensaje(FacesMessage.SEVERITY_ERROR, "expresión regular");
    }

    @Test
    void caracteristicaDeOtroTipoEsRechazada() {
        editarProducto();
        prepararCaracteristica();
        UUID id = UUID.fromString(model.getIdCaracteristicaElegida());
        Caracteristica c = caracteristicaDAO.buscarPorId(id);
        c.setIdTipoCaracteristica(new TipoCaracteristica(UUID.randomUUID()));
        model.guardarCaracteristica();
        verify(productoCaracteristicaDAO, never()).crear(any());
        mensaje(FacesMessage.SEVERITY_ERROR, "no pertenece");
    }

    @Test
    void caracteristicaInactivaEsRechazada() {
        editarProducto();
        prepararCaracteristica();
        Caracteristica c = caracteristicaDAO.buscarPorId(UUID.fromString(model.getIdCaracteristicaElegida()));
        c.setActivo(false);
        model.guardarCaracteristica();
        verify(productoCaracteristicaDAO, never()).crear(any());
        mensaje(FacesMessage.SEVERITY_ERROR, "inactiva");
    }

    @Test
    void caracteristicaDuplicadaEsRechazada() {
        editarProducto();
        prepararCaracteristica();
        ProductoCaracteristica existente = new ProductoCaracteristica(UUID.randomUUID());
        existente.setIdCaracteristica(caracteristicaDAO.buscarPorId(UUID.fromString(model.getIdCaracteristicaElegida())));
        model.getCaracteristicasAsignadas().add(existente);
        model.guardarCaracteristica();
        verify(productoCaracteristicaDAO, never()).crear(any());
        mensaje(FacesMessage.SEVERITY_WARN, "ya tiene");
    }

    @Test
    void cambiarTipoCaracteristicaCargaOpcionesYLimpiaSeleccion() {
        editarProducto();
        model.abrirFormCaracteristica();
        TipoCaracteristica t = new TipoCaracteristica(UUID.randomUUID());
        Caracteristica c = new Caracteristica(UUID.randomUUID());
        model.setIdTipoCaracteristicaElegido(t.getIdTipoCaracteristica().toString());
        model.setIdCaracteristicaElegida(c.getIdCaracteristica().toString());
        when(tipoCaracteristicaDAO.buscarPorId(t.getIdTipoCaracteristica())).thenReturn(t);
        when(caracteristicaDAO.findByTipoCaracteristica(t)).thenReturn(List.of(c));
        model.alCambiarTipoCaracteristica();
        assertNull(model.getIdCaracteristicaElegida());
        assertSame(t, model.getTipoCaracteristicaElegido());
        assertEquals(List.of(c), model.getCaracteristicasDelTipo());
    }

    @Test
    void guardarDescuentoAsignaRelaciones() {
        Producto p = editarProducto();
        Descuento d = prepararDescuento();
        DescuentoProducto nuevo = model.getNuevoDescuento();
        when(descuentoProductoDAO.crear(nuevo)).thenReturn(true);
        model.guardarDescuento();
        assertSame(p, nuevo.getIdProducto());
        assertSame(d, nuevo.getIdDescuento());
        assertFalse(model.isFormDescuento());
    }

    @Test
    void porcentajeDeDescuentoNuloEsRechazado() {
        editarProducto();
        prepararDescuento();
        model.getNuevoDescuento().setValor(null);
        model.guardarDescuento();
        verify(descuentoProductoDAO, never()).crear(any());
        assertTrue(model.isFormDescuento());
    }

    @Test
    void porcentajeDeDescuentoCeroEsRechazado() {
        editarProducto();
        prepararDescuento();
        model.getNuevoDescuento().setValor(0);
        model.guardarDescuento();
        verify(descuentoProductoDAO, never()).crear(any());
        assertTrue(model.isFormDescuento());
    }

    @Test
    void porcentajeDeDescuentoNegativoEsRechazado() {
        editarProducto();
        prepararDescuento();
        model.getNuevoDescuento().setValor(-1);
        model.guardarDescuento();
        verify(descuentoProductoDAO, never()).crear(any());
        assertTrue(model.isFormDescuento());
    }

    @Test
    void porcentajeDeDescuentoSuperaMaximoEsRechazado() {
        editarProducto();
        prepararDescuento();
        model.getNuevoDescuento().setValor(31);
        model.guardarDescuento();
        verify(descuentoProductoDAO, never()).crear(any());
        assertTrue(model.isFormDescuento());
    }

    @Test
    void descuentoInicioNuloEsRechazado() {
        editarProducto();
        prepararDescuento();
        model.getNuevoDescuento().setFechaDesde(null);
        model.getNuevoDescuento().setFechaHasta(fecha("2026-01-20"));
        model.guardarDescuento();
        verify(descuentoProductoDAO, never()).crear(any());
        assertTrue(model.isFormDescuento());
    }

    @Test
    void descuentoInicioAnteriorEsRechazado() {
        editarProducto();
        prepararDescuento();
        model.getNuevoDescuento().setFechaDesde(fecha("2025-12-31"));
        model.getNuevoDescuento().setFechaHasta(fecha("2026-01-20"));
        model.guardarDescuento();
        verify(descuentoProductoDAO, never()).crear(any());
        assertTrue(model.isFormDescuento());
    }

    @Test
    void descuentoInicioPosteriorEsRechazado() {
        editarProducto();
        prepararDescuento();
        model.getNuevoDescuento().setFechaDesde(fecha("2026-02-01"));
        model.getNuevoDescuento().setFechaHasta(fecha("2026-02-02"));
        model.guardarDescuento();
        verify(descuentoProductoDAO, never()).crear(any());
        assertTrue(model.isFormDescuento());
    }

    @Test
    void descuentoFinObligatorioEsRechazado() {
        editarProducto();
        prepararDescuento();
        model.getNuevoDescuento().setFechaDesde(fecha("2026-01-10"));
        model.getNuevoDescuento().setFechaHasta(null);
        model.guardarDescuento();
        verify(descuentoProductoDAO, never()).crear(any());
        assertTrue(model.isFormDescuento());
    }

    @Test
    void descuentoFinAntesDelInicioEsRechazado() {
        editarProducto();
        prepararDescuento();
        model.getNuevoDescuento().setFechaDesde(fecha("2026-01-10"));
        model.getNuevoDescuento().setFechaHasta(fecha("2026-01-09"));
        model.guardarDescuento();
        verify(descuentoProductoDAO, never()).crear(any());
        assertTrue(model.isFormDescuento());
    }

    @Test
    void descuentoFinFueraDeVigenciaEsRechazado() {
        editarProducto();
        prepararDescuento();
        model.getNuevoDescuento().setFechaDesde(fecha("2026-01-10"));
        model.getNuevoDescuento().setFechaHasta(fecha("2026-02-01"));
        model.guardarDescuento();
        verify(descuentoProductoDAO, never()).crear(any());
        assertTrue(model.isFormDescuento());
    }

    @Test
    void fechasLimiteDelDescuentoSonInclusivas() {
        editarProducto();
        Descuento d = prepararDescuento();
        DescuentoProducto nuevo = model.getNuevoDescuento();
        nuevo.setFechaDesde(d.getFechaDesde());
        nuevo.setFechaHasta(d.getFechaHasta());
        when(descuentoProductoDAO.crear(nuevo)).thenReturn(true);
        model.guardarDescuento();
        verify(descuentoProductoDAO).crear(nuevo);
    }

    @Test
    void descuentoDeOtroTipoEsRechazado() {
        editarProducto();
        Descuento d = prepararDescuento();
        d.setIdTipoDescuento(UUID.randomUUID());
        model.guardarDescuento();
        verify(descuentoProductoDAO, never()).crear(any());
        mensaje(FacesMessage.SEVERITY_ERROR, "no pertenece");
    }

    @Test
    void descuentoSinFechaFinalPermiteAsignacionAbierta() {
        editarProducto();
        Descuento d = prepararDescuento();
        d.setFechaHasta(null);
        DescuentoProducto nuevo = model.getNuevoDescuento();
        nuevo.setFechaHasta(null);
        when(descuentoProductoDAO.crear(nuevo)).thenReturn(true);
        model.guardarDescuento();
        verify(descuentoProductoDAO).crear(nuevo);
    }

    @Test
    void eliminarTipoExito() {
        editarProducto();
        ProductoTipoProducto detalle = new ProductoTipoProducto(UUID.randomUUID());
        model.setTipoSeleccionado(detalle);
        when(productoTipoProductoDAO.eliminar(detalle)).thenReturn(true);
        model.eliminarTipo();
        verify(productoTipoProductoDAO).eliminar(detalle);
        assertNull(model.getTipoSeleccionado());
    }

    @Test
    void eliminarTipoFallido() {
        editarProducto();
        ProductoTipoProducto detalle = new ProductoTipoProducto(UUID.randomUUID());
        model.setTipoSeleccionado(detalle);
        model.eliminarTipo();
        verify(productoTipoProductoDAO).eliminar(detalle);
        assertSame(detalle, model.getTipoSeleccionado());
    }

    @Test
    void eliminarProductoSeDetieneSiFallaTipo() {
        Producto p = model.nuevoRegistro();
        ProductoTipoProducto detalle = new ProductoTipoProducto(UUID.randomUUID());
        when(productoTipoProductoDAO.findByProducto(p.getIdProducto())).thenReturn(List.of(detalle));
        model.eliminar(p);
        verify(productoDAO, never()).eliminar(any());
        mensaje(FacesMessage.SEVERITY_ERROR, "quitar");
    }

    @Test
    void eliminarCaracteristicaExito() {
        editarProducto();
        ProductoCaracteristica detalle = new ProductoCaracteristica(UUID.randomUUID());
        model.setCaracteristicaSeleccionada(detalle);
        when(productoCaracteristicaDAO.eliminar(detalle)).thenReturn(true);
        model.eliminarCaracteristica();
        verify(productoCaracteristicaDAO).eliminar(detalle);
        assertNull(model.getCaracteristicaSeleccionada());
    }

    @Test
    void eliminarCaracteristicaFallido() {
        editarProducto();
        ProductoCaracteristica detalle = new ProductoCaracteristica(UUID.randomUUID());
        model.setCaracteristicaSeleccionada(detalle);
        model.eliminarCaracteristica();
        verify(productoCaracteristicaDAO).eliminar(detalle);
        assertSame(detalle, model.getCaracteristicaSeleccionada());
    }

    @Test
    void eliminarProductoSeDetieneSiFallaCaracteristica() {
        Producto p = model.nuevoRegistro();
        ProductoCaracteristica detalle = new ProductoCaracteristica(UUID.randomUUID());
        when(productoCaracteristicaDAO.findByProducto(p.getIdProducto())).thenReturn(List.of(detalle));
        model.eliminar(p);
        verify(productoDAO, never()).eliminar(any());
        mensaje(FacesMessage.SEVERITY_ERROR, "quitar");
    }

    @Test
    void eliminarDescuentoExito() {
        editarProducto();
        DescuentoProducto detalle = new DescuentoProducto(UUID.randomUUID());
        model.setDescuentoSeleccionado(detalle);
        when(descuentoProductoDAO.eliminar(detalle)).thenReturn(true);
        model.eliminarDescuento();
        verify(descuentoProductoDAO).eliminar(detalle);
        assertNull(model.getDescuentoSeleccionado());
    }

    @Test
    void eliminarDescuentoFallido() {
        editarProducto();
        DescuentoProducto detalle = new DescuentoProducto(UUID.randomUUID());
        model.setDescuentoSeleccionado(detalle);
        model.eliminarDescuento();
        verify(descuentoProductoDAO).eliminar(detalle);
        assertSame(detalle, model.getDescuentoSeleccionado());
    }

    @Test
    void eliminarProductoSeDetieneSiFallaDescuento() {
        Producto p = model.nuevoRegistro();
        DescuentoProducto detalle = new DescuentoProducto(UUID.randomUUID());
        when(descuentoProductoDAO.findByProducto(p.getIdProducto())).thenReturn(List.of(detalle));
        model.eliminar(p);
        verify(productoDAO, never()).eliminar(any());
        mensaje(FacesMessage.SEVERITY_ERROR, "quitar");
    }

    @Test
    void eliminarProductoQuitaDetallesAntesDeLaEntidad() {
        Producto p = model.nuevoRegistro();
        ProductoTipoProducto t = new ProductoTipoProducto(UUID.randomUUID());
        ProductoCaracteristica c = new ProductoCaracteristica(UUID.randomUUID());
        DescuentoProducto d = new DescuentoProducto(UUID.randomUUID());
        when(productoTipoProductoDAO.findByProducto(p.getIdProducto())).thenReturn(List.of(t));
        when(productoCaracteristicaDAO.findByProducto(p.getIdProducto())).thenReturn(List.of(c));
        when(descuentoProductoDAO.findByProducto(p.getIdProducto())).thenReturn(List.of(d));
        when(productoTipoProductoDAO.eliminar(t)).thenReturn(true);
        when(productoCaracteristicaDAO.eliminar(c)).thenReturn(true);
        when(descuentoProductoDAO.eliminar(d)).thenReturn(true);
        when(productoDAO.eliminar(p)).thenReturn(true);
        model.eliminar(p);
        InOrder orden = inOrder(productoTipoProductoDAO, productoCaracteristicaDAO, descuentoProductoDAO, productoDAO);
        orden.verify(productoTipoProductoDAO).eliminar(t);
        orden.verify(productoCaracteristicaDAO).eliminar(c);
        orden.verify(descuentoProductoDAO).eliminar(d);
        orden.verify(productoDAO).eliminar(p);
    }

    @Test
    void cambiarTipoDescuentoCargaOpcionesYLimpiaSeleccion() {
        editarProducto();
        model.abrirFormDescuento();
        TipoDescuento tipo = new TipoDescuento(UUID.randomUUID());
        Descuento d = new Descuento(UUID.randomUUID());
        model.setIdTipoDescuentoElegido(tipo.getIdTipoDescuento().toString());
        model.setIdDescuentoElegido(d.getIdDescuento().toString());
        when(tipoDescuentoDAO.buscarPorId(tipo.getIdTipoDescuento())).thenReturn(tipo);
        when(descuentoDAO.findByTipo(tipo.getIdTipoDescuento())).thenReturn(List.of(d));
        model.alCambiarTipoDescuento();
        assertSame(tipo, model.getTipoDescuentoElegido());
        assertEquals(List.of(d), model.getDescuentosDelTipo());
        assertNull(model.getIdDescuentoElegido());
        assertNull(model.getDescuentoElegido());
    }

    @Test
    void cambiarTipoDescuentoInvalidoLimpiaOpcionesSinConsultar() {
        model.setIdTipoDescuentoElegido("incorrecto");
        model.alCambiarTipoDescuento();
        assertNull(model.getTipoDescuentoElegido());
        assertTrue(model.getDescuentosDelTipo().isEmpty());
        verifyNoInteractions(tipoDescuentoDAO, descuentoDAO);
    }

    @Test
    void cambiarTipoDescuentoConErrorMuestraMensaje() {
        UUID id = UUID.randomUUID();
        model.setIdTipoDescuentoElegido(id.toString());
        when(tipoDescuentoDAO.buscarPorId(id)).thenThrow(new PersistenceException("Fallo"));
        model.alCambiarTipoDescuento();
        mensaje(FacesMessage.SEVERITY_ERROR, "cargar los descuentos");
    }

    @Test
    void cambiarDescuentoRecuperaSeleccion() {
        Descuento d = new Descuento(UUID.randomUUID());
        model.setIdDescuentoElegido(d.getIdDescuento().toString());
        when(descuentoDAO.buscarPorId(d.getIdDescuento())).thenReturn(d);
        model.alCambiarDescuento();
        assertSame(d, model.getDescuentoElegido());
        model.setIdDescuentoElegido(null);
        model.alCambiarDescuento();
        assertNull(model.getDescuentoElegido());
        verify(descuentoDAO, times(1)).buscarPorId(any());
    }

    @Test
    void cambiarDescuentoConErrorNoPropagaExcepcion() {
        UUID id = UUID.randomUUID();
        model.setIdDescuentoElegido(id.toString());
        when(descuentoDAO.buscarPorId(id)).thenThrow(new PersistenceException("Fallo"));
        assertDoesNotThrow(() -> model.alCambiarDescuento());
        assertNull(model.getDescuentoElegido());
    }

    @Test
    void seleccionesInvalidasNoCarganCatalogosDependientes() {
        model.setIdTipoCaracteristicaElegido("incorrecto");
        model.alCambiarTipoCaracteristica();
        assertNull(model.getTipoCaracteristicaElegido());
        assertTrue(model.getCaracteristicasDelTipo().isEmpty());
        verifyNoInteractions(tipoCaracteristicaDAO, caracteristicaDAO);
    }

    @Test
    void cargaDeTiposDisponiblesEntregaOpcionesDelDAO() {
        editarProducto();
        TipoProducto tp = new TipoProducto(UUID.randomUUID());
        TipoCaracteristica tc = new TipoCaracteristica(UUID.randomUUID());
        TipoDescuento td = new TipoDescuento(UUID.randomUUID());
        when(tipoProductoDAO.findAll()).thenReturn(List.of(tp));
        when(tipoCaracteristicaDAO.findAll()).thenReturn(List.of(tc));
        when(tipoDescuentoDAO.findAll()).thenReturn(List.of(td));
        model.abrirFormTipo();
        model.abrirFormCaracteristica();
        model.abrirFormDescuento();
        assertEquals(List.of(tp), model.getTiposProductoDisponibles());
        assertEquals(List.of(tc), model.getTiposCaracteristicaDisponibles());
        assertEquals(List.of(td), model.getTiposDescuentoDisponibles());
    }

    @Test
    void nuevoLimpiaSeleccionYDetallesAnteriores() {
        editarProducto();
        model.abrirFormTipo();
        model.setSeleccionado(model.getRegistro());
        model.nuevo();
        assertNull(model.getSeleccionado());
        assertFalse(model.isSubFormularioAbierto());
        assertTrue(model.getTiposAsignados().isEmpty());
        assertTrue(model.isCreando());
    }

    @Test
    void falloAlEliminarDetalleConservaProducto() {
        Producto p = model.nuevoRegistro();
        when(productoTipoProductoDAO.findByProducto(p.getIdProducto())).thenThrow(new PersistenceException("Fallo"));
        model.eliminar(p);
        verify(productoDAO, never()).eliminar(any());
        mensaje(FacesMessage.SEVERITY_ERROR, "No se puede eliminar");
    }

}
