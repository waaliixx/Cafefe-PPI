package sv.edu.ues.occ.ingenieria.ppi115_2026.cafefe.control;

import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import sv.edu.ues.occ.ingenieria.ppi115_2026.cafefe.entity.Empleado;
import sv.edu.ues.occ.ingenieria.ppi115_2026.cafefe.entity.EmpleadoRol;
import sv.edu.ues.occ.ingenieria.ppi115_2026.cafefe.entity.Factura;
import sv.edu.ues.occ.ingenieria.ppi115_2026.cafefe.entity.FacturaOrdenProducto;
import sv.edu.ues.occ.ingenieria.ppi115_2026.cafefe.entity.Pago;
import sv.edu.ues.occ.ingenieria.ppi115_2026.cafefe.entity.PagoDetalle;

/** Arma los datos del ticket de una factura. Quien dibuja el PDF es TicketPdf. */
@Stateless
public class TicketService {

    @Inject
    private FacturaDAO facturaDAO;
    @Inject
    private FacturaOrdenProductoDAO facturaOrdenProductoDAO;
    @Inject
    private PagoDAO pagoDAO;
    @Inject
    private PagoDetalleDAO pagoDetalleDAO;
    @Inject
    private EmpleadoDAO empleadoDAO;

    public record Ticket(String idFactura, Instant fecha, String cliente, String caja, String cajero,
            String estado, List<LineaTicket> lineas, List<PagoTicket> pagos,
            BigDecimal total, BigDecimal pagado, BigDecimal vuelto) {
    }

    /** Productos iguales con el mismo precio se agrupan: "2 x Latte". */
    public record LineaTicket(int cantidad, String producto, BigDecimal precioUnitario, BigDecimal subtotal) {
    }

    public record PagoTicket(String tipoPago, BigDecimal monto, String referencia) {
    }

    /** El ticket de la factura, o null si la factura no existe. */
    public Ticket ticket(UUID idFactura) {
        Factura factura = idFactura == null ? null : facturaDAO.buscarPorId(idFactura);
        if (factura == null) {
            return null;
        }
        List<LineaTicket> lineas = agrupar(facturaOrdenProductoDAO.findByFactura(idFactura));
        BigDecimal total = BigDecimal.ZERO;
        for (LineaTicket l : lineas) {
            total = total.add(l.subtotal());
        }
        List<PagoTicket> pagos = new ArrayList<>();
        BigDecimal pagado = BigDecimal.ZERO;
        for (Pago pago : pagoDAO.findByFactura(factura)) {
            if (!PagoService.PAGO_PAGADO.equals(pago.getEstado())) {
                continue;
            }
            for (PagoDetalle d : pagoDetalleDAO.findByPago(pago.getIdPago())) {
                BigDecimal monto = dinero(d.getMonto());
                pagos.add(new PagoTicket(d.getTipoPago(), monto, d.getReferenciaExterna()));
                pagado = pagado.add(monto);
            }
        }
        String cliente = factura.getCliente() == null || factura.getCliente().isBlank()
                ? "Consumidor final" : factura.getCliente();
        return new Ticket(
                factura.getIdFactura().toString(),
                factura.getFechaFacturacion() != null
                        ? Instant.ofEpochMilli(factura.getFechaFacturacion().getTime()) : null,
                cliente,
                factura.getIdCaja() != null ? factura.getIdCaja().getNombre() : null,
                nombreCajero(factura.getIdEmpleadoRol()),
                factura.getEstado(),
                lineas, pagos,
                dinero(total), dinero(pagado), dinero(pagado.subtract(total).max(BigDecimal.ZERO)));
    }

    private String nombreCajero(EmpleadoRol er) {
        if (er == null || er.getIdEmpleado() == null) {
            return null;
        }
        Empleado e = empleadoDAO.buscarPorId(er.getIdEmpleado());
        if (e == null) {
            return null;
        }
        String nombre = (e.getNombre() + " " + (e.getApellido() == null ? "" : e.getApellido())).trim();
        return er.getIdRol() != null ? nombre + " (" + er.getIdRol().getNombre() + ")" : nombre;
    }

    static List<LineaTicket> agrupar(List<FacturaOrdenProducto> productos) {
        Map<String, LineaTicket> grupos = new LinkedHashMap<>();
        for (FacturaOrdenProducto fop : productos) {
            String nombre = fop.getIdOrdenProducto() != null && fop.getIdOrdenProducto().getIdProducto() != null
                    ? fop.getIdOrdenProducto().getIdProducto().getNombre() : "Producto";
            BigDecimal precio = dinero(fop.getPrecio());
            String clave = nombre + "|" + precio;
            LineaTicket previa = grupos.get(clave);
            int cantidad = previa == null ? 1 : previa.cantidad() + 1;
            grupos.put(clave, new LineaTicket(cantidad, nombre, precio, precio.multiply(BigDecimal.valueOf(cantidad))));
        }
        return new ArrayList<>(grupos.values());
    }

    private static BigDecimal dinero(BigDecimal valor) {
        return (valor == null ? BigDecimal.ZERO : valor).setScale(2, RoundingMode.HALF_UP);
    }
}
