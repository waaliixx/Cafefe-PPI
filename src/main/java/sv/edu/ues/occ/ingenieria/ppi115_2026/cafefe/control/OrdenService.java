/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package sv.edu.ues.occ.ingenieria.ppi115_2026.cafefe.control;

import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import sv.edu.ues.occ.ingenieria.ppi115_2026.cafefe.control.OrdenException.Codigo;
import sv.edu.ues.occ.ingenieria.ppi115_2026.cafefe.entity.EmpleadoRol;
import sv.edu.ues.occ.ingenieria.ppi115_2026.cafefe.entity.Orden;
import sv.edu.ues.occ.ingenieria.ppi115_2026.cafefe.entity.OrdenProducto;
import sv.edu.ues.occ.ingenieria.ppi115_2026.cafefe.entity.Producto;

/**
 *
 * @author walixx
 */
/**
 * Creación de órdenes (HU2). La orden y todas sus líneas se guardan en una sola
 * transacción: si una línea es inválida no queda nada a medias.
 */
@Stateless
public class OrdenService {
    private static final long serialVersionUID = 1L;

    public static final String ROL_CAMARERO = "Camarero";
    public static final int MAX_CLIENTE = 155;
    public static final int MAX_CANTIDAD = 50;
    public static final int MAX_LINEAS = 100;

    /** Un producto pedido; {@code cantidad} genera esa cantidad de líneas, cada una con su precio. */
    public record LineaOrden(UUID idProducto, int cantidad, String observaciones) {
    }

    public record ResultadoOrden(Orden orden, List<OrdenProducto> lineas) {
    }

    @PersistenceContext(unitName = "CafefePU")
    private EntityManager em;

    public OrdenService() {
    }

    /**
     * @param idEmpleadoRol camarero que toma la orden; {@code null} cuando la hace el
     * cliente desde el mesero virtual de la aplicación web
     * @param cliente nombre del cliente; se agrega a la observación de cada producto
     * porque la orden no tiene campo de descripción
     * @param lineas productos pedidos
     */
    public ResultadoOrden crear(UUID idEmpleadoRol, String cliente, List<LineaOrden> lineas) {
        if (cliente == null || cliente.isBlank()) {
            throw new OrdenException(Codigo.DATOS_INVALIDOS, "El nombre del cliente es obligatorio.");
        }
        String nombre = cliente.trim();
        if (nombre.length() > MAX_CLIENTE) {
            throw new OrdenException(Codigo.DATOS_INVALIDOS,
                    "El nombre del cliente no puede superar " + MAX_CLIENTE + " caracteres.");
        }
        if (lineas == null || lineas.isEmpty()) {
            throw new OrdenException(Codigo.DATOS_INVALIDOS, "La orden debe tener al menos un producto.");
        }
        int totalLineas = 0;
        for (LineaOrden l : lineas) {
            if (l == null || l.idProducto() == null) {
                throw new OrdenException(Codigo.DATOS_INVALIDOS, "Cada producto requiere su identificador.");
            }
            if (l.cantidad() < 1 || l.cantidad() > MAX_CANTIDAD) {
                throw new OrdenException(Codigo.DATOS_INVALIDOS,
                        "La cantidad de cada producto debe estar entre 1 y " + MAX_CANTIDAD + ".");
            }
            totalLineas += l.cantidad();
        }
        if (totalLineas > MAX_LINEAS) {
            throw new OrdenException(Codigo.DATOS_INVALIDOS,
                    "Una orden no puede superar " + MAX_LINEAS + " productos.");
        }

        EmpleadoRol camarero = idEmpleadoRol == null ? null : validarCamarero(idEmpleadoRol);

        Orden orden = new Orden(UUID.randomUUID());
        orden.setFechaCreacion(new Date());
        orden.setIdEmpleadoRol(camarero);
        em.persist(orden);

        List<OrdenProducto> creadas = new ArrayList<>();
        for (LineaOrden l : lineas) {
            Producto p = em.find(Producto.class, l.idProducto());
            if (p == null) {
                throw new OrdenException(Codigo.NO_ENCONTRADO, "El producto " + l.idProducto() + " no existe.");
            }
            if (!Boolean.TRUE.equals(p.getActivo())) {
                throw new OrdenException(Codigo.PRODUCTO_INACTIVO,
                        "El producto " + p.getNombre() + " no está disponible.");
            }
            if (p.getPrecioSugerido() == null) {
                throw new OrdenException(Codigo.DATOS_INVALIDOS,
                        "El producto " + p.getNombre() + " no tiene precio.");
            }
            String observacion = "Cliente: " + nombre
                    + (l.observaciones() == null || l.observaciones().isBlank() ? "" : " | " + l.observaciones().trim());
            for (int i = 0; i < l.cantidad(); i++) {
                OrdenProducto op = new OrdenProducto(UUID.randomUUID());
                op.setIdOrden(orden);
                op.setIdProducto(p);
                op.setPrecio(p.getPrecioSugerido()); // el precio queda fijo al momento del pedido
                op.setObservaciones(observacion);
                em.persist(op);
                creadas.add(op);
            }
        }
        return new ResultadoOrden(orden, creadas);
    }

    private EmpleadoRol validarCamarero(UUID idEmpleadoRol) {
        EmpleadoRol er = em.find(EmpleadoRol.class, idEmpleadoRol);
        if (er == null) {
            throw new OrdenException(Codigo.NO_ENCONTRADO, "El camarero no existe.");
        }
        if (!Boolean.TRUE.equals(er.getActivo()) || er.getIdRol() == null
                || !ROL_CAMARERO.equalsIgnoreCase(er.getIdRol().getNombre())) {
            throw new OrdenException(Codigo.NO_AUTORIZADO, "Solo un empleado activo con rol Camarero puede tomar órdenes.");
        }
        return er;
    }
}
