package sv.edu.ues.occ.ingenieria.ppi115_2026.cafefe.control;

import jakarta.ejb.ApplicationException;
import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import sv.edu.ues.occ.ingenieria.ppi115_2026.cafefe.entity.Empleado;
import sv.edu.ues.occ.ingenieria.ppi115_2026.cafefe.entity.EmpleadoRol;
import sv.edu.ues.occ.ingenieria.ppi115_2026.cafefe.entity.Orden;
import sv.edu.ues.occ.ingenieria.ppi115_2026.cafefe.entity.OrdenProducto;
import sv.edu.ues.occ.ingenieria.ppi115_2026.cafefe.entity.Producto;

/** Órdenes para la API: crear la orden del cliente y consultar las pendientes de pago. */
@Stateless
public class OrdenService {

    public static final int MAX_CLIENTE = 100;
    public static final int MAX_ITEMS = 50;
    public static final int MAX_CANTIDAD = 20;

    @PersistenceContext(unitName = "CafefePU")
    private EntityManager em;
    @Inject
    private OrdenDAO ordenDAO;
    @Inject
    private OrdenProductoDAO ordenProductoDAO;
    @Inject
    private EmpleadoDAO empleadoDAO;

    /** Dato inválido del cuerpo → 422 con la cabecera Wrong-Param. */
    @ApplicationException(rollback = true)
    public static class SolicitudInvalidaException extends RuntimeException {

        private static final long serialVersionUID = 1L;
        private final String wrongParam;

        public SolicitudInvalidaException(String wrongParam, String mensaje) {
            super(mensaje);
            this.wrongParam = wrongParam;
        }

        public String getWrongParam() {
            return wrongParam;
        }
    }

    /** Falta el empleado "mesero virtual" → 503. */
    @ApplicationException(rollback = true)
    public static class ConfiguracionIncompletaException extends RuntimeException {

        private static final long serialVersionUID = 1L;

        public ConfiguracionIncompletaException(String mensaje) {
            super(mensaje);
        }
    }

    /** Cuerpo de POST /orden. */
    public static class SolicitudOrden {

        public String cliente;
        public List<ItemSolicitado> items;
    }

    public static class ItemSolicitado {

        public String idProducto;
        public Integer cantidad;
        public String observaciones;
    }

    private record Linea(int indice, UUID idProducto, int cantidad, String nota) {
    }

    // ---------------------------------------------------------------- HU2: crear la orden

    public Map<String, Object> crearOrden(SolicitudOrden solicitud) {
        if (solicitud == null) {
            throw new SolicitudInvalidaException("registro", "El cuerpo de la orden es obligatorio.");
        }
        String cliente = validarCliente(solicitud.cliente);
        List<Linea> lineas = validarLineas(solicitud.items);
        EmpleadoRol mesero = buscarMeseroVirtual();

        // Se valida todo antes de guardar: productos que existan, activos y con precio.
        Map<UUID, Producto> productos = new HashMap<>();
        for (Linea l : lineas) {
            if (productos.containsKey(l.idProducto())) {
                continue;
            }
            Producto p = em.find(Producto.class, l.idProducto());
            String campo = "items[" + l.indice() + "].idProducto";
            if (p == null || !Boolean.TRUE.equals(p.getActivo())) {
                throw new SolicitudInvalidaException(campo, "El producto no existe o está inactivo.");
            }
            if (p.getPrecioSugerido() == null) {
                throw new SolicitudInvalidaException(campo, "El producto no tiene precio.");
            }
            productos.put(l.idProducto(), p);
        }

        Date ahora = new Date();
        Orden orden = new Orden(UUID.randomUUID());
        orden.setFechaCreacion(ahora);
        orden.setIdEmpleadoRol(mesero);
        em.persist(orden);

        BigDecimal total = BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
        List<Map<String, Object>> lineasDto = new ArrayList<>();
        for (Linea l : lineas) {
            Producto p = productos.get(l.idProducto());
            BigDecimal precio = p.getPrecioSugerido().setScale(2, RoundingMode.HALF_UP);
            String observaciones = ReglasOrden.formatearObservaciones(cliente, l.nota());
            // Una fila por unidad: 2 cafés son 2 líneas, cada una cobrable por separado.
            for (int u = 0; u < l.cantidad(); u++) {
                OrdenProducto op = new OrdenProducto(UUID.randomUUID());
                op.setIdOrden(orden);
                op.setIdProducto(p);
                op.setPrecio(precio);
                op.setObservaciones(observaciones);
                em.persist(op);
                total = total.add(precio);
                Map<String, Object> dto = new LinkedHashMap<>();
                dto.put("idOrdenProducto", op.getIdOrdenProducto().toString());
                dto.put("idProducto", p.getIdProducto().toString());
                dto.put("producto", p.getNombre());
                dto.put("precio", precio);
                dto.put("observaciones", observaciones);
                lineasDto.add(dto);
            }
        }
        Map<String, Object> respuesta = new LinkedHashMap<>();
        respuesta.put("idOrden", orden.getIdOrden().toString());
        respuesta.put("fechaCreacion", Instant.ofEpochMilli(ahora.getTime()).toString());
        respuesta.put("cliente", cliente);
        respuesta.put("total", total);
        respuesta.put("lineas", lineasDto);
        return respuesta;
    }

    private static String validarCliente(String valor) {
        String cliente = valor == null ? "" : valor.trim();
        if (cliente.isEmpty()) {
            throw new SolicitudInvalidaException("cliente", "El nombre del cliente es obligatorio.");
        }
        if (cliente.length() > MAX_CLIENTE) {
            throw new SolicitudInvalidaException("cliente",
                    "El nombre del cliente no puede superar " + MAX_CLIENTE + " caracteres.");
        }
        return cliente;
    }

    private static List<Linea> validarLineas(List<ItemSolicitado> items) {
        if (items == null || items.isEmpty()) {
            throw new SolicitudInvalidaException("items", "La orden debe incluir al menos un producto.");
        }
        if (items.size() > MAX_ITEMS) {
            throw new SolicitudInvalidaException("items", "La orden no puede superar " + MAX_ITEMS + " productos.");
        }
        List<Linea> lineas = new ArrayList<>();
        for (int i = 0; i < items.size(); i++) {
            ItemSolicitado it = items.get(i);
            UUID id = it == null ? null : aUuid(it.idProducto);
            if (id == null) {
                throw new SolicitudInvalidaException("items[" + i + "].idProducto",
                        "El producto es obligatorio o su identificador no es válido.");
            }
            int cantidad = it.cantidad == null ? 1 : it.cantidad;
            if (cantidad < 1 || cantidad > MAX_CANTIDAD) {
                throw new SolicitudInvalidaException("items[" + i + "].cantidad",
                        "La cantidad debe estar entre 1 y " + MAX_CANTIDAD + ".");
            }
            lineas.add(new Linea(i, id, cantidad, it.observaciones));
        }
        return lineas;
    }

    /** Empleado activo cuyo rol (activo) sea "Tienda en línea" o "Mesero Virtual". */
    private EmpleadoRol buscarMeseroVirtual() {
        List<EmpleadoRol> activos = em.createQuery(
                "SELECT er FROM EmpleadoRol er WHERE er.activo = :activo", EmpleadoRol.class)
                .setParameter("activo", Boolean.TRUE)
                .getResultList();
        for (EmpleadoRol er : activos) {
            if (!ReglasOrden.esRolEnLinea(er.getIdRol()) || !Boolean.TRUE.equals(er.getIdRol().getActivo())) {
                continue;
            }
            Empleado e = er.getIdEmpleado() == null ? null : em.find(Empleado.class, er.getIdEmpleado());
            if (e != null && Boolean.TRUE.equals(e.getActivo())) {
                return er;
            }
        }
        throw new ConfiguracionIncompletaException(
                "Falta configurar el mesero virtual: cree el rol \"Tienda en línea\" y un empleado activo con ese rol.");
    }

    // ---------------------------------------------------------------- HU3 y HU4: pendientes

    /** Órdenes con productos sin cobrar, cada una con SOLO sus productos pendientes. */
    public List<Map<String, Object>> pendientes() {
        Map<UUID, String> nombres = new HashMap<>();
        List<Map<String, Object>> resultado = new ArrayList<>();
        for (Orden o : ordenDAO.findPendientesDePago()) {
            resultado.add(ordenDto(o, ordenProductoDAO.findNoFacturadasByOrden(o.getIdOrden()), nombres));
        }
        return resultado;
    }

    /** Una orden con sus productos pendientes, o null si no existe. */
    public Map<String, Object> pendiente(UUID idOrden) {
        Orden orden = idOrden == null ? null : ordenDAO.buscarPorId(idOrden);
        if (orden == null) {
            return null;
        }
        return ordenDto(orden, ordenProductoDAO.findNoFacturadasByOrden(idOrden), new HashMap<>());
    }

    /** Nombre del cliente que quedó guardado en las observaciones de la orden, o null. */
    public String clienteDeOrden(UUID idOrden) {
        for (OrdenProducto op : ordenProductoDAO.findByOrden(idOrden)) {
            String cliente = ReglasOrden.clienteDesde(op.getObservaciones());
            if (cliente != null) {
                return cliente;
            }
        }
        return null;
    }

    private Map<String, Object> ordenDto(Orden o, List<OrdenProducto> lineas, Map<UUID, String> nombres) {
        BigDecimal total = BigDecimal.ZERO;
        String cliente = null;
        List<Map<String, Object>> productos = new ArrayList<>();
        for (OrdenProducto op : lineas) {
            BigDecimal precio = op.getPrecio() == null ? BigDecimal.ZERO : op.getPrecio().setScale(2, RoundingMode.HALF_UP);
            total = total.add(precio);
            if (cliente == null) {
                cliente = ReglasOrden.clienteDesde(op.getObservaciones());
            }
            Map<String, Object> linea = new LinkedHashMap<>();
            linea.put("idOrdenProducto", op.getIdOrdenProducto().toString());
            linea.put("idProducto", op.getIdProducto() != null ? op.getIdProducto().getIdProducto().toString() : null);
            linea.put("producto", op.getIdProducto() != null ? op.getIdProducto().getNombre() : null);
            linea.put("precio", precio);
            linea.put("nota", ReglasOrden.notaDe(op.getObservaciones()));
            productos.add(linea);
        }
        Map<String, Object> dto = new LinkedHashMap<>();
        dto.put("idOrden", o.getIdOrden().toString());
        dto.put("fechaCreacion", o.getFechaCreacion() != null
                ? Instant.ofEpochMilli(o.getFechaCreacion().getTime()).toString() : null);
        dto.put("cliente", cliente);
        dto.put("tomadaPor", tomadaPor(o.getIdEmpleadoRol(), nombres));
        dto.put("total", total.setScale(2, RoundingMode.HALF_UP));
        dto.put("productos", productos);
        return dto;
    }

    // "Mesero Virtual (Tienda en línea)" o "Javier Herrera (Camarero)".
    private String tomadaPor(EmpleadoRol er, Map<UUID, String> nombres) {
        if (er == null || er.getIdEmpleado() == null) {
            return null;
        }
        String nombre = nombres.computeIfAbsent(er.getIdEmpleado(), id -> {
            Empleado e = empleadoDAO.buscarPorId(id);
            return e == null ? "(empleado no encontrado)"
                    : (e.getNombre() + " " + (e.getApellido() == null ? "" : e.getApellido())).trim();
        });
        return er.getIdRol() != null ? nombre + " (" + er.getIdRol().getNombre() + ")" : nombre;
    }

    static UUID aUuid(String texto) {
        if (texto == null || texto.isBlank()) {
            return null;
        }
        try {
            return UUID.fromString(texto.trim());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}
