package sv.edu.ues.occ.ingenieria.ppi115_2026.cafefe.entity;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/** Pruebas unitarias de Producto, sin persistencia ni servidor. */
class ProductoTest {

    @Test
    void constructorVacioInicializaAtributosEnNull() {
        Producto entidad = new Producto();
        assertNull(entidad.getIdProducto());
        assertNull(entidad.getNombre());
        assertNull(entidad.getActivo());
        assertNull(entidad.getComentarios());
        assertNull(entidad.getPrecioSugerido());
        assertNull(entidad.getProductoCaracteristicaCollection());
        assertNull(entidad.getOrdenProductoCollection());
        assertNull(entidad.getDescuentoProductoCollection());
        assertNull(entidad.getProductoTipoProductoCollection());
    }

    @Test
    void constructorConIdConservaIdentificador() {
        UUID id = UUID.fromString("00000000-0000-0000-0000-000000000001");
        Producto entidad = new Producto(id);
        assertEquals(id, entidad.getIdProducto());
    }

    @Test
    void constructorConIdYPrecioConservaAmbosValores() {
        UUID id = UUID.randomUUID();
        BigDecimal precio = new BigDecimal("2.50");
        Producto entidad = new Producto(id, precio);
        assertEquals(id, entidad.getIdProducto());
        assertEquals(precio, entidad.getPrecioSugerido());
    }

    @Test
    void asignarIdProductoPermiteActualizarYLimpiar() {
        Producto entidad = new Producto();
        UUID inicial = UUID.fromString("00000000-0000-0000-0000-000000000001");
        UUID actualizado = UUID.fromString("00000000-0000-0000-0000-000000000002");
        entidad.setIdProducto(inicial);
        assertEquals(inicial, entidad.getIdProducto());
        entidad.setIdProducto(actualizado);
        assertEquals(actualizado, entidad.getIdProducto());
        entidad.setIdProducto(null);
        assertNull(entidad.getIdProducto());
    }

    @Test
    void asignarNombrePermiteActualizarYLimpiar() {
        Producto entidad = new Producto();
        String inicial = "Valor inicial";
        String actualizado = "Valor actualizado";
        entidad.setNombre(inicial);
        assertEquals(inicial, entidad.getNombre());
        entidad.setNombre(actualizado);
        assertEquals(actualizado, entidad.getNombre());
        entidad.setNombre(null);
        assertNull(entidad.getNombre());
    }

    @Test
    void asignarActivoPermiteActualizarYLimpiar() {
        Producto entidad = new Producto();
        Boolean inicial = Boolean.TRUE;
        Boolean actualizado = Boolean.FALSE;
        entidad.setActivo(inicial);
        assertEquals(inicial, entidad.getActivo());
        entidad.setActivo(actualizado);
        assertEquals(actualizado, entidad.getActivo());
        entidad.setActivo(null);
        assertNull(entidad.getActivo());
    }

    @Test
    void asignarComentariosPermiteActualizarYLimpiar() {
        Producto entidad = new Producto();
        String inicial = "Valor inicial";
        String actualizado = "Valor actualizado";
        entidad.setComentarios(inicial);
        assertEquals(inicial, entidad.getComentarios());
        entidad.setComentarios(actualizado);
        assertEquals(actualizado, entidad.getComentarios());
        entidad.setComentarios(null);
        assertNull(entidad.getComentarios());
    }

    @Test
    void asignarPrecioSugeridoPermiteActualizarYLimpiar() {
        Producto entidad = new Producto();
        BigDecimal inicial = new BigDecimal("2.50");
        BigDecimal actualizado = new BigDecimal("3.75");
        entidad.setPrecioSugerido(inicial);
        assertEquals(inicial, entidad.getPrecioSugerido());
        entidad.setPrecioSugerido(actualizado);
        assertEquals(actualizado, entidad.getPrecioSugerido());
        entidad.setPrecioSugerido(null);
        assertNull(entidad.getPrecioSugerido());
    }

    @Test
    void asignarProductoCaracteristicaCollectionPermiteActualizarYLimpiar() {
        Producto entidad = new Producto();
        Collection<ProductoCaracteristica> inicial = new ArrayList<>(List.of(new ProductoCaracteristica(UUID.randomUUID())));
        Collection<ProductoCaracteristica> actualizado = new ArrayList<>();
        entidad.setProductoCaracteristicaCollection(inicial);
        assertSame(inicial, entidad.getProductoCaracteristicaCollection());
        entidad.setProductoCaracteristicaCollection(actualizado);
        assertSame(actualizado, entidad.getProductoCaracteristicaCollection());
        entidad.setProductoCaracteristicaCollection(null);
        assertNull(entidad.getProductoCaracteristicaCollection());
    }

    @Test
    void asignarOrdenProductoCollectionPermiteActualizarYLimpiar() {
        Producto entidad = new Producto();
        Collection<OrdenProducto> inicial = new ArrayList<>(List.of(new OrdenProducto(UUID.randomUUID())));
        Collection<OrdenProducto> actualizado = new ArrayList<>();
        entidad.setOrdenProductoCollection(inicial);
        assertSame(inicial, entidad.getOrdenProductoCollection());
        entidad.setOrdenProductoCollection(actualizado);
        assertSame(actualizado, entidad.getOrdenProductoCollection());
        entidad.setOrdenProductoCollection(null);
        assertNull(entidad.getOrdenProductoCollection());
    }

    @Test
    void asignarDescuentoProductoCollectionPermiteActualizarYLimpiar() {
        Producto entidad = new Producto();
        Collection<DescuentoProducto> inicial = new ArrayList<>(List.of(new DescuentoProducto(UUID.randomUUID())));
        Collection<DescuentoProducto> actualizado = new ArrayList<>();
        entidad.setDescuentoProductoCollection(inicial);
        assertSame(inicial, entidad.getDescuentoProductoCollection());
        entidad.setDescuentoProductoCollection(actualizado);
        assertSame(actualizado, entidad.getDescuentoProductoCollection());
        entidad.setDescuentoProductoCollection(null);
        assertNull(entidad.getDescuentoProductoCollection());
    }

    @Test
    void asignarProductoTipoProductoCollectionPermiteActualizarYLimpiar() {
        Producto entidad = new Producto();
        Collection<ProductoTipoProducto> inicial = new ArrayList<>(List.of(new ProductoTipoProducto(UUID.randomUUID())));
        Collection<ProductoTipoProducto> actualizado = new ArrayList<>();
        entidad.setProductoTipoProductoCollection(inicial);
        assertSame(inicial, entidad.getProductoTipoProductoCollection());
        entidad.setProductoTipoProductoCollection(actualizado);
        assertSame(actualizado, entidad.getProductoTipoProductoCollection());
        entidad.setProductoTipoProductoCollection(null);
        assertNull(entidad.getProductoTipoProductoCollection());
    }

    @Test
    void igualdadDependeDelIdentificador() {
        UUID id = UUID.randomUUID();
        Producto entidad = new Producto(id);
        Producto otra = new Producto(id);
        Producto tercera = new Producto(id);
        assertEquals(entidad, entidad);
        assertEquals(entidad, otra);
        assertEquals(otra, entidad);
        assertEquals(otra, tercera);
        assertEquals(entidad, tercera);
        assertEquals(entidad.hashCode(), otra.hashCode());
        assertNotEquals(entidad, new Producto(UUID.randomUUID()));
        assertNotEquals(entidad, null);
        assertNotEquals(entidad, new Object());
    }

    @Test
    void identificadorNullSeComparaSinExcepciones() {
        Producto sinId = new Producto();
        Producto conId = new Producto(UUID.randomUUID());
        // Comportamiento actual: dos entidades sin identificador se consideran iguales.
        assertEquals(sinId, new Producto());
        assertNotEquals(sinId, conId);
        assertNotEquals(conId, sinId);
        assertEquals(0, sinId.hashCode());
    }

    @Test
    void hashCodeCorrespondeAlIdentificador() {
        UUID id = UUID.randomUUID();
        Producto entidad = new Producto(id);
        assertEquals(id.hashCode(), entidad.hashCode());
        entidad.setIdProducto(null);
        assertEquals(0, entidad.hashCode());
    }

    @Test
    void toStringIncluyeClaseEIdentificador() {
        UUID id = UUID.randomUUID();
        assertEquals("sv.edu.ues.occ.ingenieria.ppi115_2026.cafefe.entity.Producto[ idProducto=" + id + " ]",
                new Producto(id).toString());
        assertEquals("sv.edu.ues.occ.ingenieria.ppi115_2026.cafefe.entity.Producto[ idProducto=null ]",
                new Producto().toString());
    }
}
