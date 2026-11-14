package sv.edu.ues.occ.ingenieria.ppi115_2026.cafefe.boundary.jsf;

import jakarta.faces.application.FacesMessage;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;
import sv.edu.ues.occ.ingenieria.ppi115_2026.cafefe.control.AbstraccionDAO;
import sv.edu.ues.occ.ingenieria.ppi115_2026.cafefe.control.CaracteristicaDAO;
import sv.edu.ues.occ.ingenieria.ppi115_2026.cafefe.control.DescuentoDAO;
import sv.edu.ues.occ.ingenieria.ppi115_2026.cafefe.control.DescuentoProductoDAO;
import sv.edu.ues.occ.ingenieria.ppi115_2026.cafefe.control.ProductoCaracteristicaDAO;
import sv.edu.ues.occ.ingenieria.ppi115_2026.cafefe.control.ProductoDAO;
import sv.edu.ues.occ.ingenieria.ppi115_2026.cafefe.control.ProductoTipoProductoDAO;
import sv.edu.ues.occ.ingenieria.ppi115_2026.cafefe.control.TipoCaracteristicaDAO;
import sv.edu.ues.occ.ingenieria.ppi115_2026.cafefe.control.TipoDescuentoDAO;
import sv.edu.ues.occ.ingenieria.ppi115_2026.cafefe.control.TipoProductoDAO;
import sv.edu.ues.occ.ingenieria.ppi115_2026.cafefe.entity.Caracteristica;
import sv.edu.ues.occ.ingenieria.ppi115_2026.cafefe.entity.Descuento;
import sv.edu.ues.occ.ingenieria.ppi115_2026.cafefe.entity.DescuentoProducto;
import sv.edu.ues.occ.ingenieria.ppi115_2026.cafefe.entity.Producto;
import sv.edu.ues.occ.ingenieria.ppi115_2026.cafefe.entity.ProductoCaracteristica;
import sv.edu.ues.occ.ingenieria.ppi115_2026.cafefe.entity.ProductoTipoProducto;
import sv.edu.ues.occ.ingenieria.ppi115_2026.cafefe.entity.TipoCaracteristica;
import sv.edu.ues.occ.ingenieria.ppi115_2026.cafefe.entity.TipoDescuento;
import sv.edu.ues.occ.ingenieria.ppi115_2026.cafefe.entity.TipoProducto;

/**
 *
 * @author edenrvm
 */
@Named("productoModel")
@ViewScoped
public class ProductoModel extends AbstraccionModel<Producto> implements Serializable {

    private static final long serialVersionUID = 1L;
    private static final Logger LOG = Logger.getLogger(ProductoModel.class.getName());
    private static final BigDecimal PRECIO_MAXIMO = new BigDecimal("999999.99");
    private static final List<BigDecimal> PRECIOS_SUGERIDOS = List.of(
            new BigDecimal("1.99"),
            new BigDecimal("2.50"),
            new BigDecimal("5.99"),
            new BigDecimal("8.99"));
    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    @Inject
    private ProductoDAO productoDAO;
    @Inject
    private ProductoTipoProductoDAO productoTipoProductoDAO;
    @Inject
    private ProductoCaracteristicaDAO productoCaracteristicaDAO;
    @Inject
    private DescuentoProductoDAO descuentoProductoDAO;
    @Inject
    private TipoProductoDAO tipoProductoDAO;
    @Inject
    private TipoCaracteristicaDAO tipoCaracteristicaDAO;
    @Inject
    private CaracteristicaDAO caracteristicaDAO;
    @Inject
    private TipoDescuentoDAO tipoDescuentoDAO;
    @Inject
    private DescuentoDAO descuentoDAO;

    private Producto seleccionado;

    //Tipo Producto
    private List<ProductoTipoProducto> tiposAsignados = new ArrayList<>();
    private ProductoTipoProducto tipoSeleccionado;
    private ProductoTipoProducto nuevoTipo;
    private boolean formTipo;
    private String idTipoProductoElegido;
    private List<TipoProducto> tiposProductoDisponibles = new ArrayList<>();

    //Caracteristica
    private List<ProductoCaracteristica> caracteristicasAsignadas = new ArrayList<>();
    private ProductoCaracteristica caracteristicaSeleccionada;
    private ProductoCaracteristica nuevaCaracteristica;
    private boolean formCaracteristica;
    private String idTipoCaracteristicaElegido;
    private String idCaracteristicaElegida;
    private TipoCaracteristica tipoCaracteristicaElegido;
    private List<TipoCaracteristica> tiposCaracteristicaDisponibles = new ArrayList<>();
    private List<Caracteristica> caracteristicasDelTipo = new ArrayList<>();

    //Descuento
    private List<DescuentoProducto> descuentosAsignados = new ArrayList<>();
    private DescuentoProducto descuentoSeleccionado;
    private DescuentoProducto nuevoDescuento;
    private boolean formDescuento;
    private String idTipoDescuentoElegido;
    private String idDescuentoElegido;
    private TipoDescuento tipoDescuentoElegido;
    private Descuento descuentoElegido;
    private List<TipoDescuento> tiposDescuentoDisponibles = new ArrayList<>();
    private List<Descuento> descuentosDelTipo = new ArrayList<>();
    private Map<UUID, String> nombresTipoDescuento = new HashMap<>();

    @Override
    protected AbstraccionDAO<Producto> getDAO() {
        return productoDAO;
    }

    @Override
    protected Producto nuevoRegistro() {
        Producto nuevo = new Producto(UUID.randomUUID());
        nuevo.setActivo(Boolean.TRUE);
        return nuevo;
    }

    @Override
    protected Object getIdRegistro(Producto entidad) {
        return entidad != null ? entidad.getIdProducto() : null;
    }

    //Generalidades
    @Override
    protected boolean validarRegistro() {
        Producto p = getRegistro();
        if (p == null) {
            return false;
        }
        if (p.getNombre() == null || p.getNombre().trim().isEmpty()) {
            mostrarMensaje(FacesMessage.SEVERITY_WARN, "El nombre del producto es obligatorio.");
            return false;
        }
        if (p.getNombre().trim().length() > 155) {
            mostrarMensaje(FacesMessage.SEVERITY_WARN, "El nombre no puede superar 155 caracteres.");
            return false;
        }
        if (p.getPrecioSugerido() == null || p.getPrecioSugerido().signum() < 0) {
            mostrarMensaje(FacesMessage.SEVERITY_WARN, "El precio sugerido es obligatorio y no puede ser negativo.");
            return false;
        }
        if (p.getPrecioSugerido().compareTo(PRECIO_MAXIMO) > 0) {
            mostrarMensaje(FacesMessage.SEVERITY_WARN, "El precio sugerido no puede superar 999,999.99.");
            return false;
        }
        p.setNombre(p.getNombre().trim());
        return true;
    }

    public List<BigDecimal> sugerirPrecios(String consulta) {
        String q = consulta == null ? "" : consulta.trim();
        List<BigDecimal> resultado = new ArrayList<>();
        for (BigDecimal precio : PRECIOS_SUGERIDOS) {
            if (precio.toPlainString().startsWith(q)) {
                resultado.add(precio);
            }
        }
        return resultado;
    }

    @Override
    public void nuevo() {
        seleccionado = null;
        reiniciarDetalles();
        super.nuevo();
    }

    @Override
    public void editar(Producto entidad) {
        super.editar(entidad);
        if (isEditando()) {
            reiniciarDetalles();
            cargarDetalles();
        }
    }

    @Override
    public void cancelar() {
        super.cancelar();
        seleccionado = null;
        reiniciarDetalles();
    }

    @Override
    public void eliminar(Producto entidad) {
        if (entidad != null && entidad.getIdProducto() != null) {
            UUID id = entidad.getIdProducto();
            try {
                for (ProductoTipoProducto x : productoTipoProductoDAO.findByProducto(id)) {
                    if (!productoTipoProductoDAO.eliminar(x)) {
                        mostrarMensaje(FacesMessage.SEVERITY_ERROR, "No se pudieron quitar los tipos del producto.");
                        return;
                    }
                }
                for (ProductoCaracteristica x : productoCaracteristicaDAO.findByProducto(id)) {
                    if (!productoCaracteristicaDAO.eliminar(x)) {
                        mostrarMensaje(FacesMessage.SEVERITY_ERROR, "No se pudieron quitar las características del producto.");
                        return;
                    }
                }
                for (DescuentoProducto x : descuentoProductoDAO.findByProducto(id)) {
                    if (!descuentoProductoDAO.eliminar(x)) {
                        mostrarMensaje(FacesMessage.SEVERITY_ERROR, "No se pudieron quitar los descuentos del producto.");
                        return;
                    }
                }
            } catch (RuntimeException e) {
                LOG.log(Level.SEVERE, "Error al quitar los detalles del producto", e);
                mostrarMensaje(FacesMessage.SEVERITY_ERROR,
                        "No se puede eliminar: el producto tiene detalles u órdenes asociadas.");
                return;
            }
        }
        super.eliminar(entidad);
        seleccionado = null;
    }

    private void reiniciarDetalles() {
        tiposAsignados = new ArrayList<>();
        caracteristicasAsignadas = new ArrayList<>();
        descuentosAsignados = new ArrayList<>();
        tipoSeleccionado = null;
        caracteristicaSeleccionada = null;
        descuentoSeleccionado = null;
        cerrarFormTipo();
        cerrarFormCaracteristica();
        cerrarFormDescuento();
    }

    private void cargarDetalles() {
        UUID id = getRegistro().getIdProducto();
        try {
            tiposAsignados = new ArrayList<>(productoTipoProductoDAO.findByProducto(id));
            caracteristicasAsignadas = new ArrayList<>(productoCaracteristicaDAO.findByProducto(id));
            descuentosAsignados = new ArrayList<>(descuentoProductoDAO.findByProducto(id));
            nombresTipoDescuento = new HashMap<>();
            for (TipoDescuento td : tipoDescuentoDAO.findAll()) {
                nombresTipoDescuento.put(td.getIdTipoDescuento(), td.getNombre());
            }
        } catch (RuntimeException e) {
            LOG.log(Level.SEVERE, "No se pudieron cargar los detalles del producto", e);
            mostrarMensaje(FacesMessage.SEVERITY_ERROR, "No se pudieron cargar los detalles del producto.");
        }
    }

    private boolean listoParaDetalles() {
        if (!isEditando() || getRegistro() == null) {
            mostrarMensaje(FacesMessage.SEVERITY_WARN, "Guarde primero el producto para agregarle detalles.");
            return false;
        }
        return true;
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

    public boolean isSubFormularioAbierto() {
        return formTipo || formCaracteristica || formDescuento;
    }

    //Tipo de Producto
    public void abrirFormTipo() {
        if (!listoParaDetalles()) {
            return;
        }
        nuevoTipo = new ProductoTipoProducto(UUID.randomUUID());
        nuevoTipo.setFechaCreacion(new Date());
        idTipoProductoElegido = null;
        try {
            tiposProductoDisponibles = new ArrayList<>(tipoProductoDAO.findAll());
        } catch (RuntimeException e) {
            LOG.log(Level.SEVERE, "No se pudieron cargar los tipos de producto", e);
            mostrarMensaje(FacesMessage.SEVERITY_ERROR, "No se pudieron cargar los tipos de producto.");
            return;
        }
        formTipo = true;
    }

    public void cerrarFormTipo() {
        formTipo = false;
        nuevoTipo = null;
        idTipoProductoElegido = null;
    }

    public void guardarTipo() {
        if (!listoParaDetalles() || nuevoTipo == null) {
            return;
        }
        try {
            UUID idTipo = aUuid(idTipoProductoElegido);
            if (idTipo == null) {
                mostrarMensaje(FacesMessage.SEVERITY_WARN, "Seleccione un tipo de producto.");
                return;
            }
            TipoProducto tipo = tipoProductoDAO.buscarPorId(idTipo);
            if (tipo == null) {
                mostrarMensaje(FacesMessage.SEVERITY_ERROR, "El tipo de producto elegido ya no existe.");
                return;
            }
            if (!Boolean.TRUE.equals(tipo.getActivo())) {
                mostrarMensaje(FacesMessage.SEVERITY_ERROR,
                        "No se puede elegir un tipo de producto inactivo (\"" + tipo.getNombre() + "\").");
                return;
            }
            for (ProductoTipoProducto existente : tiposAsignados) {
                if (existente.getIdTipoProducto() != null
                        && idTipo.equals(existente.getIdTipoProducto().getIdTipoProducto())) {
                    mostrarMensaje(FacesMessage.SEVERITY_WARN,
                            "El producto ya tiene el tipo \"" + tipo.getNombre() + "\".");
                    return;
                }
            }
            nuevoTipo.setIdProducto(getRegistro());
            nuevoTipo.setIdTipoProducto(tipo);
            if (nuevoTipo.getFechaCreacion() == null) {
                nuevoTipo.setFechaCreacion(new Date());
            }
            if (!productoTipoProductoDAO.crear(nuevoTipo)) {
                mostrarMensaje(FacesMessage.SEVERITY_ERROR, "No se pudo guardar el tipo de producto.");
                return;
            }
            mostrarMensaje(FacesMessage.SEVERITY_INFO, "Tipo de producto agregado.");
            cerrarFormTipo();
            tiposAsignados = new ArrayList<>(productoTipoProductoDAO.findByProducto(getRegistro().getIdProducto()));
        } catch (RuntimeException e) {
            LOG.log(Level.SEVERE, "Error al guardar el tipo de producto", e);
            mostrarMensaje(FacesMessage.SEVERITY_ERROR, "No se pudo guardar el tipo de producto.");
        }
    }

    public void eliminarTipo() {
        if (tipoSeleccionado == null || !listoParaDetalles()) {
            return;
        }
        try {
            if (!productoTipoProductoDAO.eliminar(tipoSeleccionado)) {
                mostrarMensaje(FacesMessage.SEVERITY_ERROR, "No se pudo quitar el tipo de producto.");
                return;
            }
            mostrarMensaje(FacesMessage.SEVERITY_INFO, "Tipo de producto quitado.");
            tipoSeleccionado = null;
            tiposAsignados = new ArrayList<>(productoTipoProductoDAO.findByProducto(getRegistro().getIdProducto()));
        } catch (RuntimeException e) {
            LOG.log(Level.SEVERE, "Error al quitar el tipo de producto", e);
            mostrarMensaje(FacesMessage.SEVERITY_ERROR, "No se pudo quitar el tipo de producto.");
        }
    }

    //Caracteristica
    public void abrirFormCaracteristica() {
        if (!listoParaDetalles()) {
            return;
        }
        nuevaCaracteristica = new ProductoCaracteristica(UUID.randomUUID());
        idTipoCaracteristicaElegido = null;
        idCaracteristicaElegida = null;
        tipoCaracteristicaElegido = null;
        caracteristicasDelTipo = new ArrayList<>();
        try {
            tiposCaracteristicaDisponibles = new ArrayList<>(tipoCaracteristicaDAO.findAll());
        } catch (RuntimeException e) {
            LOG.log(Level.SEVERE, "No se pudieron cargar los tipos de característica", e);
            mostrarMensaje(FacesMessage.SEVERITY_ERROR, "No se pudieron cargar los tipos de característica.");
            return;
        }
        formCaracteristica = true;
    }

    public void cerrarFormCaracteristica() {
        formCaracteristica = false;
        nuevaCaracteristica = null;
        idTipoCaracteristicaElegido = null;
        idCaracteristicaElegida = null;
        tipoCaracteristicaElegido = null;
        caracteristicasDelTipo = new ArrayList<>();
    }

    public void alCambiarTipoCaracteristica() {
        idCaracteristicaElegida = null;
        tipoCaracteristicaElegido = null;
        caracteristicasDelTipo = new ArrayList<>();
        UUID idTipo = aUuid(idTipoCaracteristicaElegido);
        if (idTipo == null) {
            return;
        }
        try {
            tipoCaracteristicaElegido = tipoCaracteristicaDAO.buscarPorId(idTipo);
            if (tipoCaracteristicaElegido != null) {
                caracteristicasDelTipo = new ArrayList<>(
                        caracteristicaDAO.findByTipoCaracteristica(tipoCaracteristicaElegido));
            }
        } catch (RuntimeException e) {
            LOG.log(Level.SEVERE, "No se pudieron cargar las características", e);
            mostrarMensaje(FacesMessage.SEVERITY_ERROR, "No se pudieron cargar las características del tipo.");
        }
    }

    public void guardarCaracteristica() {
        if (!listoParaDetalles() || nuevaCaracteristica == null) {
            return;
        }
        try {
            UUID idTipo = aUuid(idTipoCaracteristicaElegido);
            if (idTipo == null) {
                mostrarMensaje(FacesMessage.SEVERITY_WARN, "Seleccione un tipo de característica.");
                return;
            }
            TipoCaracteristica tipo = tipoCaracteristicaDAO.buscarPorId(idTipo);
            if (tipo == null) {
                mostrarMensaje(FacesMessage.SEVERITY_ERROR, "El tipo de característica elegido ya no existe.");
                return;
            }
            if (!Boolean.TRUE.equals(tipo.getActivo())) {
                mostrarMensaje(FacesMessage.SEVERITY_ERROR,
                        "No se puede elegir un tipo de característica inactivo (\"" + tipo.getNombre() + "\").");
                return;
            }
            UUID idCar = aUuid(idCaracteristicaElegida);
            if (idCar == null) {
                mostrarMensaje(FacesMessage.SEVERITY_WARN, "Seleccione una característica.");
                return;
            }
            Caracteristica car = caracteristicaDAO.buscarPorId(idCar);
            if (car == null || car.getIdTipoCaracteristica() == null
                    || !idTipo.equals(car.getIdTipoCaracteristica().getIdTipoCaracteristica())) {
                mostrarMensaje(FacesMessage.SEVERITY_ERROR, "La característica no pertenece al tipo elegido.");
                return;
            }
            if (!Boolean.TRUE.equals(car.getActivo())) {
                mostrarMensaje(FacesMessage.SEVERITY_ERROR,
                        "No se puede elegir una característica inactiva (\"" + car.getNombre() + "\").");
                return;
            }
            String valor = normalizar(nuevaCaracteristica.getValor());
            if (valor == null) {
                mostrarMensaje(FacesMessage.SEVERITY_WARN, "El valor de la característica es obligatorio.");
                return;
            }
            String er = tipo.getExpresionRegular();
            try {
                if (er == null || er.isEmpty() || !Pattern.matches(er, valor)) {
                    mostrarMensaje(FacesMessage.SEVERITY_ERROR,
                            "El valor \"" + valor + "\" no cumple el formato del tipo \"" + tipo.getNombre()
                            + "\" (expresión: " + er + ").");
                    return;
                }
            } catch (PatternSyntaxException e) {
                mostrarMensaje(FacesMessage.SEVERITY_ERROR,
                        "La expresión regular del tipo \"" + tipo.getNombre()
                        + "\" no es válida. Corríjala en Tipos de Característica.");
                return;
            }
            for (ProductoCaracteristica existente : caracteristicasAsignadas) {
                if (existente.getIdCaracteristica() != null
                        && idCar.equals(existente.getIdCaracteristica().getIdCaracteristica())) {
                    mostrarMensaje(FacesMessage.SEVERITY_WARN,
                            "El producto ya tiene la característica \"" + car.getNombre()
                            + "\". Quítela primero para registrar otro valor.");
                    return;
                }
            }
            nuevaCaracteristica.setIdProducto(getRegistro());
            nuevaCaracteristica.setIdCaracteristica(car);
            nuevaCaracteristica.setValor(valor);
            if (!productoCaracteristicaDAO.crear(nuevaCaracteristica)) {
                mostrarMensaje(FacesMessage.SEVERITY_ERROR, "No se pudo guardar la característica.");
                return;
            }
            mostrarMensaje(FacesMessage.SEVERITY_INFO, "Característica agregada.");
            cerrarFormCaracteristica();
            caracteristicasAsignadas = new ArrayList<>(
                    productoCaracteristicaDAO.findByProducto(getRegistro().getIdProducto()));
        } catch (RuntimeException e) {
            LOG.log(Level.SEVERE, "Error al guardar la característica", e);
            mostrarMensaje(FacesMessage.SEVERITY_ERROR, "No se pudo guardar la característica.");
        }
    }

    public void eliminarCaracteristica() {
        if (caracteristicaSeleccionada == null || !listoParaDetalles()) {
            return;
        }
        try {
            if (!productoCaracteristicaDAO.eliminar(caracteristicaSeleccionada)) {
                mostrarMensaje(FacesMessage.SEVERITY_ERROR, "No se pudo quitar la característica.");
                return;
            }
            mostrarMensaje(FacesMessage.SEVERITY_INFO, "Característica quitada.");
            caracteristicaSeleccionada = null;
            caracteristicasAsignadas = new ArrayList<>(
                    productoCaracteristicaDAO.findByProducto(getRegistro().getIdProducto()));
        } catch (RuntimeException e) {
            LOG.log(Level.SEVERE, "Error al quitar la característica", e);
            mostrarMensaje(FacesMessage.SEVERITY_ERROR, "No se pudo quitar la característica.");
        }
    }

    //Descuento
    public void abrirFormDescuento() {
        if (!listoParaDetalles()) {
            return;
        }
        nuevoDescuento = new DescuentoProducto(UUID.randomUUID());
        idTipoDescuentoElegido = null;
        idDescuentoElegido = null;
        tipoDescuentoElegido = null;
        descuentoElegido = null;
        descuentosDelTipo = new ArrayList<>();
        try {
            tiposDescuentoDisponibles = new ArrayList<>(tipoDescuentoDAO.findAll());
        } catch (RuntimeException e) {
            LOG.log(Level.SEVERE, "No se pudieron cargar los tipos de descuento", e);
            mostrarMensaje(FacesMessage.SEVERITY_ERROR, "No se pudieron cargar los tipos de descuento.");
            return;
        }
        formDescuento = true;
    }

    public void cerrarFormDescuento() {
        formDescuento = false;
        nuevoDescuento = null;
        idTipoDescuentoElegido = null;
        idDescuentoElegido = null;
        tipoDescuentoElegido = null;
        descuentoElegido = null;
        descuentosDelTipo = new ArrayList<>();
    }

    public void alCambiarTipoDescuento() {
        idDescuentoElegido = null;
        descuentoElegido = null;
        tipoDescuentoElegido = null;
        descuentosDelTipo = new ArrayList<>();
        UUID idTipo = aUuid(idTipoDescuentoElegido);
        if (idTipo == null) {
            return;
        }
        try {
            tipoDescuentoElegido = tipoDescuentoDAO.buscarPorId(idTipo);
            descuentosDelTipo = new ArrayList<>(descuentoDAO.findByTipo(idTipo));
        } catch (RuntimeException e) {
            LOG.log(Level.SEVERE, "No se pudieron cargar los descuentos", e);
            mostrarMensaje(FacesMessage.SEVERITY_ERROR, "No se pudieron cargar los descuentos del tipo.");
        }
    }

    public void alCambiarDescuento() {
        descuentoElegido = null;
        UUID id = aUuid(idDescuentoElegido);
        if (id == null) {
            return;
        }
        try {
            descuentoElegido = descuentoDAO.buscarPorId(id);
        } catch (RuntimeException e) {
            LOG.log(Level.SEVERE, "No se pudo recuperar el descuento", e);
        }
    }

    public void guardarDescuento() {
        if (!listoParaDetalles() || nuevoDescuento == null) {
            return;
        }
        try {
            UUID idTipo = aUuid(idTipoDescuentoElegido);
            if (idTipo == null) {
                mostrarMensaje(FacesMessage.SEVERITY_WARN, "Seleccione un tipo de descuento.");
                return;
            }
            TipoDescuento tipo = tipoDescuentoDAO.buscarPorId(idTipo);
            if (tipo == null) {
                mostrarMensaje(FacesMessage.SEVERITY_ERROR, "El tipo de descuento elegido ya no existe.");
                return;
            }
            if (!Boolean.TRUE.equals(tipo.getActivo())) {
                mostrarMensaje(FacesMessage.SEVERITY_ERROR,
                        "No se puede elegir un tipo de descuento inactivo (\"" + tipo.getNombre() + "\").");
                return;
            }
            UUID idDesc = aUuid(idDescuentoElegido);
            if (idDesc == null) {
                mostrarMensaje(FacesMessage.SEVERITY_WARN, "Seleccione un descuento.");
                return;
            }
            Descuento desc = descuentoDAO.buscarPorId(idDesc);
            if (desc == null || !idTipo.equals(desc.getIdTipoDescuento())) {
                mostrarMensaje(FacesMessage.SEVERITY_ERROR, "El descuento no pertenece al tipo elegido.");
                return;
            }

            Integer valor = nuevoDescuento.getValor();
            if (valor == null || valor <= 0) {
                mostrarMensaje(FacesMessage.SEVERITY_WARN, "El porcentaje debe ser mayor que 0.");
                return;
            }
            if (tipo.getDescuentoMaximo() != null && valor > tipo.getDescuentoMaximo()) {
                mostrarMensaje(FacesMessage.SEVERITY_ERROR,
                        "El porcentaje (" + valor + "%) supera el descuento máximo del tipo \""
                        + tipo.getNombre() + "\" (" + tipo.getDescuentoMaximo() + "%).");
                return;
            }

            LocalDate inicio = dia(nuevoDescuento.getFechaDesde());
            LocalDate fin = dia(nuevoDescuento.getFechaHasta());
            LocalDate vigDesde = dia(desc.getFechaDesde());
            LocalDate vigHasta = dia(desc.getFechaHasta());

            if (inicio == null) {
                mostrarMensaje(FacesMessage.SEVERITY_WARN, "La fecha de inicio es obligatoria.");
                return;
            }
            if (vigDesde != null && inicio.isBefore(vigDesde)) {
                mostrarMensaje(FacesMessage.SEVERITY_ERROR,
                        "La fecha de inicio no puede ser anterior al inicio del descuento ("
                        + vigDesde.format(FMT) + ").");
                return;
            }
            if (vigHasta != null && inicio.isAfter(vigHasta)) {
                mostrarMensaje(FacesMessage.SEVERITY_ERROR,
                        "La fecha de inicio no puede ser posterior al fin del descuento ("
                        + vigHasta.format(FMT) + ").");
                return;
            }
            if (vigHasta != null && fin == null) {
                mostrarMensaje(FacesMessage.SEVERITY_ERROR,
                        "El descuento \"" + desc.getNombre() + "\" termina el " + vigHasta.format(FMT)
                        + ", por lo que la fecha final es obligatoria.");
                return;
            }
            if (fin != null && fin.isBefore(inicio)) {
                mostrarMensaje(FacesMessage.SEVERITY_ERROR,
                        "La fecha final no puede ser anterior a la fecha de inicio.");
                return;
            }
            if (fin != null && vigHasta != null && fin.isAfter(vigHasta)) {
                mostrarMensaje(FacesMessage.SEVERITY_ERROR,
                        "La fecha final no puede ser posterior al fin del descuento ("
                        + vigHasta.format(FMT) + ").");
                return;
            }

            nuevoDescuento.setIdProducto(getRegistro());
            nuevoDescuento.setIdDescuento(desc);
            if (!descuentoProductoDAO.crear(nuevoDescuento)) {
                mostrarMensaje(FacesMessage.SEVERITY_ERROR, "No se pudo guardar el descuento.");
                return;
            }
            mostrarMensaje(FacesMessage.SEVERITY_INFO, "Descuento agregado.");
            cerrarFormDescuento();
            descuentosAsignados = new ArrayList<>(
                    descuentoProductoDAO.findByProducto(getRegistro().getIdProducto()));
        } catch (RuntimeException e) {
            LOG.log(Level.SEVERE, "Error al guardar el descuento", e);
            mostrarMensaje(FacesMessage.SEVERITY_ERROR, "No se pudo guardar el descuento.");
        }
    }

    public void eliminarDescuento() {
        if (descuentoSeleccionado == null || !listoParaDetalles()) {
            return;
        }
        try {
            if (!descuentoProductoDAO.eliminar(descuentoSeleccionado)) {
                mostrarMensaje(FacesMessage.SEVERITY_ERROR, "No se pudo quitar el descuento.");
                return;
            }
            mostrarMensaje(FacesMessage.SEVERITY_INFO, "Descuento quitado.");
            descuentoSeleccionado = null;
            descuentosAsignados = new ArrayList<>(
                    descuentoProductoDAO.findByProducto(getRegistro().getIdProducto()));
        } catch (RuntimeException e) {
            LOG.log(Level.SEVERE, "Error al quitar el descuento", e);
            mostrarMensaje(FacesMessage.SEVERITY_ERROR, "No se pudo quitar el descuento.");
        }
    }

    public String getNombreTipoDescuento(DescuentoProducto dp) {
        if (dp == null || dp.getIdDescuento() == null) {
            return "";
        }
        String nombre = nombresTipoDescuento.get(dp.getIdDescuento().getIdTipoDescuento());
        return nombre == null ? "" : nombre;
    }

    @Override
    public List<Producto> getRegistros() {
        return super.getRegistros();
    }

    public Producto getSeleccionado() {
        return seleccionado;
    }

    public void setSeleccionado(Producto seleccionado) {
        this.seleccionado = seleccionado;
    }

    public List<ProductoTipoProducto> getTiposAsignados() {
        return tiposAsignados;
    }

    public ProductoTipoProducto getTipoSeleccionado() {
        return tipoSeleccionado;
    }

    public void setTipoSeleccionado(ProductoTipoProducto t) {
        this.tipoSeleccionado = t;
    }

    public ProductoTipoProducto getNuevoTipo() {
        return nuevoTipo;
    }

    public boolean isFormTipo() {
        return formTipo;
    }

    public String getIdTipoProductoElegido() {
        return idTipoProductoElegido;
    }

    public void setIdTipoProductoElegido(String id) {
        this.idTipoProductoElegido = id;
    }

    public List<TipoProducto> getTiposProductoDisponibles() {
        return tiposProductoDisponibles;
    }

    public List<ProductoCaracteristica> getCaracteristicasAsignadas() {
        return caracteristicasAsignadas;
    }

    public ProductoCaracteristica getCaracteristicaSeleccionada() {
        return caracteristicaSeleccionada;
    }

    public void setCaracteristicaSeleccionada(ProductoCaracteristica c) {
        this.caracteristicaSeleccionada = c;
    }

    public ProductoCaracteristica getNuevaCaracteristica() {
        return nuevaCaracteristica;
    }

    public boolean isFormCaracteristica() {
        return formCaracteristica;
    }

    public String getIdTipoCaracteristicaElegido() {
        return idTipoCaracteristicaElegido;
    }

    public void setIdTipoCaracteristicaElegido(String id) {
        this.idTipoCaracteristicaElegido = id;
    }

    public String getIdCaracteristicaElegida() {
        return idCaracteristicaElegida;
    }

    public void setIdCaracteristicaElegida(String id) {
        this.idCaracteristicaElegida = id;
    }

    public TipoCaracteristica getTipoCaracteristicaElegido() {
        return tipoCaracteristicaElegido;
    }

    public List<TipoCaracteristica> getTiposCaracteristicaDisponibles() {
        return tiposCaracteristicaDisponibles;
    }

    public List<Caracteristica> getCaracteristicasDelTipo() {
        return caracteristicasDelTipo;
    }

    public List<DescuentoProducto> getDescuentosAsignados() {
        return descuentosAsignados;
    }

    public DescuentoProducto getDescuentoSeleccionado() {
        return descuentoSeleccionado;
    }

    public void setDescuentoSeleccionado(DescuentoProducto d) {
        this.descuentoSeleccionado = d;
    }

    public DescuentoProducto getNuevoDescuento() {
        return nuevoDescuento;
    }

    public boolean isFormDescuento() {
        return formDescuento;
    }

    public String getIdTipoDescuentoElegido() {
        return idTipoDescuentoElegido;
    }

    public void setIdTipoDescuentoElegido(String id) {
        this.idTipoDescuentoElegido = id;
    }

    public String getIdDescuentoElegido() {
        return idDescuentoElegido;
    }

    public void setIdDescuentoElegido(String id) {
        this.idDescuentoElegido = id;
    }

    public TipoDescuento getTipoDescuentoElegido() {
        return tipoDescuentoElegido;
    }

    public Descuento getDescuentoElegido() {
        return descuentoElegido;
    }

    public List<TipoDescuento> getTiposDescuentoDisponibles() {
        return tiposDescuentoDisponibles;
    }

    public List<Descuento> getDescuentosDelTipo() {
        return descuentosDelTipo;
    }
}
