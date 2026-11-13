/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package sv.edu.ues.occ.ingenieria.ppi115_2026.cafefe.control;

import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.LockModeType;
import jakarta.persistence.PersistenceContext;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import sv.edu.ues.occ.ingenieria.ppi115_2026.cafefe.control.CobroException.Codigo;
import sv.edu.ues.occ.ingenieria.ppi115_2026.cafefe.entity.Caja;
import sv.edu.ues.occ.ingenieria.ppi115_2026.cafefe.entity.Empleado;
import sv.edu.ues.occ.ingenieria.ppi115_2026.cafefe.entity.EmpleadoRol;
import sv.edu.ues.occ.ingenieria.ppi115_2026.cafefe.entity.Factura;
import sv.edu.ues.occ.ingenieria.ppi115_2026.cafefe.entity.FacturaOrdenProducto;
import sv.edu.ues.occ.ingenieria.ppi115_2026.cafefe.entity.Orden;
import sv.edu.ues.occ.ingenieria.ppi115_2026.cafefe.entity.OrdenProducto;
import sv.edu.ues.occ.ingenieria.ppi115_2026.cafefe.entity.Pago;
import sv.edu.ues.occ.ingenieria.ppi115_2026.cafefe.entity.PagoDetalle;

/**
 *
 * @author walixx
 */
@Stateless
public class PagoService implements Serializable {
    
    private static final long serialVersionUID = 1L;

    public static final String ROL_CAJERO = "Cajero";
    public static final String FACTURA_PAGADA = "Pagada";
    public static final String PAGO_PAGADO = "Pagado";
    public static final int MAX_CLIENTE = 155;
    public static final int MAX_REFERENCIA = 200;

    public static final String EFECTIVO = "Efectivo";
    public static final String TARJETA = "Tarjeta";
    public static final String QR = "QR";
    
    /** Formas de pago admitidas, indexadas por su nombre en minúsculas. */
    private static final Map<String, String> TIPOS_PAGO = Map.of(
            "efectivo", EFECTIVO, "tarjeta", TARJETA, "qr", QR);

    /** Una forma de pago aplicada al cobro. */
    public record LineaPago(String tipoPago, BigDecimal monto, String referenciaExterna) {
    }

    /** Resumen de lo registrado. */
    public record ResultadoCobro(Factura factura, Pago pago,
            List<FacturaOrdenProducto> lineas, List<PagoDetalle> detalles,
            BigDecimal total, BigDecimal pagado, BigDecimal vuelto) {
    }
    
    @PersistenceContext(unitName = "CafefePU")
    private EntityManager em;
    @Inject
    private OrdenProductoDAO ordenProductoDAO;

    public PagoService() {
    }
    
    /**
     * Cobra todas las líneas pendientes de una orden.
     *
     * @param idOrden orden a cobrar
     * @param idCaja caja activa donde se cobra
     * @param idEmpleadoRol cajero (empleado_rol con rol "Cajero" activo)
     * @param cliente nombre para la factura; puede ser null (clientes no registrados)
     * @param pagos formas de pago; la suma debe cubrir el total. Si sobra, el
     * excedente debe poder darse como vuelto en efectivo.
     * @throws CobroException por cualquier regla de negocio incumplida
     */
    public ResultadoCobro cobrar(UUID idOrden, UUID idCaja, UUID idEmpleadoRol,
            String cliente, List<LineaPago> pagos) {
        if (idOrden == null || idCaja == null || idEmpleadoRol == null) {
            throw new CobroException(Codigo.DATOS_INVALIDOS, "Orden, caja y cajero son obligatorios.");
        }
        if (cliente != null && cliente.length() > MAX_CLIENTE) {
            throw new CobroException(Codigo.DATOS_INVALIDOS,
                    "El nombre del cliente no puede superar " + MAX_CLIENTE + " caracteres.");
        }
        List<LineaPago> validados = normalizarPagos(pagos);

        EmpleadoRol cajero = validarCajero(idEmpleadoRol);
        Caja caja = validarCaja(idCaja);

        // Bloqueo: si dos cajeros cobran la misma orden, el segundo espera y luego
        // encuentra que ya no hay líneas por cobrar.
        Orden orden = em.find(Orden.class, idOrden, LockModeType.PESSIMISTIC_WRITE);
        if (orden == null) {
            throw new CobroException(Codigo.NO_ENCONTRADO, "La orden no existe.");
        }
        List<OrdenProducto> pendientes = ordenProductoDAO.findNoFacturadasByOrden(idOrden);
        if (pendientes == null || pendientes.isEmpty()) {
            throw new CobroException(Codigo.ORDEN_YA_PAGADA, "La orden no tiene productos pendientes de pago.");
        }

        BigDecimal total = BigDecimal.ZERO;
        for (OrdenProducto op : pendientes) {
            if (op.getPrecio() == null) {
                throw new CobroException(Codigo.DATOS_INVALIDOS,
                        "Un producto de la orden no tiene precio; no se puede cobrar.");
            }
            total = total.add(op.getPrecio());
        }

        BigDecimal pagado = BigDecimal.ZERO;
        BigDecimal noEfectivo = BigDecimal.ZERO;
        for (LineaPago lp : validados) {
            pagado = pagado.add(lp.monto());
            if (!EFECTIVO.equals(lp.tipoPago())) {
                noEfectivo = noEfectivo.add(lp.monto());
            }
        }
        if (pagado.compareTo(total) < 0) {
            throw new CobroException(Codigo.SALDO_INSUFICIENTE,
                    "Faltan " + dinero(total.subtract(pagado)) + " para completar el total de "
                    + dinero(total) + ".");
        }
        // vuelto = pagado - total. Solo puede salir del efectivo, o sea que lo
        // pagado sin efectivo no puede pasar del total.
        if (noEfectivo.compareTo(total) > 0) {
            throw new CobroException(Codigo.EXCESO_NO_EFECTIVO,
                    "Lo pagado con tarjeta o QR no puede superar el total de " + dinero(total) + ".");
        }
        BigDecimal vuelto = pagado.subtract(total);

        Factura factura = new Factura(UUID.randomUUID());
        factura.setCliente(cliente == null || cliente.isBlank() ? null : cliente.trim());
        factura.setFechaFacturacion(new Date());
        factura.setEstado(FACTURA_PAGADA);
        factura.setIdCaja(caja);
        factura.setIdEmpleadoRol(cajero);
        em.persist(factura);

        List<FacturaOrdenProducto> lineas = new ArrayList<>();
        for (OrdenProducto op : pendientes) {
            FacturaOrdenProducto fop = new FacturaOrdenProducto(UUID.randomUUID());
            fop.setIdFactura(factura);
            fop.setIdOrdenProducto(op);
            fop.setPrecio(op.getPrecio()); // se congela el precio al momento del cobro
            em.persist(fop);
            lineas.add(fop);
        }

        Pago pago = new Pago(UUID.randomUUID());
        pago.setIdFactura(factura);
        pago.setEstado(PAGO_PAGADO);
        pago.setObservaciones("Vuelto: " + dinero(vuelto));
        em.persist(pago);

        // Se guarda lo entregado en cada forma de pago; el vuelto queda en el Pago.
        List<PagoDetalle> detalles = new ArrayList<>();
        for (LineaPago lp : validados) {
            PagoDetalle d = new PagoDetalle(UUID.randomUUID());
            d.setIdPago(pago);
            d.setTipoPago(lp.tipoPago());
            d.setMonto(lp.monto());
            d.setReferenciaExterna(lp.referenciaExterna());
            em.persist(d);
            detalles.add(d);
        }

        return new ResultadoCobro(factura, pago, lineas, detalles, total, pagado, vuelto);
    }
    
    /** Valida y normaliza las formas de pago (nombre canónico, montos positivos de a lo mucho 2 decimales). */
    List<LineaPago> normalizarPagos(List<LineaPago> pagos) {
        if (pagos == null || pagos.isEmpty()) {
            throw new CobroException(Codigo.DATOS_INVALIDOS, "Debe indicar al menos una forma de pago.");
        }
        List<LineaPago> resultado = new ArrayList<>();
        for (LineaPago lp : pagos) {
            if (lp == null || lp.tipoPago() == null || lp.monto() == null) {
                throw new CobroException(Codigo.DATOS_INVALIDOS, "Cada pago requiere tipo y monto.");
            }
            String tipo = TIPOS_PAGO.get(lp.tipoPago().trim().toLowerCase(Locale.ROOT));
            if (tipo == null) {
                throw new CobroException(Codigo.DATOS_INVALIDOS,
                        "Forma de pago no admitida: " + lp.tipoPago() + " (use Efectivo, Tarjeta o QR).");
            }
            if (lp.monto().signum() <= 0) {
                throw new CobroException(Codigo.DATOS_INVALIDOS, "El monto de cada pago debe ser mayor que cero.");
            }
            if (lp.monto().stripTrailingZeros().scale() > 2) {
                throw new CobroException(Codigo.DATOS_INVALIDOS, "El monto admite como máximo 2 decimales.");
            }
            String ref = lp.referenciaExterna() == null || lp.referenciaExterna().isBlank()
                    ? null : lp.referenciaExterna().trim();
            if (ref != null && ref.length() > MAX_REFERENCIA) {
                throw new CobroException(Codigo.DATOS_INVALIDOS,
                        "La referencia no puede superar " + MAX_REFERENCIA + " caracteres.");
            }
            resultado.add(new LineaPago(tipo, dinero(lp.monto()), ref));
        }
        return resultado;
    }
    
    private static BigDecimal dinero(BigDecimal valor) {
        return valor.setScale(2, RoundingMode.HALF_UP);
    }

    private EmpleadoRol validarCajero(UUID idEmpleadoRol) {
        EmpleadoRol er = em.find(EmpleadoRol.class, idEmpleadoRol);
        if (er == null) {
            throw new CobroException(Codigo.NO_ENCONTRADO, "El cajero no existe.");
        }
        if (!Boolean.TRUE.equals(er.getActivo()) || er.getIdRol() == null
                || !ROL_CAJERO.equalsIgnoreCase(er.getIdRol().getNombre())) {
            throw new CobroException(Codigo.NO_AUTORIZADO, "Solo un empleado activo con rol Cajero puede cobrar.");
        }
        Empleado emp = er.getIdEmpleado() == null ? null : em.find(Empleado.class, er.getIdEmpleado());
        if (emp == null || Boolean.FALSE.equals(emp.getActivo())) {
            throw new CobroException(Codigo.NO_AUTORIZADO, "El cajero está inactivo.");
        }
        return er;
    }

    private Caja validarCaja(UUID idCaja) {
        Caja caja = em.find(Caja.class, idCaja);
        if (caja == null) {
            throw new CobroException(Codigo.NO_ENCONTRADO, "La caja no existe.");
        }
        if (!Boolean.TRUE.equals(caja.getActivo())) {
            throw new CobroException(Codigo.NO_AUTORIZADO, "La caja está inactiva.");
        }
        return caja;
    }
}
