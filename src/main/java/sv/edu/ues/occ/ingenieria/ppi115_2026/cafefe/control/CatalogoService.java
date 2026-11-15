package sv.edu.ues.occ.ingenieria.ppi115_2026.cafefe.control;

import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import sv.edu.ues.occ.ingenieria.ppi115_2026.cafefe.entity.Producto;
import sv.edu.ues.occ.ingenieria.ppi115_2026.cafefe.entity.TipoProducto;

/** Categorías y productos para la pantalla del cliente. */
@Stateless
public class CatalogoService {

    @PersistenceContext(unitName = "CafefePU")
    private EntityManager em;

    /** Todas las categorías (cada una trae su campo "activo"), o solo las activas. */
    public List<Map<String, Object>> categorias(boolean soloActivas) {
        List<TipoProducto> tipos = em.createQuery(
                "SELECT t FROM TipoProducto t" + (soloActivas ? " WHERE t.activo = TRUE" : "")
                + " ORDER BY t.nombre", TipoProducto.class).getResultList();
        List<Map<String, Object>> resultado = new ArrayList<>();
        for (TipoProducto t : tipos) {
            Map<String, Object> dto = new LinkedHashMap<>();
            dto.put("idTipoProducto", t.getIdTipoProducto().toString());
            dto.put("nombre", t.getNombre());
            dto.put("activo", Boolean.TRUE.equals(t.getActivo()));
            dto.put("observaciones", t.getObservaciones());
            resultado.add(dto);
        }
        return resultado;
    }

    public TipoProducto buscarCategoria(UUID idTipoProducto) {
        return idTipoProducto == null ? null : em.find(TipoProducto.class, idTipoProducto);
    }

    /** Productos de una categoría (cada uno trae su campo "activo"), o solo los activos. */
    public List<Map<String, Object>> productosDeCategoria(UUID idTipoProducto, boolean soloActivos) {
        List<Producto> productos = em.createQuery(
                "SELECT DISTINCT p FROM ProductoTipoProducto pt JOIN pt.idProducto p "
                + "WHERE pt.idTipoProducto.idTipoProducto = :id"
                + (soloActivos ? " AND p.activo = TRUE" : "")
                + " ORDER BY p.nombre", Producto.class)
                .setParameter("id", idTipoProducto)
                .getResultList();
        List<Map<String, Object>> resultado = new ArrayList<>();
        for (Producto p : productos) {
            Map<String, Object> dto = new LinkedHashMap<>();
            dto.put("idProducto", p.getIdProducto().toString());
            dto.put("nombre", p.getNombre());
            dto.put("precio", p.getPrecioSugerido());
            dto.put("activo", Boolean.TRUE.equals(p.getActivo()));
            resultado.add(dto);
        }
        return resultado;
    }
}