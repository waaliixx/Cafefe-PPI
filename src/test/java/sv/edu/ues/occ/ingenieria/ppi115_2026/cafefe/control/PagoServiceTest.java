/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package sv.edu.ues.occ.ingenieria.ppi115_2026.cafefe.control;

import jakarta.persistence.EntityManager;
import jakarta.persistence.LockModeType;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import sv.edu.ues.occ.ingenieria.ppi115_2026.cafefe.control.CobroException.Codigo;
import sv.edu.ues.occ.ingenieria.ppi115_2026.cafefe.control.PagoService.LineaPago;
import sv.edu.ues.occ.ingenieria.ppi115_2026.cafefe.control.PagoService.ResultadoCobro;
import sv.edu.ues.occ.ingenieria.ppi115_2026.cafefe.entity.Caja;
import sv.edu.ues.occ.ingenieria.ppi115_2026.cafefe.entity.Empleado;
import sv.edu.ues.occ.ingenieria.ppi115_2026.cafefe.entity.EmpleadoRol;
import sv.edu.ues.occ.ingenieria.ppi115_2026.cafefe.entity.Factura;
import sv.edu.ues.occ.ingenieria.ppi115_2026.cafefe.entity.FacturaOrdenProducto;
import sv.edu.ues.occ.ingenieria.ppi115_2026.cafefe.entity.Orden;
import sv.edu.ues.occ.ingenieria.ppi115_2026.cafefe.entity.OrdenProducto;
import sv.edu.ues.occ.ingenieria.ppi115_2026.cafefe.entity.Pago;
import sv.edu.ues.occ.ingenieria.ppi115_2026.cafefe.entity.PagoDetalle;
import sv.edu.ues.occ.ingenieria.ppi115_2026.cafefe.entity.Rol;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
/**
 *
 * @author walixx
 */
/** Reglas de negocio del cobro (HU4). EntityManager y DAO simulados con Mockito. */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class PagoServiceTest {

    @Mock
    EntityManager em;
    @Mock
    OrdenProductoDAO ordenProductoDAO;
    @InjectMocks
    PagoService servicio;

    UUID idOrden = UUID.randomUUID();
    UUID idCaja = UUID.randomUUID();
    UUID idEmpleado = UUID.randomUUID();
    UUID idEmpleadoRol = UUID.randomUUID();

    Caja caja;
    Empleado empleado;
    EmpleadoRol cajero;

    @BeforeEach
    void escenarioBase() {
        caja = new Caja(idCaja);
        caja.setActivo(true);
        empleado = new Empleado(idEmpleado);
        empleado.setActivo(true);
        Rol rol = new Rol(UUID.randomUUID());
        rol.setNombre("Cajero");
        cajero = new EmpleadoRol(idEmpleadoRol);
        cajero.setIdEmpleado(idEmpleado);
        cajero.setIdRol(rol);
        cajero.setActivo(true);

        when(em.find(Caja.class, idCaja)).thenReturn(caja);
        when(em.find(EmpleadoRol.class, idEmpleadoRol)).thenReturn(cajero);
        when(em.find(Empleado.class, idEmpleado)).thenReturn(empleado);
        when(em.find(Orden.class, idOrden, LockModeType.PESSIMISTIC_WRITE)).thenReturn(new Orden(idOrden));
    }

    // ---------- utilidades ----------
    private void ordenConPrecios(String... precios) {
        List<OrdenProducto> lineas = new java.util.ArrayList<>();
        for (String p : precios) {
            OrdenProducto op = new OrdenProducto(UUID.randomUUID());
            op.setPrecio(p == null ? null : new BigDecimal(p));
            lineas.add(op);
        }
        when(ordenProductoDAO.findNoFacturadasByOrden(idOrden)).thenReturn(lineas);
    }

    private static LineaPago pago(String tipo, String monto) {
        return new LineaPago(tipo, new BigDecimal(monto), null);
    }

    private ResultadoCobro cobrar(String cliente, LineaPago... pagos) {
        return servicio.cobrar(idOrden, idCaja, idEmpleadoRol, cliente, List.of(pagos));
    }

    private CobroException falla(Codigo esperado, Runnable accion) {
        CobroException ex = assertThrows(CobroException.class, accion::run);
        assertEquals(esperado, ex.getCodigo());
        return ex;
    }

    private List<Object> persistidos() {
        ArgumentCaptor<Object> c = ArgumentCaptor.forClass(Object.class);
        verify(em, atLeast(0)).persist(c.capture());
        return c.getAllValues();
    }

    // ---------- caminos felices ----------
    @Test
    void pagoExactoEnEfectivoRegistraTodoSinVuelto() {
        ordenConPrecios("10.00", "8.50");
        ResultadoCobro r = cobrar("Ana García", pago("Efectivo", "18.50"));

        assertEquals(0, new BigDecimal("18.50").compareTo(r.total()));
        assertEquals(0, BigDecimal.ZERO.compareTo(r.vuelto()));
        assertEquals("Pagada", r.factura().getEstado());
        assertEquals("Pagado", r.pago().getEstado());
        assertEquals("Ana García", r.factura().getCliente());
        assertSame(caja, r.factura().getIdCaja());
        assertSame(cajero, r.factura().getIdEmpleadoRol());
        assertNotNull(r.factura().getFechaFacturacion());
        assertEquals(2, r.lineas().size());
        assertEquals(1, r.detalles().size());
    }

    @Test
    void persisteFacturaLineasPagoYDetalles() {
        ordenConPrecios("5.00", "5.00", "8.50");
        cobrar(null, pago("Efectivo", "10.00"), pago("Tarjeta", "8.50"));

        List<Object> p = persistidos();
        assertEquals(1, p.stream().filter(Factura.class::isInstance).count());
        assertEquals(3, p.stream().filter(FacturaOrdenProducto.class::isInstance).count());
        assertEquals(1, p.stream().filter(Pago.class::isInstance).count());
        assertEquals(2, p.stream().filter(PagoDetalle.class::isInstance).count());
        // la factura se persiste antes que sus hijos
        assertInstanceOf(Factura.class, p.get(0));
    }

    @Test
    void pagoFraccionadoEfectivoYTarjetaCompletaElTotal() {
        ordenConPrecios("10.00", "8.50");
        ResultadoCobro r = cobrar(null, pago("Efectivo", "10.00"), pago("Tarjeta", "8.50"));
        assertEquals(0, BigDecimal.ZERO.compareTo(r.vuelto()));
        assertEquals(0, new BigDecimal("18.50").compareTo(r.pagado()));
    }

    @Test
    void efectivoDeMasGeneraVueltoYQuedaEnObservacionesDelPago() {
        ordenConPrecios("18.50");
        ResultadoCobro r = cobrar(null, pago("Efectivo", "20.00"));
        assertEquals(0, new BigDecimal("1.50").compareTo(r.vuelto()));
        assertEquals("Vuelto: 1.50", r.pago().getObservaciones());
        // se guarda lo entregado, no lo neto
        assertEquals(0, new BigDecimal("20.00").compareTo(r.detalles().get(0).getMonto()));
    }

    @Test
    void vueltoSaleDelEfectivoCuandoSeCombinaConTarjeta() {
        ordenConPrecios("18.50");
        ResultadoCobro r = cobrar(null, pago("Tarjeta", "8.50"), pago("Efectivo", "11.00"));
        assertEquals(0, new BigDecimal("1.00").compareTo(r.vuelto()));
    }

    @Test
    void tarjetaPorElTotalExactoEsValida() {
        ordenConPrecios("18.50");
        ResultadoCobro r = cobrar(null, pago("Tarjeta", "18.50"));
        assertEquals(0, BigDecimal.ZERO.compareTo(r.vuelto()));
    }

    @Test
    void elPrecioDeLaLineaDeFacturaSeCongelaDesdeLaOrden() {
        ordenConPrecios("7.25");
        ResultadoCobro r = cobrar(null, pago("Efectivo", "7.25"));
        assertEquals(0, new BigDecimal("7.25").compareTo(r.lineas().get(0).getPrecio()));
        assertNotNull(r.lineas().get(0).getIdOrdenProducto());
        assertSame(r.factura(), r.lineas().get(0).getIdFactura());
    }

    @Test
    void detalleGuardaTipoReferenciaYApuntaAlPago() {
        ordenConPrecios("10.00");
        ResultadoCobro r = cobrar(null, new LineaPago("tarjeta", new BigDecimal("10"), "  AUT-123 "));
        PagoDetalle d = r.detalles().get(0);
        assertEquals("Tarjeta", d.getTipoPago());
        assertEquals("AUT-123", d.getReferenciaExterna());
        assertSame(r.pago(), d.getIdPago());
        assertSame(r.factura(), r.pago().getIdFactura());
    }

    @Test
    void tipoDePagoNoDistingueMayusculas() {
        ordenConPrecios("3.00");
        assertEquals("QR", cobrar(null, pago("qr", "3.00")).detalles().get(0).getTipoPago());
    }

    @Test
    void clienteEnBlancoSeGuardaComoNull() {
        ordenConPrecios("3.00");
        assertNull(cobrar("   ", pago("Efectivo", "3.00")).factura().getCliente());
    }

    @Test
    void clienteSeRecorta() {
        ordenConPrecios("3.00");
        assertEquals("Juan", cobrar("  Juan ", pago("Efectivo", "3.00")).factura().getCliente());
    }

    @Test
    void bloqueaLaOrdenParaEvitarCobrosDobles() {
        ordenConPrecios("3.00");
        cobrar(null, pago("Efectivo", "3.00"));
        verify(em).find(Orden.class, idOrden, LockModeType.PESSIMISTIC_WRITE);
    }

    // ---------- reglas de monto ----------
    @Test
    void saldoInsuficienteNoRegistraNada() {
        ordenConPrecios("18.50");
        CobroException ex = falla(Codigo.SALDO_INSUFICIENTE, () -> cobrar(null, pago("Efectivo", "10.00")));
        assertTrue(ex.getMessage().contains("8.50"));
        verify(em, never()).persist(any());
    }

    @Test
    void tarjetaPorEncimaDelTotalSeRechaza() {
        ordenConPrecios("18.50");
        falla(Codigo.EXCESO_NO_EFECTIVO, () -> cobrar(null, pago("Tarjeta", "20.00")));
        verify(em, never()).persist(any());
    }

    @Test
    void tarjetaYQrJuntosPorEncimaDelTotalSeRechazan() {
        ordenConPrecios("10.00");
        falla(Codigo.EXCESO_NO_EFECTIVO, () -> cobrar(null, pago("Tarjeta", "6.00"), pago("QR", "6.00"),
                pago("Efectivo", "1.00")));
    }

    @Test
    void ordenSinLineasPendientesYaEstaPagada() {
        when(ordenProductoDAO.findNoFacturadasByOrden(idOrden)).thenReturn(List.of());
        falla(Codigo.ORDEN_YA_PAGADA, () -> cobrar(null, pago("Efectivo", "5.00")));
        verify(em, never()).persist(any());
    }

    @Test
    void ordenInexistente() {
        when(em.find(Orden.class, idOrden, LockModeType.PESSIMISTIC_WRITE)).thenReturn(null);
        falla(Codigo.NO_ENCONTRADO, () -> cobrar(null, pago("Efectivo", "5.00")));
    }

    @Test
    void productoSinPrecioNoSePuedeCobrar() {
        ordenConPrecios("5.00", null);
        falla(Codigo.DATOS_INVALIDOS, () -> cobrar(null, pago("Efectivo", "5.00")));
        verify(em, never()).persist(any());
    }

    // ---------- caja y cajero ----------
    @Test
    void cajaInexistente() {
        when(em.find(Caja.class, idCaja)).thenReturn(null);
        ordenConPrecios("5.00");
        falla(Codigo.NO_ENCONTRADO, () -> cobrar(null, pago("Efectivo", "5.00")));
    }

    @Test
    void cajaInactiva() {
        caja.setActivo(false);
        ordenConPrecios("5.00");
        falla(Codigo.NO_AUTORIZADO, () -> cobrar(null, pago("Efectivo", "5.00")));
    }

    @Test
    void cajaSinMarcaDeActivaSeTrataComoInactiva() {
        caja.setActivo(null);
        ordenConPrecios("5.00");
        falla(Codigo.NO_AUTORIZADO, () -> cobrar(null, pago("Efectivo", "5.00")));
    }

    @Test
    void cajeroInexistente() {
        when(em.find(EmpleadoRol.class, idEmpleadoRol)).thenReturn(null);
        ordenConPrecios("5.00");
        falla(Codigo.NO_ENCONTRADO, () -> cobrar(null, pago("Efectivo", "5.00")));
    }

    @Test
    void soloElRolCajeroPuedeCobrar() {
        cajero.getIdRol().setNombre("Camarero");
        ordenConPrecios("5.00");
        falla(Codigo.NO_AUTORIZADO, () -> cobrar(null, pago("Efectivo", "5.00")));
    }

    @Test
    void rolCajeroNoDistingueMayusculas() {
        cajero.getIdRol().setNombre("cajero");
        ordenConPrecios("5.00");
        assertNotNull(cobrar(null, pago("Efectivo", "5.00")));
    }

    @Test
    void cajeroConRolInactivo() {
        cajero.setActivo(false);
        ordenConPrecios("5.00");
        falla(Codigo.NO_AUTORIZADO, () -> cobrar(null, pago("Efectivo", "5.00")));
    }

    @Test
    void cajeroSinRol() {
        cajero.setIdRol(null);
        ordenConPrecios("5.00");
        falla(Codigo.NO_AUTORIZADO, () -> cobrar(null, pago("Efectivo", "5.00")));
    }

    @Test
    void empleadoInactivo() {
        empleado.setActivo(false);
        ordenConPrecios("5.00");
        falla(Codigo.NO_AUTORIZADO, () -> cobrar(null, pago("Efectivo", "5.00")));
    }

    @Test
    void empleadoInexistente() {
        when(em.find(Empleado.class, idEmpleado)).thenReturn(null);
        ordenConPrecios("5.00");
        falla(Codigo.NO_AUTORIZADO, () -> cobrar(null, pago("Efectivo", "5.00")));
    }

    @Test
    void empleadoRolSinIdEmpleado() {
        cajero.setIdEmpleado(null);
        ordenConPrecios("5.00");
        falla(Codigo.NO_AUTORIZADO, () -> cobrar(null, pago("Efectivo", "5.00")));
    }

    // ---------- validación de entrada ----------
    @Test
    void idsObligatorios() {
        List<LineaPago> p = List.of(pago("Efectivo", "5.00"));
        falla(Codigo.DATOS_INVALIDOS, () -> servicio.cobrar(null, idCaja, idEmpleadoRol, null, p));
        falla(Codigo.DATOS_INVALIDOS, () -> servicio.cobrar(idOrden, null, idEmpleadoRol, null, p));
        falla(Codigo.DATOS_INVALIDOS, () -> servicio.cobrar(idOrden, idCaja, null, null, p));
    }

    @Test
    void clienteDemasiadoLargo() {
        ordenConPrecios("5.00");
        falla(Codigo.DATOS_INVALIDOS, () -> cobrar("x".repeat(156), pago("Efectivo", "5.00")));
    }

    @Test
    void sinFormasDePago() {
        falla(Codigo.DATOS_INVALIDOS, () -> servicio.cobrar(idOrden, idCaja, idEmpleadoRol, null, null));
        falla(Codigo.DATOS_INVALIDOS, () -> servicio.cobrar(idOrden, idCaja, idEmpleadoRol, null, List.of()));
    }

    @Test
    void pagoNuloOIncompleto() {
        java.util.List<LineaPago> conNulo = new java.util.ArrayList<>();
        conNulo.add(null);
        falla(Codigo.DATOS_INVALIDOS, () -> servicio.cobrar(idOrden, idCaja, idEmpleadoRol, null, conNulo));
        falla(Codigo.DATOS_INVALIDOS, () -> servicio.cobrar(idOrden, idCaja, idEmpleadoRol, null,
                List.of(new LineaPago(null, BigDecimal.ONE, null))));
        falla(Codigo.DATOS_INVALIDOS, () -> servicio.cobrar(idOrden, idCaja, idEmpleadoRol, null,
                List.of(new LineaPago("Efectivo", null, null))));
    }

    @Test
    void formaDePagoNoAdmitida() {
        CobroException ex = falla(Codigo.DATOS_INVALIDOS, () -> cobrar(null, pago("Cheque", "5.00")));
        assertTrue(ex.getMessage().contains("Cheque"));
    }

    @Test
    void montoCeroONegativo() {
        falla(Codigo.DATOS_INVALIDOS, () -> cobrar(null, pago("Efectivo", "0")));
        falla(Codigo.DATOS_INVALIDOS, () -> cobrar(null, pago("Efectivo", "-1.00")));
    }

    @Test
    void montoConMasDeDosDecimales() {
        falla(Codigo.DATOS_INVALIDOS, () -> cobrar(null, pago("Efectivo", "5.001")));
    }

    @Test
    void montoConCerosDeSobraEnDecimalesEsValido() {
        ordenConPrecios("5.00");
        assertNotNull(cobrar(null, pago("Efectivo", "5.0000")));
    }

    @Test
    void referenciaDemasiadoLarga() {
        falla(Codigo.DATOS_INVALIDOS, () -> servicio.cobrar(idOrden, idCaja, idEmpleadoRol, null,
                List.of(new LineaPago("Tarjeta", BigDecimal.TEN, "r".repeat(201)))));
    }

    @Test
    void referenciaEnBlancoSeGuardaComoNull() {
        List<LineaPago> n = servicio.normalizarPagos(List.of(new LineaPago("QR", BigDecimal.TEN, "  ")));
        assertNull(n.get(0).referenciaExterna());
        assertEquals("QR", n.get(0).tipoPago());
        assertEquals(2, n.get(0).monto().scale());
    }
}
