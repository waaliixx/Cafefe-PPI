package sv.edu.ues.occ.ingenieria.ppi115_2026.cafefe.model;

import jakarta.faces.application.FacesMessage;
import jakarta.persistence.PersistenceException;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.InOrder;
import org.mockito.Mock;
import sv.edu.ues.occ.ingenieria.ppi115_2026.cafefe.dao.*;
import sv.edu.ues.occ.ingenieria.ppi115_2026.cafefe.entity.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/** Pruebas de comportamiento de OrdenModel, con DAO y contexto JSF simulados. */
class OrdenModelTest extends ModelTestSupport {

    @Mock OrdenDAO ordenDAO;
    @Mock OrdenProductoDAO ordenProductoDAO;
    @Mock EmpleadoRolDAO empleadoRolDAO;
    @Mock EmpleadoDAO empleadoDAO;
    @Mock ProductoDAO productoDAO;
    @Mock DescuentoProductoDAO descuentoProductoDAO;
    @InjectMocks OrdenModel model;

    private Orden editarOrden() {
        Orden o = model.nuevoRegistro();
        o.setFechaCreacion(fecha("2026-01-15"));
        when(ordenDAO.buscarPorId(o.getIdOrden())).thenReturn(o);
        model.editar(o);
        return o;
    }

    private EmpleadoRol camarero(Empleado e) {
        Rol rol = new Rol(UUID.randomUUID());
        rol.setNombre("Camarero");
        EmpleadoRol er = new EmpleadoRol(UUID.randomUUID());
        er.setIdEmpleado(e.getIdEmpleado());
        er.setIdRol(rol);
        er.setActivo(true);
        return er;
    }

    private Empleado cargarEmpleado() {
        Empleado e = new Empleado(UUID.randomUUID());
        e.setNombre("Ana");
        e.setApellido("Pérez");
        e.setActivo(true);
        when(empleadoDAO.findAll()).thenReturn(List.of(e));
        model.cargarRegistros();
        return e;
    }

    private Producto prepararProducto() {
        model.abrirFormProducto();
        Producto p = new Producto(UUID.randomUUID());
        p.setNombre("Café");
        p.setActivo(true);
        p.setPrecioSugerido(new BigDecimal("2.50"));
        model.setIdProductoElegido(p.getIdProducto().toString());
        when(productoDAO.buscarPorId(p.getIdProducto())).thenReturn(p);
        return p;
    }

    private DescuentoProducto prepararDescuento() {
        Producto p = new Producto(UUID.randomUUID());
        p.setPrecioSugerido(new BigDecimal("10.00"));
        OrdenProducto linea = new OrdenProducto(UUID.randomUUID());
        linea.setIdOrden(model.getRegistro());
        linea.setIdProducto(p);
        linea.setPrecio(new BigDecimal("10.00"));
        Descuento d = new Descuento(UUID.randomUUID());
        d.setNombre("Promoción");
        d.setFechaDesde(fecha("2026-01-01"));
        d.setFechaHasta(fecha("2026-01-31"));
        DescuentoProducto dp = new DescuentoProducto(UUID.randomUUID());
        dp.setIdProducto(p);
        dp.setIdDescuento(d);
        dp.setValor(15);
        dp.setFechaDesde(fecha("2026-01-10"));
        dp.setFechaHasta(fecha("2026-01-20"));
        model.abrirFormDescuento(linea);
        model.setIdDescuentoElegido(dp.getIdDescuentoProducto().toString());
        when(ordenProductoDAO.buscarPorId(linea.getIdOrdenProducto())).thenReturn(linea);
        when(descuentoProductoDAO.buscarPorId(dp.getIdDescuentoProducto())).thenReturn(dp);
        return dp;
    }

    @Test
    void usaElDAOInyectado() {
        assertSame(ordenDAO, model.getDAO());
    }

    @Test
    void nuevoRegistroGeneraIdentificadoresDiferentes() {
        Orden primero = model.nuevoRegistro();
        Orden segundo = model.nuevoRegistro();
        assertNotNull(primero.getIdOrden());
        assertNotEquals(primero.getIdOrden(), segundo.getIdOrden());
        assertEquals(primero.getIdOrden(), model.getIdRegistro(primero));
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
    void nuevaOrdenTieneFechaActual() {
        long antes = System.currentTimeMillis();
        Orden o = model.nuevoRegistro();
        long despues = System.currentTimeMillis();
        assertNotNull(o.getFechaCreacion());
        assertTrue(o.getFechaCreacion().getTime() >= antes);
        assertTrue(o.getFechaCreacion().getTime() <= despues);
    }

    @Test
    void cargarOrdenaPorFechaDescendenteConNulosAlFinal() {
        Orden antigua = model.nuevoRegistro();
        antigua.setFechaCreacion(fecha("2026-01-01"));
        Orden reciente = model.nuevoRegistro();
        reciente.setFechaCreacion(fecha("2026-01-02"));
        Orden sinFecha = new Orden(UUID.randomUUID());
        when(ordenDAO.findAll()).thenReturn(List.of(antigua, sinFecha, reciente));
        model.cargarRegistros();
        assertEquals(List.of(reciente, antigua, sinFecha), model.getRegistros());
    }

    @Test
    void camarerosFiltranEmpleadosInactivosYOrdenanPorNombre() {
        Empleado ana = new Empleado(UUID.randomUUID());
        ana.setNombre("Ana");
        ana.setActivo(true);
        Empleado zoe = new Empleado(UUID.randomUUID());
        zoe.setNombre("Zoe");
        zoe.setActivo(true);
        Empleado inactivo = new Empleado(UUID.randomUUID());
        inactivo.setActivo(false);
        EmpleadoRol a = camarero(ana);
        EmpleadoRol z = camarero(zoe);
        EmpleadoRol i = camarero(inactivo);
        EmpleadoRol inexistente = camarero(new Empleado(UUID.randomUUID()));
        when(empleadoDAO.findAll()).thenReturn(List.of(zoe, ana, inactivo));
        when(empleadoRolDAO.findActivosPorNombreRol("Camarero")).thenReturn(List.of(z, i, inexistente, a));
        model.cargarRegistros();
        model.nuevo();
        assertEquals(List.of(a, z), model.getCamareros());
    }

    @Test
    void guardarOrdenAsignaCamareroYFecha() {
        Empleado e = cargarEmpleado();
        EmpleadoRol er = camarero(e);
        model.nuevo();
        Orden o = model.getRegistro();
        o.setFechaCreacion(null);
        model.setIdEmpleadoRolElegido(er.getIdEmpleadoRol().toString());
        when(empleadoRolDAO.buscarPorId(er.getIdEmpleadoRol())).thenReturn(er);
        when(ordenDAO.crear(o)).thenReturn(true);
        model.guardar();
        assertSame(er, o.getIdEmpleadoRol());
        assertNotNull(o.getFechaCreacion());
        assertTrue(model.isListado());
    }

    @Test
    void camareroRolIncorrectoImpideGuardar() {
        Empleado e = cargarEmpleado();
        EmpleadoRol er = camarero(e);
        er.getIdRol().setNombre("Cajero");
        model.nuevo();
        model.setIdEmpleadoRolElegido(er.getIdEmpleadoRol().toString());
        when(empleadoRolDAO.buscarPorId(er.getIdEmpleadoRol())).thenReturn(er);
        model.guardar();
        verify(ordenDAO, never()).crear(any());
        assertTrue(model.isCreando());
        mensaje(FacesMessage.SEVERITY_ERROR, "Camarero");
    }

    @Test
    void camareroRolNuloImpideGuardar() {
        Empleado e = cargarEmpleado();
        EmpleadoRol er = camarero(e);
        er.setIdRol(null);
        model.nuevo();
        model.setIdEmpleadoRolElegido(er.getIdEmpleadoRol().toString());
        when(empleadoRolDAO.buscarPorId(er.getIdEmpleadoRol())).thenReturn(er);
        model.guardar();
        verify(ordenDAO, never()).crear(any());
        assertTrue(model.isCreando());
        mensaje(FacesMessage.SEVERITY_ERROR, "Camarero");
    }

    @Test
    void camareroAsignacionInactivaImpideGuardar() {
        Empleado e = cargarEmpleado();
        EmpleadoRol er = camarero(e);
        er.setActivo(false);
        model.nuevo();
        model.setIdEmpleadoRolElegido(er.getIdEmpleadoRol().toString());
        when(empleadoRolDAO.buscarPorId(er.getIdEmpleadoRol())).thenReturn(er);
        model.guardar();
        verify(ordenDAO, never()).crear(any());
        assertTrue(model.isCreando());
        mensaje(FacesMessage.SEVERITY_ERROR, "inactivo");
    }

    @Test
    void camareroEmpleadoInactivoImpideGuardar() {
        Empleado e = cargarEmpleado();
        EmpleadoRol er = camarero(e);
        e.setActivo(false);
        model.nuevo();
        model.setIdEmpleadoRolElegido(er.getIdEmpleadoRol().toString());
        when(empleadoRolDAO.buscarPorId(er.getIdEmpleadoRol())).thenReturn(er);
        model.guardar();
        verify(ordenDAO, never()).crear(any());
        assertTrue(model.isCreando());
        mensaje(FacesMessage.SEVERITY_ERROR, "inactivo");
    }

    @Test
    void camareroUUIDInvalidoNoConsulta() {
        model.nuevo();
        model.setIdEmpleadoRolElegido("incorrecto");
        model.guardar();
        verify(empleadoRolDAO, never()).buscarPorId(any());
        verify(ordenDAO, never()).crear(any());
        mensaje(FacesMessage.SEVERITY_WARN, "camarero");
    }

    @Test
    void ordenExistenteNoPermiteModificarGeneralidades() {
        editarOrden();
        model.guardar();
        verify(ordenDAO, never()).modificar(any());
        mensaje(FacesMessage.SEVERITY_WARN, "no se puede modificar");
    }

    @Test
    void etiquetasMuestranEmpleadoYRol() {
        Empleado e = cargarEmpleado();
        EmpleadoRol er = camarero(e);
        Orden o = new Orden(UUID.randomUUID());
        o.setIdEmpleadoRol(er);
        assertEquals("Ana Pérez - Camarero", model.etiquetaCamarero(er));
        assertEquals("Camarero - Ana Pérez", model.empleoYNombre(o));
        assertEquals("Ana Pérez", model.nombreCamarero(o));
        assertEquals("", model.etiquetaCamarero(null));
        assertEquals("", model.empleoYNombre(null));
        assertEquals("", model.nombreCamarero(new Orden()));
    }

    @Test
    void formulariosRequierenOrdenGuardada() {
        model.nuevo();
        model.abrirFormProducto();
        model.abrirFormDescuento(new OrdenProducto(UUID.randomUUID()));
        assertFalse(model.isSubFormularioAbierto());
        verifyNoInteractions(productoDAO, descuentoProductoDAO);
        mensaje(FacesMessage.SEVERITY_WARN, "Guarde primero");
    }

    @Test
    void abrirProductoOrdenaCatalogoConNulosAlFinal() {
        editarOrden();
        Producto z = new Producto(UUID.randomUUID());
        z.setNombre("Zumo");
        Producto a = new Producto(UUID.randomUUID());
        a.setNombre("agua");
        Producto n = new Producto(UUID.randomUUID());
        when(productoDAO.findAll()).thenReturn(List.of(z, n, a));
        model.abrirFormProducto();
        assertEquals(List.of(a, z, n), model.getProductosDisponibles());
        assertTrue(model.isFormProducto());
        assertNotNull(model.getNuevoProducto().getIdOrdenProducto());
    }

    @Test
    void guardarProductoAsignaPrecioYNormalizaObservaciones() {
        Orden o = editarOrden();
        Producto p = prepararProducto();
        OrdenProducto nuevo = model.getNuevoProducto();
        nuevo.setObservaciones("  Sin azúcar  ");
        when(ordenProductoDAO.crear(nuevo)).thenReturn(true);
        model.guardarProducto();
        assertSame(o, nuevo.getIdOrden());
        assertSame(p, nuevo.getIdProducto());
        assertEquals(new BigDecimal("2.50"), nuevo.getPrecio());
        assertEquals("Sin azúcar", nuevo.getObservaciones());
        assertFalse(model.isFormProducto());
        assertNull(model.getNuevoProducto());
    }

    @Test
    void productoInactivoNoSeAgrega() {
        editarOrden();
        Producto p = prepararProducto();
        p.setActivo(false);
        model.guardarProducto();
        verify(ordenProductoDAO, never()).crear(any());
        mensaje(FacesMessage.SEVERITY_ERROR, "inactivo");
    }

    @Test
    void productoInexistenteNoSeAgrega() {
        editarOrden();
        model.abrirFormProducto();
        model.setIdProductoElegido(UUID.randomUUID().toString());
        model.guardarProducto();
        verify(ordenProductoDAO, never()).crear(any());
        mensaje(FacesMessage.SEVERITY_ERROR, "ya no existe");
    }

    @Test
    void guardarProductoFallidoConservaFormulario() {
        editarOrden();
        prepararProducto();
        OrdenProducto nuevo = model.getNuevoProducto();
        model.guardarProducto();
        assertSame(nuevo, model.getNuevoProducto());
        assertTrue(model.isFormProducto());
        mensaje(FacesMessage.SEVERITY_ERROR, "agregar");
    }

    @Test
    void falloAlCargarProductosNoAbreFormulario() {
        editarOrden();
        when(productoDAO.findAll()).thenThrow(new PersistenceException("Fallo"));
        model.abrirFormProducto();
        assertFalse(model.isFormProducto());
        mensaje(FacesMessage.SEVERITY_ERROR, "cargar");
    }

    @Test
    void eliminarProductoExito() {
        editarOrden();
        OrdenProducto p = new OrdenProducto(UUID.randomUUID());
        model.setProductoSeleccionado(p);
        when(ordenProductoDAO.eliminar(p)).thenReturn(true);
        model.eliminarProducto();
        verify(ordenProductoDAO).eliminar(p);
        assertNull(model.getProductoSeleccionado());
    }

    @Test
    void eliminarProductoFallido() {
        editarOrden();
        OrdenProducto p = new OrdenProducto(UUID.randomUUID());
        model.setProductoSeleccionado(p);
        model.eliminarProducto();
        verify(ordenProductoDAO).eliminar(p);
        assertSame(p, model.getProductoSeleccionado());
    }

    @Test
    void abrirDescuentoFiltraAsignacionesSinNombreOPorcentaje() {
        editarOrden();
        Producto p = new Producto(UUID.randomUUID());
        OrdenProducto linea = new OrdenProducto(UUID.randomUUID());
        linea.setIdProducto(p);
        DescuentoProducto valido = new DescuentoProducto(UUID.randomUUID());
        valido.setIdDescuento(new Descuento(UUID.randomUUID()));
        valido.setValor(10);
        DescuentoProducto sinDescuento = new DescuentoProducto(UUID.randomUUID());
        sinDescuento.setValor(10);
        DescuentoProducto cero = new DescuentoProducto(UUID.randomUUID());
        cero.setIdDescuento(valido.getIdDescuento());
        cero.setValor(0);
        when(descuentoProductoDAO.findByProducto(p.getIdProducto())).thenReturn(List.of(valido, sinDescuento, cero));
        model.abrirFormDescuento(linea);
        assertEquals(List.of(valido), model.getDescuentosDisponibles());
        assertTrue(model.isFormDescuento());
    }

    @Test
    void aplicarDescuentoCalculaPrecioYAgregaComentario() {
        editarOrden();
        DescuentoProducto dp = prepararDescuento();
        dp.setObservaciones("  Promoción de temporada  ");
        OrdenProducto linea = model.getLineaConDescuento();
        linea.setObservaciones("Sin azúcar");
        when(ordenProductoDAO.modificar(linea)).thenReturn(true);
        model.aplicarDescuento();
        assertEquals(new BigDecimal("8.50"), linea.getPrecio());
        assertTrue(linea.getObservaciones().startsWith("Sin azúcar | "));
        assertTrue(linea.getObservaciones().contains("15%: -$1.50"));
        assertTrue(linea.getObservaciones().contains("Promoción de temporada"));
        verify(ordenProductoDAO).modificar(linea);
        mensaje(FacesMessage.SEVERITY_INFO, "Nuevo precio: $8.50");
    }

    @Test
    void descuentoRedondeaAMedioHaciaArriba() {
        editarOrden();
        DescuentoProducto dp = prepararDescuento();
        dp.setValor(50);
        OrdenProducto linea = model.getLineaConDescuento();
        linea.setPrecio(new BigDecimal("1.99"));
        when(ordenProductoDAO.modificar(linea)).thenReturn(true);
        model.aplicarDescuento();
        assertEquals(new BigDecimal("1.00"), linea.getPrecio());
    }

    @Test
    void precioNuloUsaPrecioSugerido() {
        editarOrden();
        prepararDescuento();
        OrdenProducto linea = model.getLineaConDescuento();
        linea.setPrecio(null);
        when(ordenProductoDAO.modificar(linea)).thenReturn(true);
        model.aplicarDescuento();
        assertEquals(new BigDecimal("8.50"), linea.getPrecio());
    }

    @Test
    void descuentoCienPorCientoDaPrecioCero() {
        editarOrden();
        DescuentoProducto dp = prepararDescuento();
        dp.setValor(100);
        OrdenProducto linea = model.getLineaConDescuento();
        when(ordenProductoDAO.modificar(linea)).thenReturn(true);
        model.aplicarDescuento();
        assertEquals(new BigDecimal("0.00"), linea.getPrecio());
    }

    @Test
    void porcentajeNuloNoSeAplica() {
        editarOrden();
        DescuentoProducto dp = prepararDescuento();
        dp.setValor(null);
        model.aplicarDescuento();
        verify(ordenProductoDAO, never()).modificar(any());
        assertEquals(new BigDecimal("10.00"), model.getLineaConDescuento().getPrecio());
        mensaje(FacesMessage.SEVERITY_ERROR, "porcentaje");
    }

    @Test
    void porcentajeCeroNoSeAplica() {
        editarOrden();
        DescuentoProducto dp = prepararDescuento();
        dp.setValor(0);
        model.aplicarDescuento();
        verify(ordenProductoDAO, never()).modificar(any());
        assertEquals(new BigDecimal("10.00"), model.getLineaConDescuento().getPrecio());
        mensaje(FacesMessage.SEVERITY_ERROR, "porcentaje");
    }

    @Test
    void porcentajeNegativoNoSeAplica() {
        editarOrden();
        DescuentoProducto dp = prepararDescuento();
        dp.setValor(-1);
        model.aplicarDescuento();
        verify(ordenProductoDAO, never()).modificar(any());
        assertEquals(new BigDecimal("10.00"), model.getLineaConDescuento().getPrecio());
        mensaje(FacesMessage.SEVERITY_ERROR, "porcentaje");
    }

    @Test
    void porcentajeMayorQueCienNoSeAplica() {
        editarOrden();
        DescuentoProducto dp = prepararDescuento();
        dp.setValor(101);
        model.aplicarDescuento();
        verify(ordenProductoDAO, never()).modificar(any());
        assertEquals(new BigDecimal("10.00"), model.getLineaConDescuento().getPrecio());
        mensaje(FacesMessage.SEVERITY_ERROR, "porcentaje");
    }

    @Test
    void fechaOrdenInicioAsignacion() {
        Orden o = editarOrden();
        o.setFechaCreacion(fecha("2026-01-10"));
        prepararDescuento();
        OrdenProducto linea = model.getLineaConDescuento();
        when(ordenProductoDAO.modificar(linea)).thenReturn(true);
        model.aplicarDescuento();
        verify(ordenProductoDAO).modificar(linea);
    }

    @Test
    void fechaOrdenFinAsignacion() {
        Orden o = editarOrden();
        o.setFechaCreacion(fecha("2026-01-20"));
        prepararDescuento();
        OrdenProducto linea = model.getLineaConDescuento();
        when(ordenProductoDAO.modificar(linea)).thenReturn(true);
        model.aplicarDescuento();
        verify(ordenProductoDAO).modificar(linea);
    }

    @Test
    void fechaOrdenAntesAsignacion() {
        Orden o = editarOrden();
        o.setFechaCreacion(fecha("2026-01-09"));
        prepararDescuento();
        OrdenProducto linea = model.getLineaConDescuento();
        model.aplicarDescuento();
        verify(ordenProductoDAO, never()).modificar(any());
        mensaje(FacesMessage.SEVERITY_WARN, "no está vigente");
    }

    @Test
    void fechaOrdenDespuesAsignacion() {
        Orden o = editarOrden();
        o.setFechaCreacion(fecha("2026-01-21"));
        prepararDescuento();
        OrdenProducto linea = model.getLineaConDescuento();
        model.aplicarDescuento();
        verify(ordenProductoDAO, never()).modificar(any());
        mensaje(FacesMessage.SEVERITY_WARN, "no está vigente");
    }

    @Test
    void vigenciaGeneralTambienDebeCumplirse() {
        editarOrden();
        DescuentoProducto dp = prepararDescuento();
        dp.getIdDescuento().setFechaHasta(fecha("2026-01-14"));
        model.aplicarDescuento();
        verify(ordenProductoDAO, never()).modificar(any());
        mensaje(FacesMessage.SEVERITY_WARN, "no está vigente");
    }

    @Test
    void descuentoDeOtroProductoNoSeAplica() {
        editarOrden();
        DescuentoProducto dp = prepararDescuento();
        dp.setIdProducto(new Producto(UUID.randomUUID()));
        model.aplicarDescuento();
        verify(ordenProductoDAO, never()).modificar(any());
        mensaje(FacesMessage.SEVERITY_ERROR, "no corresponde");
    }

    @Test
    void productoSinPrecioNoRecibeDescuento() {
        editarOrden();
        prepararDescuento();
        OrdenProducto linea = model.getLineaConDescuento();
        linea.setPrecio(null);
        linea.getIdProducto().setPrecioSugerido(null);
        model.aplicarDescuento();
        verify(ordenProductoDAO, never()).modificar(any());
        mensaje(FacesMessage.SEVERITY_ERROR, "no tiene precio");
    }

    @Test
    void etiquetaDescuentoIncluyeVigencia() {
        Descuento d = new Descuento(UUID.randomUUID());
        d.setNombre("Promoción");
        DescuentoProducto dp = new DescuentoProducto(UUID.randomUUID());
        dp.setIdDescuento(d);
        dp.setValor(15);
        assertEquals("Promoción - 15% (sin límite de fechas)", model.etiquetaDescuento(dp));
        dp.setFechaDesde(fecha("2026-01-10"));
        assertTrue(model.etiquetaDescuento(dp).contains("desde 10/01/2026, sin fecha final"));
        dp.setFechaHasta(fecha("2026-01-20"));
        assertTrue(model.etiquetaDescuento(dp).contains("10/01/2026 al 20/01/2026"));
        dp.setFechaDesde(null);
        assertTrue(model.etiquetaDescuento(dp).contains("hasta 20/01/2026"));
        assertEquals("", model.etiquetaDescuento(null));
        assertEquals("", model.etiquetaDescuento(new DescuentoProducto()));
    }

    @Test
    void eliminarOrdenConProductosExito() {
        Orden o = model.nuevoRegistro();
        OrdenProducto linea = new OrdenProducto(UUID.randomUUID());
        when(ordenProductoDAO.findByOrden(o.getIdOrden())).thenReturn(List.of(linea));
        when(ordenProductoDAO.eliminar(linea)).thenReturn(true);
        when(ordenDAO.eliminar(o)).thenReturn(true);
        model.eliminar(o);
        InOrder orden = inOrder(ordenProductoDAO, ordenDAO);
        orden.verify(ordenProductoDAO).eliminar(linea);
        orden.verify(ordenDAO).eliminar(o);
    }

    @Test
    void eliminarOrdenConProductosFalse() {
        Orden o = model.nuevoRegistro();
        OrdenProducto linea = new OrdenProducto(UUID.randomUUID());
        when(ordenProductoDAO.findByOrden(o.getIdOrden())).thenReturn(List.of(linea));
        model.eliminar(o);
        verify(ordenDAO, never()).eliminar(any());
        mensaje(FacesMessage.SEVERITY_ERROR, "quitar");
    }

    @Test
    void eliminarOrdenConProductosExcepcion() {
        Orden o = model.nuevoRegistro();
        OrdenProducto linea = new OrdenProducto(UUID.randomUUID());
        when(ordenProductoDAO.findByOrden(o.getIdOrden())).thenReturn(List.of(linea));
        when(ordenProductoDAO.eliminar(linea)).thenThrow(new PersistenceException("Fallo"));
        model.eliminar(o);
        verify(ordenDAO, never()).eliminar(any());
        mensaje(FacesMessage.SEVERITY_ERROR, "asociados");
    }

    @Test
    void cancelarLimpiaProductosYDescuentos() {
        editarOrden();
        model.abrirFormProducto();
        OrdenProducto linea = new OrdenProducto(UUID.randomUUID());
        linea.setIdProducto(new Producto(UUID.randomUUID()));
        model.abrirFormDescuento(linea);
        model.cancelar();
        assertFalse(model.isSubFormularioAbierto());
        assertNull(model.getNuevoProducto());
        assertNull(model.getLineaConDescuento());
        assertNull(model.getSeleccionado());
    }
}
