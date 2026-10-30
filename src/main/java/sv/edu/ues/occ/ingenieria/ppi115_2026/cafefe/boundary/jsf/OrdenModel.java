package sv.edu.ues.occ.ingenieria.ppi115_2026.cafefe.boundary.jsf;

import jakarta.faces.application.FacesMessage;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import sv.edu.ues.occ.ingenieria.ppi115_2026.cafefe.control.AbstraccionDAO;
import sv.edu.ues.occ.ingenieria.ppi115_2026.cafefe.control.DescuentoProductoDAO;
import sv.edu.ues.occ.ingenieria.ppi115_2026.cafefe.control.EmpleadoDAO;
import sv.edu.ues.occ.ingenieria.ppi115_2026.cafefe.control.EmpleadoRolDAO;
import sv.edu.ues.occ.ingenieria.ppi115_2026.cafefe.control.OrdenDAO;
import sv.edu.ues.occ.ingenieria.ppi115_2026.cafefe.control.OrdenProductoDAO;
import sv.edu.ues.occ.ingenieria.ppi115_2026.cafefe.control.ProductoDAO;
import sv.edu.ues.occ.ingenieria.ppi115_2026.cafefe.entity.DescuentoProducto;
import sv.edu.ues.occ.ingenieria.ppi115_2026.cafefe.entity.Empleado;
import sv.edu.ues.occ.ingenieria.ppi115_2026.cafefe.entity.EmpleadoRol;
import sv.edu.ues.occ.ingenieria.ppi115_2026.cafefe.entity.Orden;
import sv.edu.ues.occ.ingenieria.ppi115_2026.cafefe.entity.OrdenProducto;
import sv.edu.ues.occ.ingenieria.ppi115_2026.cafefe.entity.Producto;

/**
 *
 * @author edenrvm
 */
@Named("ordenModel")
@ViewScoped
public class OrdenModel extends AbstraccionModel<Orden> implements Serializable {

    private static final long serialVersionUID = 1L;
    private static final Logger LOG = Logger.getLogger(OrdenModel.class.getName());
    private static final String ROL_CAMARERO = "Camarero";
    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    @Inject
    private OrdenDAO ordenDAO;
    @Inject
    private OrdenProductoDAO ordenProductoDAO;
    @Inject
    private EmpleadoRolDAO empleadoRolDAO;
    @Inject
    private EmpleadoDAO empleadoDAO;
    @Inject
    private ProductoDAO productoDAO;
    @Inject
    private DescuentoProductoDAO descuentoProductoDAO;

    private Orden seleccionado;
    private Map<UUID, Empleado> empleados = new HashMap<>();

    // Generalidades
    private List<EmpleadoRol> camareros = new ArrayList<>();
    private String idEmpleadoRolElegido;

    // Productos 
    private List<OrdenProducto> productosOrden = new ArrayList<>();
    private OrdenProducto productoSeleccionado;
    private OrdenProducto nuevoProducto;
    private boolean formProducto;
    private String idProductoElegido;
    private List<Producto> productosDisponibles = new ArrayList<>();

    // Descuentos
    private boolean formDescuento;
    private OrdenProducto lineaConDescuento;
    private String idDescuentoElegido;
    private List<DescuentoProducto> descuentosDisponibles = new ArrayList<>();

    @Override
    protected AbstraccionDAO<Orden> getDAO() {
        return ordenDAO;
    }

    @Override
    protected Orden nuevoRegistro() {
        Orden nueva = new Orden(UUID.randomUUID());
        nueva.setFechaCreacion(new Date());
        return nueva;
    }

    @Override
    protected Object getIdRegistro(Orden entidad) {
        return entidad != null ? entidad.getIdOrden() : null;
    }

    @Override
    public void cargarRegistros() {
        super.cargarRegistros();
        getRegistros().sort(Comparator.comparing(Orden::getFechaCreacion,
                Comparator.nullsLast(Comparator.<Date>reverseOrder())));
        try {
            empleados = new HashMap<>();
            for (Empleado e : empleadoDAO.findAll()) {
                empleados.put(e.getIdEmpleado(), e);
            }
        } catch (RuntimeException e) {
            LOG.log(Level.SEVERE, "No se pudieron cargar los empleados", e);
            mostrarMensaje(FacesMessage.SEVERITY_ERROR, "No se pudieron cargar los empleados.");
        }
    }

    //Generalidades
    @Override
    protected boolean validarRegistro() {
        if (isEditando()) {
            mostrarMensaje(FacesMessage.SEVERITY_WARN,
                    "Una orden ya creada no se puede modificar; solo se le pueden agregar productos.");
            return false;
        }
        Orden o = getRegistro();
        if (o == null) {
            return false;
        }
        UUID idEr = aUuid(idEmpleadoRolElegido);
        if (idEr == null) {
            mostrarMensaje(FacesMessage.SEVERITY_WARN, "Seleccione el camarero que toma la orden.");
            return false;
        }
        EmpleadoRol er = empleadoRolDAO.buscarPorId(idEr);
        if (er == null || er.getIdRol() == null
                || !ROL_CAMARERO.equalsIgnoreCase(er.getIdRol().getNombre())) {
            mostrarMensaje(FacesMessage.SEVERITY_ERROR, "Solo un empleado con rol Camarero puede tomar órdenes.");
            return false;
        }
        Empleado emp = empleados.get(er.getIdEmpleado());
        if (!Boolean.TRUE.equals(er.getActivo()) || emp == null || Boolean.FALSE.equals(emp.getActivo())) {
            mostrarMensaje(FacesMessage.SEVERITY_ERROR, "El camarero elegido está inactivo.");
            return false;
        }
        o.setIdEmpleadoRol(er);
        if (o.getFechaCreacion() == null) {
            o.setFechaCreacion(new Date());
        }
        return true;
    }

    @Override
    public void nuevo() {
        seleccionado = null;
        reiniciarDetalles();
        super.nuevo();
        idEmpleadoRolElegido = null;
        cargarCamareros();
    }

    @Override
    public void editar(Orden entidad) {
        super.editar(entidad);
        if (isEditando()) {
            reiniciarDetalles();
            EmpleadoRol actual = getRegistro().getIdEmpleadoRol();
            idEmpleadoRolElegido = actual != null && actual.getIdEmpleadoRol() != null
                    ? actual.getIdEmpleadoRol().toString() : null;
            cargarCamareros();
            if (actual != null && !camareros.contains(actual)) {
                camareros.add(actual);
            }
            cargarProductosOrden();
        }
    }

    @Override
    public void cancelar() {
        super.cancelar();
        seleccionado = null;
        reiniciarDetalles();
    }

    @Override
    public void eliminar(Orden entidad) {
        if (entidad != null && entidad.getIdOrden() != null) {
            try {
                for (OrdenProducto op : ordenProductoDAO.findByOrden(entidad.getIdOrden())) {
                    if (!ordenProductoDAO.eliminar(op)) {
                        mostrarMensaje(FacesMessage.SEVERITY_ERROR, "No se pudieron quitar los productos de la orden.");
                        return;
                    }
                }
            } catch (RuntimeException e) {
                LOG.log(Level.SEVERE, "Error al quitar los productos de la orden", e);
                mostrarMensaje(FacesMessage.SEVERITY_ERROR,
                        "No se puede eliminar: la orden tiene productos asociados a una factura.");
                return;
            }
        }
        super.eliminar(entidad);
        seleccionado = null;
    }

    private void cargarCamareros() {
        camareros = new ArrayList<>();
        try {
            for (EmpleadoRol er : empleadoRolDAO.findActivosPorNombreRol(ROL_CAMARERO)) {
                Empleado e = empleados.get(er.getIdEmpleado());
                if (e != null && !Boolean.FALSE.equals(e.getActivo())) {
                    camareros.add(er);
                }
            }
            camareros.sort(Comparator.comparing(this::etiquetaCamarero, String.CASE_INSENSITIVE_ORDER));
        } catch (RuntimeException e) {
            LOG.log(Level.SEVERE, "No se pudieron cargar los camareros", e);
            mostrarMensaje(FacesMessage.SEVERITY_ERROR, "No se pudieron cargar los camareros.");
        }
    }

    public String etiquetaCamarero(EmpleadoRol er) {
        if (er == null) {
            return "";
        }
        String rol = er.getIdRol() != null && er.getIdRol().getNombre() != null ? er.getIdRol().getNombre() : "";
        return nombreEmpleado(er.getIdEmpleado()) + " - " + rol;
    }

    public String empleoYNombre(Orden orden) {
        if (orden == null || orden.getIdEmpleadoRol() == null) {
            return "";
        }
        EmpleadoRol er = orden.getIdEmpleadoRol();
        String empleo = er.getIdRol() != null && er.getIdRol().getNombre() != null
                ? er.getIdRol().getNombre() : "";
        String nombre = nombreEmpleado(er.getIdEmpleado());
        return empleo.isEmpty() ? nombre : empleo + " - " + nombre;
    }

    private String nombreEmpleado(UUID idEmpleado) {
        Empleado e = empleados.get(idEmpleado);
        if (e == null) {
            return "(empleado no encontrado)";
        }
        String nombre = e.getNombre() == null ? "" : e.getNombre();
        String apellido = e.getApellido() == null ? "" : e.getApellido();
        return (nombre + " " + apellido).trim();
    }

    public String nombreCamarero(Orden orden) {
        if (orden == null || orden.getIdEmpleadoRol() == null) {
            return "";
        }
        return nombreEmpleado(orden.getIdEmpleadoRol().getIdEmpleado());
    }

    private void reiniciarDetalles() {
        productosOrden = new ArrayList<>();
        productoSeleccionado = null;
        cerrarFormProducto();
        cerrarFormDescuento();
    }

    private void cargarProductosOrden() {
        try {
            productosOrden = new ArrayList<>(ordenProductoDAO.findByOrden(getRegistro().getIdOrden()));
        } catch (RuntimeException e) {
            LOG.log(Level.SEVERE, "No se pudieron cargar los productos de la orden", e);
            mostrarMensaje(FacesMessage.SEVERITY_ERROR, "No se pudieron cargar los productos de la orden.");
        }
    }

    private boolean listoParaDetalles() {
        if (!isEditando() || getRegistro() == null) {
            mostrarMensaje(FacesMessage.SEVERITY_WARN, "Guarde primero la orden para agregarle productos.");
            return false;
        }
        return true;
    }

    public boolean isSubFormularioAbierto() {
        return formProducto || formDescuento;
    }

    public void abrirFormProducto() {
        if (!listoParaDetalles()) {
            return;
        }
        nuevoProducto = new OrdenProducto(UUID.randomUUID());
        idProductoElegido = null;
        try {
            productosDisponibles = new ArrayList<>(productoDAO.findAll());
            productosDisponibles.sort(Comparator.comparing(Producto::getNombre,
                    Comparator.nullsLast(String.CASE_INSENSITIVE_ORDER)));
        } catch (RuntimeException e) {
            LOG.log(Level.SEVERE, "No se pudieron cargar los productos", e);
            mostrarMensaje(FacesMessage.SEVERITY_ERROR, "No se pudieron cargar los productos.");
            return;
        }
        formProducto = true;
    }

    public void cerrarFormProducto() {
        formProducto = false;
        nuevoProducto = null;
        idProductoElegido = null;
    }

    public void guardarProducto() {
        if (!listoParaDetalles() || nuevoProducto == null) {
            return;
        }
        try {
            UUID idProd = aUuid(idProductoElegido);
            if (idProd == null) {
                mostrarMensaje(FacesMessage.SEVERITY_WARN, "Seleccione un producto.");
                return;
            }
            Producto producto = productoDAO.buscarPorId(idProd);
            if (producto == null) {
                mostrarMensaje(FacesMessage.SEVERITY_ERROR, "El producto elegido ya no existe.");
                return;
            }
            if (!Boolean.TRUE.equals(producto.getActivo())) {
                mostrarMensaje(FacesMessage.SEVERITY_ERROR,
                        "No se puede agregar un producto inactivo (\"" + producto.getNombre() + "\").");
                return;
            }
            nuevoProducto.setIdOrden(getRegistro());
            nuevoProducto.setIdProducto(producto);
            nuevoProducto.setPrecio(producto.getPrecioSugerido());
            nuevoProducto.setObservaciones(normalizar(nuevoProducto.getObservaciones()));
            if (!ordenProductoDAO.crear(nuevoProducto)) {
                mostrarMensaje(FacesMessage.SEVERITY_ERROR, "No se pudo agregar el producto.");
                return;
            }
            mostrarMensaje(FacesMessage.SEVERITY_INFO, "Producto agregado a la orden.");
            cerrarFormProducto();
            cargarProductosOrden();
        } catch (RuntimeException e) {
            LOG.log(Level.SEVERE, "Error al agregar el producto", e);
            mostrarMensaje(FacesMessage.SEVERITY_ERROR, "No se pudo agregar el producto.");
        }
    }

    public void eliminarProducto() {
        if (productoSeleccionado == null || !listoParaDetalles()) {
            return;
        }
        try {
            if (!ordenProductoDAO.eliminar(productoSeleccionado)) {
                mostrarMensaje(FacesMessage.SEVERITY_ERROR, "No se pudo quitar el producto.");
                return;
            }
            mostrarMensaje(FacesMessage.SEVERITY_INFO, "Producto quitado de la orden.");
            productoSeleccionado = null;
            cargarProductosOrden();
        } catch (RuntimeException e) {
            LOG.log(Level.SEVERE, "Error al quitar el producto", e);
            mostrarMensaje(FacesMessage.SEVERITY_ERROR,
                    "No se pudo quitar el producto. Puede estar asociado a una factura.");
        }
    }

    public void abrirFormDescuento(OrdenProducto linea) {
        if (!listoParaDetalles()) {
            return;
        }
        if (linea == null || linea.getIdProducto() == null) {
            mostrarMensaje(FacesMessage.SEVERITY_WARN, "Seleccione un producto de la orden.");
            return;
        }
        lineaConDescuento = linea;
        idDescuentoElegido = null;
        try {
            descuentosDisponibles = descuentosDelProducto(linea.getIdProducto().getIdProducto());
        } catch (RuntimeException e) {
            LOG.log(Level.SEVERE, "No se pudieron cargar los descuentos", e);
            mostrarMensaje(FacesMessage.SEVERITY_ERROR, "No se pudieron cargar los descuentos del producto.");
            return;
        }
        formDescuento = true;
    }

    public void cerrarFormDescuento() {
        formDescuento = false;
        lineaConDescuento = null;
        idDescuentoElegido = null;
        descuentosDisponibles = new ArrayList<>();
    }

    public void aplicarDescuento() {
        if (!listoParaDetalles() || lineaConDescuento == null) {
            return;
        }
        try {
            UUID idDp = aUuid(idDescuentoElegido);
            if (idDp == null) {
                mostrarMensaje(FacesMessage.SEVERITY_WARN, "Seleccione un descuento.");
                return;
            }
            OrdenProducto linea = ordenProductoDAO.buscarPorId(lineaConDescuento.getIdOrdenProducto());
            DescuentoProducto dp = descuentoProductoDAO.buscarPorId(idDp);
            if (linea == null || dp == null) {
                mostrarMensaje(FacesMessage.SEVERITY_ERROR, "No se pudo recuperar el producto o el descuento.");
                return;
            }
            if (dp.getIdProducto() == null || linea.getIdProducto() == null
                    || !dp.getIdProducto().getIdProducto().equals(linea.getIdProducto().getIdProducto())) {
                mostrarMensaje(FacesMessage.SEVERITY_ERROR, "El descuento no corresponde al producto de esta línea.");
                return;
            }
            LocalDate fechaOrden = dia(getRegistro().getFechaCreacion());
            if (!aplicaEnFecha(dp, fechaOrden)) {
                mostrarMensaje(FacesMessage.SEVERITY_WARN, "El descuento \"" + dp.getIdDescuento().getNombre()
                        + "\" no se aplicó: no está vigente en la fecha de la orden ("
                        + (fechaOrden == null ? "sin fecha" : fechaOrden.format(FMT)) + ").");
                return;
            }
            Integer valor = dp.getValor();
            if (valor == null || valor <= 0 || valor > 100) {
                mostrarMensaje(FacesMessage.SEVERITY_ERROR, "El porcentaje del descuento no es válido.");
                return;
            }
            BigDecimal actual = linea.getPrecio() != null
                    ? linea.getPrecio() : linea.getIdProducto().getPrecioSugerido();
            if (actual == null) {
                mostrarMensaje(FacesMessage.SEVERITY_ERROR, "El producto no tiene precio.");
                return;
            }
            BigDecimal nuevo = actual.multiply(BigDecimal.valueOf(100L - valor))
                    .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
            BigDecimal descontado = actual.subtract(nuevo);

            String comentario = "Descuento \"" + dp.getIdDescuento().getNombre() + "\" " + valor
                    + "%: -$" + descontado.toPlainString()
                    + " (de $" + actual.toPlainString() + " a $" + nuevo.toPlainString() + ")";
            String detalle = normalizar(dp.getObservaciones());
            if (detalle != null) {
                comentario += ". " + detalle;
            }
            String previo = normalizar(linea.getObservaciones());

            linea.setPrecio(nuevo);
            linea.setObservaciones(previo == null ? comentario : previo + " | " + comentario);
            if (!ordenProductoDAO.modificar(linea)) {
                mostrarMensaje(FacesMessage.SEVERITY_ERROR, "No se pudo aplicar el descuento.");
                return;
            }
            mostrarMensaje(FacesMessage.SEVERITY_INFO, "Descuento \"" + dp.getIdDescuento().getNombre()
                    + "\" (" + valor + "%) aplicado: -$" + descontado.toPlainString()
                    + ". Nuevo precio: $" + nuevo.toPlainString() + ".");
            lineaConDescuento = linea;
            cargarProductosOrden();
        } catch (RuntimeException e) {
            LOG.log(Level.SEVERE, "Error al aplicar el descuento", e);
            mostrarMensaje(FacesMessage.SEVERITY_ERROR, "No se pudo aplicar el descuento.");
        }
    }

    private List<DescuentoProducto> descuentosDelProducto(UUID idProducto) {
        List<DescuentoProducto> lista = new ArrayList<>();
        for (DescuentoProducto dp : descuentoProductoDAO.findByProducto(idProducto)) {
            if (dp.getIdDescuento() != null && dp.getValor() != null && dp.getValor() > 0) {
                lista.add(dp);
            }
        }
        return lista;
    }

    public String etiquetaDescuento(DescuentoProducto dp) {
        if (dp == null || dp.getIdDescuento() == null) {
            return "";
        }
        LocalDate desde = dia(dp.getFechaDesde());
        LocalDate hasta = dia(dp.getFechaHasta());
        String vigencia;
        if (desde != null && hasta != null) {
            vigencia = desde.format(FMT) + " al " + hasta.format(FMT);
        } else if (desde != null) {
            vigencia = "desde " + desde.format(FMT) + ", sin fecha final";
        } else if (hasta != null) {
            vigencia = "hasta " + hasta.format(FMT);
        } else {
            vigencia = "sin límite de fechas";
        }
        return dp.getIdDescuento().getNombre() + " - " + dp.getValor() + "% (" + vigencia + ")";
    }

    private static boolean aplicaEnFecha(DescuentoProducto dp, LocalDate fechaOrden) {
        return dp.getIdDescuento() != null
                && estaEnRango(fechaOrden, dp.getFechaDesde(), dp.getFechaHasta())
                && estaEnRango(fechaOrden, dp.getIdDescuento().getFechaDesde(), dp.getIdDescuento().getFechaHasta());
    }

    private static boolean estaEnRango(LocalDate fecha, Date desde, Date hasta) {
        if (fecha == null) {
            return false;
        }
        LocalDate inicio = dia(desde);
        LocalDate fin = dia(hasta);
        return (inicio == null || !fecha.isBefore(inicio)) && (fin == null || !fecha.isAfter(fin));
    }

    private static String normalizar(String s) {
        return (s == null || s.isBlank()) ? null : s.trim();
    }

    private static UUID aUuid(String s) {
        String n = normalizar(s);
        if (n == null) {
            return null;
        }
        try {
            return UUID.fromString(n);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    private static LocalDate dia(Date fecha) {
        return fecha == null ? null
                : Instant.ofEpochMilli(fecha.getTime()).atZone(ZoneId.systemDefault()).toLocalDate();
    }

    public Orden getSeleccionado() {
        return seleccionado;
    }

    public void setSeleccionado(Orden seleccionado) {
        this.seleccionado = seleccionado;
    }

    public List<EmpleadoRol> getCamareros() {
        return camareros;
    }

    public String getIdEmpleadoRolElegido() {
        return idEmpleadoRolElegido;
    }

    public void setIdEmpleadoRolElegido(String id) {
        this.idEmpleadoRolElegido = id;
    }

    public List<OrdenProducto> getProductosOrden() {
        return productosOrden;
    }

    public OrdenProducto getProductoSeleccionado() {
        return productoSeleccionado;
    }

    public void setProductoSeleccionado(OrdenProducto p) {
        this.productoSeleccionado = p;
    }

    public OrdenProducto getNuevoProducto() {
        return nuevoProducto;
    }

    public boolean isFormProducto() {
        return formProducto;
    }

    public String getIdProductoElegido() {
        return idProductoElegido;
    }

    public void setIdProductoElegido(String id) {
        this.idProductoElegido = id;
    }

    public List<Producto> getProductosDisponibles() {
        return productosDisponibles;
    }

    public boolean isFormDescuento() {
        return formDescuento;
    }

    public OrdenProducto getLineaConDescuento() {
        return lineaConDescuento;
    }

    public String getIdDescuentoElegido() {
        return idDescuentoElegido;
    }

    public void setIdDescuentoElegido(String id) {
        this.idDescuentoElegido = id;
    }

    public List<DescuentoProducto> getDescuentosDisponibles() {
        return descuentosDisponibles;
    }
}
