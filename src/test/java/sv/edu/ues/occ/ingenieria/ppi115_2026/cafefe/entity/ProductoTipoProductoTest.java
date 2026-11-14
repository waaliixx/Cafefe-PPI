package sv.edu.ues.occ.ingenieria.ppi115_2026.cafefe.entity;

import java.util.Date;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/** Pruebas unitarias de ProductoTipoProducto, sin persistencia ni servidor. */
class ProductoTipoProductoTest {

    @Test
    void constructorVacioInicializaAtributosEnNull() {
        ProductoTipoProducto entidad = new ProductoTipoProducto();
        assertNull(entidad.getIdProductoTipoProducto());
        assertNull(entidad.getFechaCreacion());
        assertNull(entidad.getObservaciones());
        assertNull(entidad.getIdProducto());
        assertNull(entidad.getIdTipoProducto());
    }

    @Test
    void constructorConIdConservaIdentificador() {
        UUID id = UUID.fromString("00000000-0000-0000-0000-000000000001");
        ProductoTipoProducto entidad = new ProductoTipoProducto(id);
        assertEquals(id, entidad.getIdProductoTipoProducto());
    }

    @Test
    void asignarIdProductoTipoProductoPermiteActualizarYLimpiar() {
        ProductoTipoProducto entidad = new ProductoTipoProducto();
        UUID inicial = UUID.fromString("00000000-0000-0000-0000-000000000001");
        UUID actualizado = UUID.fromString("00000000-0000-0000-0000-000000000002");
        entidad.setIdProductoTipoProducto(inicial);
        assertEquals(inicial, entidad.getIdProductoTipoProducto());
        entidad.setIdProductoTipoProducto(actualizado);
        assertEquals(actualizado, entidad.getIdProductoTipoProducto());
        entidad.setIdProductoTipoProducto(null);
        assertNull(entidad.getIdProductoTipoProducto());
    }

    @Test
    void asignarFechaCreacionPermiteActualizarYLimpiar() {
        ProductoTipoProducto entidad = new ProductoTipoProducto();
        Date inicial = new Date(1700000000000L);
        Date actualizado = new Date(1700086400000L);
        entidad.setFechaCreacion(inicial);
        assertEquals(inicial, entidad.getFechaCreacion());
        entidad.setFechaCreacion(actualizado);
        assertEquals(actualizado, entidad.getFechaCreacion());
        entidad.setFechaCreacion(null);
        assertNull(entidad.getFechaCreacion());
    }

    @Test
    void asignarObservacionesPermiteActualizarYLimpiar() {
        ProductoTipoProducto entidad = new ProductoTipoProducto();
        String inicial = "Valor inicial";
        String actualizado = "Valor actualizado";
        entidad.setObservaciones(inicial);
        assertEquals(inicial, entidad.getObservaciones());
        entidad.setObservaciones(actualizado);
        assertEquals(actualizado, entidad.getObservaciones());
        entidad.setObservaciones(null);
        assertNull(entidad.getObservaciones());
    }

    @Test
    void asignarIdProductoPermiteActualizarYLimpiar() {
        ProductoTipoProducto entidad = new ProductoTipoProducto();
        Producto inicial = new Producto(UUID.fromString("00000000-0000-0000-0000-000000000001"));
        Producto actualizado = new Producto(UUID.fromString("00000000-0000-0000-0000-000000000002"));
        entidad.setIdProducto(inicial);
        assertSame(inicial, entidad.getIdProducto());
        entidad.setIdProducto(actualizado);
        assertSame(actualizado, entidad.getIdProducto());
        entidad.setIdProducto(null);
        assertNull(entidad.getIdProducto());
    }

    @Test
    void asignarIdTipoProductoPermiteActualizarYLimpiar() {
        ProductoTipoProducto entidad = new ProductoTipoProducto();
        TipoProducto inicial = new TipoProducto(UUID.fromString("00000000-0000-0000-0000-000000000001"));
        TipoProducto actualizado = new TipoProducto(UUID.fromString("00000000-0000-0000-0000-000000000002"));
        entidad.setIdTipoProducto(inicial);
        assertSame(inicial, entidad.getIdTipoProducto());
        entidad.setIdTipoProducto(actualizado);
        assertSame(actualizado, entidad.getIdTipoProducto());
        entidad.setIdTipoProducto(null);
        assertNull(entidad.getIdTipoProducto());
    }

    @Test
    void igualdadDependeDelIdentificador() {
        UUID id = UUID.randomUUID();
        ProductoTipoProducto entidad = new ProductoTipoProducto(id);
        ProductoTipoProducto otra = new ProductoTipoProducto(id);
        ProductoTipoProducto tercera = new ProductoTipoProducto(id);
        assertEquals(entidad, entidad);
        assertEquals(entidad, otra);
        assertEquals(otra, entidad);
        assertEquals(otra, tercera);
        assertEquals(entidad, tercera);
        assertEquals(entidad.hashCode(), otra.hashCode());
        assertNotEquals(entidad, new ProductoTipoProducto(UUID.randomUUID()));
        assertNotEquals(entidad, null);
        assertNotEquals(entidad, new Object());
    }

    @Test
    void identificadorNullSeComparaSinExcepciones() {
        ProductoTipoProducto sinId = new ProductoTipoProducto();
        ProductoTipoProducto conId = new ProductoTipoProducto(UUID.randomUUID());
        // Comportamiento actual: dos entidades sin identificador se consideran iguales.
        assertEquals(sinId, new ProductoTipoProducto());
        assertNotEquals(sinId, conId);
        assertNotEquals(conId, sinId);
        assertEquals(0, sinId.hashCode());
    }

    @Test
    void hashCodeCorrespondeAlIdentificador() {
        UUID id = UUID.randomUUID();
        ProductoTipoProducto entidad = new ProductoTipoProducto(id);
        assertEquals(id.hashCode(), entidad.hashCode());
        entidad.setIdProductoTipoProducto(null);
        assertEquals(0, entidad.hashCode());
    }

    @Test
    void toStringIncluyeClaseEIdentificador() {
        UUID id = UUID.randomUUID();
        assertEquals("sv.edu.ues.occ.ingenieria.ppi115_2026.cafefe.entity.ProductoTipoProducto[ idProductoTipoProducto=" + id + " ]",
                new ProductoTipoProducto(id).toString());
        assertEquals("sv.edu.ues.occ.ingenieria.ppi115_2026.cafefe.entity.ProductoTipoProducto[ idProductoTipoProducto=null ]",
                new ProductoTipoProducto().toString());
    }
}
