package sv.edu.ues.occ.ingenieria.ppi115_2026.cafefe.entity;

import java.util.UUID;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/** Pruebas unitarias de ProductoCaracteristica, sin persistencia ni servidor. */
class ProductoCaracteristicaTest {

    @Test
    void constructorVacioInicializaAtributosEnNull() {
        ProductoCaracteristica entidad = new ProductoCaracteristica();
        assertNull(entidad.getIdProductoCaracteristica());
        assertNull(entidad.getValor());
        assertNull(entidad.getIdCaracteristica());
        assertNull(entidad.getIdProducto());
    }

    @Test
    void constructorConIdConservaIdentificador() {
        UUID id = UUID.fromString("00000000-0000-0000-0000-000000000001");
        ProductoCaracteristica entidad = new ProductoCaracteristica(id);
        assertEquals(id, entidad.getIdProductoCaracteristica());
    }

    @Test
    void asignarIdProductoCaracteristicaPermiteActualizarYLimpiar() {
        ProductoCaracteristica entidad = new ProductoCaracteristica();
        UUID inicial = UUID.fromString("00000000-0000-0000-0000-000000000001");
        UUID actualizado = UUID.fromString("00000000-0000-0000-0000-000000000002");
        entidad.setIdProductoCaracteristica(inicial);
        assertEquals(inicial, entidad.getIdProductoCaracteristica());
        entidad.setIdProductoCaracteristica(actualizado);
        assertEquals(actualizado, entidad.getIdProductoCaracteristica());
        entidad.setIdProductoCaracteristica(null);
        assertNull(entidad.getIdProductoCaracteristica());
    }

    @Test
    void asignarValorPermiteActualizarYLimpiar() {
        ProductoCaracteristica entidad = new ProductoCaracteristica();
        String inicial = "Valor inicial";
        String actualizado = "Valor actualizado";
        entidad.setValor(inicial);
        assertEquals(inicial, entidad.getValor());
        entidad.setValor(actualizado);
        assertEquals(actualizado, entidad.getValor());
        entidad.setValor(null);
        assertNull(entidad.getValor());
    }

    @Test
    void asignarIdCaracteristicaPermiteActualizarYLimpiar() {
        ProductoCaracteristica entidad = new ProductoCaracteristica();
        Caracteristica inicial = new Caracteristica(UUID.fromString("00000000-0000-0000-0000-000000000001"));
        Caracteristica actualizado = new Caracteristica(UUID.fromString("00000000-0000-0000-0000-000000000002"));
        entidad.setIdCaracteristica(inicial);
        assertSame(inicial, entidad.getIdCaracteristica());
        entidad.setIdCaracteristica(actualizado);
        assertSame(actualizado, entidad.getIdCaracteristica());
        entidad.setIdCaracteristica(null);
        assertNull(entidad.getIdCaracteristica());
    }

    @Test
    void asignarIdProductoPermiteActualizarYLimpiar() {
        ProductoCaracteristica entidad = new ProductoCaracteristica();
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
    void igualdadDependeDelIdentificador() {
        UUID id = UUID.randomUUID();
        ProductoCaracteristica entidad = new ProductoCaracteristica(id);
        ProductoCaracteristica otra = new ProductoCaracteristica(id);
        ProductoCaracteristica tercera = new ProductoCaracteristica(id);
        assertEquals(entidad, entidad);
        assertEquals(entidad, otra);
        assertEquals(otra, entidad);
        assertEquals(otra, tercera);
        assertEquals(entidad, tercera);
        assertEquals(entidad.hashCode(), otra.hashCode());
        assertNotEquals(entidad, new ProductoCaracteristica(UUID.randomUUID()));
        assertNotEquals(entidad, null);
        assertNotEquals(entidad, new Object());
    }

    @Test
    void identificadorNullSeComparaSinExcepciones() {
        ProductoCaracteristica sinId = new ProductoCaracteristica();
        ProductoCaracteristica conId = new ProductoCaracteristica(UUID.randomUUID());
        // Comportamiento actual: dos entidades sin identificador se consideran iguales.
        assertEquals(sinId, new ProductoCaracteristica());
        assertNotEquals(sinId, conId);
        assertNotEquals(conId, sinId);
        assertEquals(0, sinId.hashCode());
    }

    @Test
    void hashCodeCorrespondeAlIdentificador() {
        UUID id = UUID.randomUUID();
        ProductoCaracteristica entidad = new ProductoCaracteristica(id);
        assertEquals(id.hashCode(), entidad.hashCode());
        entidad.setIdProductoCaracteristica(null);
        assertEquals(0, entidad.hashCode());
    }

    @Test
    void toStringIncluyeClaseEIdentificador() {
        UUID id = UUID.randomUUID();
        assertEquals("sv.edu.ues.occ.ingenieria.ppi115_2026.cafefe.entity.ProductoCaracteristica[ idProductoCaracteristica=" + id + " ]",
                new ProductoCaracteristica(id).toString());
        assertEquals("sv.edu.ues.occ.ingenieria.ppi115_2026.cafefe.entity.ProductoCaracteristica[ idProductoCaracteristica=null ]",
                new ProductoCaracteristica().toString());
    }
}
