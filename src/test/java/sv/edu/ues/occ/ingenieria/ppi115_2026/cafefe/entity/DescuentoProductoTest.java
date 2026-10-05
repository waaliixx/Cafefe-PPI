package sv.edu.ues.occ.ingenieria.ppi115_2026.cafefe.entity;

import java.util.Date;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/** Pruebas unitarias de DescuentoProducto, sin persistencia ni servidor. */
class DescuentoProductoTest {

    @Test
    void constructorVacioInicializaAtributosEnNull() {
        DescuentoProducto entidad = new DescuentoProducto();
        assertNull(entidad.getIdDescuentoProducto());
        assertNull(entidad.getFechaDesde());
        assertNull(entidad.getFechaHasta());
        assertNull(entidad.getValor());
        assertNull(entidad.getObservaciones());
        assertNull(entidad.getIdDescuento());
        assertNull(entidad.getIdProducto());
    }

    @Test
    void constructorConIdConservaIdentificador() {
        UUID id = UUID.fromString("00000000-0000-0000-0000-000000000001");
        DescuentoProducto entidad = new DescuentoProducto(id);
        assertEquals(id, entidad.getIdDescuentoProducto());
    }

    @Test
    void asignarIdDescuentoProductoPermiteActualizarYLimpiar() {
        DescuentoProducto entidad = new DescuentoProducto();
        UUID inicial = UUID.fromString("00000000-0000-0000-0000-000000000001");
        UUID actualizado = UUID.fromString("00000000-0000-0000-0000-000000000002");
        entidad.setIdDescuentoProducto(inicial);
        assertEquals(inicial, entidad.getIdDescuentoProducto());
        entidad.setIdDescuentoProducto(actualizado);
        assertEquals(actualizado, entidad.getIdDescuentoProducto());
        entidad.setIdDescuentoProducto(null);
        assertNull(entidad.getIdDescuentoProducto());
    }

    @Test
    void asignarFechaDesdePermiteActualizarYLimpiar() {
        DescuentoProducto entidad = new DescuentoProducto();
        Date inicial = new Date(1700000000000L);
        Date actualizado = new Date(1700086400000L);
        entidad.setFechaDesde(inicial);
        assertEquals(inicial, entidad.getFechaDesde());
        entidad.setFechaDesde(actualizado);
        assertEquals(actualizado, entidad.getFechaDesde());
        entidad.setFechaDesde(null);
        assertNull(entidad.getFechaDesde());
    }

    @Test
    void asignarFechaHastaPermiteActualizarYLimpiar() {
        DescuentoProducto entidad = new DescuentoProducto();
        Date inicial = new Date(1700000000000L);
        Date actualizado = new Date(1700086400000L);
        entidad.setFechaHasta(inicial);
        assertEquals(inicial, entidad.getFechaHasta());
        entidad.setFechaHasta(actualizado);
        assertEquals(actualizado, entidad.getFechaHasta());
        entidad.setFechaHasta(null);
        assertNull(entidad.getFechaHasta());
    }

    @Test
    void asignarValorPermiteActualizarYLimpiar() {
        DescuentoProducto entidad = new DescuentoProducto();
        Integer inicial = 10;
        Integer actualizado = 20;
        entidad.setValor(inicial);
        assertEquals(inicial, entidad.getValor());
        entidad.setValor(actualizado);
        assertEquals(actualizado, entidad.getValor());
        entidad.setValor(null);
        assertNull(entidad.getValor());
    }

    @Test
    void asignarObservacionesPermiteActualizarYLimpiar() {
        DescuentoProducto entidad = new DescuentoProducto();
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
    void asignarIdDescuentoPermiteActualizarYLimpiar() {
        DescuentoProducto entidad = new DescuentoProducto();
        Descuento inicial = new Descuento(UUID.fromString("00000000-0000-0000-0000-000000000001"));
        Descuento actualizado = new Descuento(UUID.fromString("00000000-0000-0000-0000-000000000002"));
        entidad.setIdDescuento(inicial);
        assertSame(inicial, entidad.getIdDescuento());
        entidad.setIdDescuento(actualizado);
        assertSame(actualizado, entidad.getIdDescuento());
        entidad.setIdDescuento(null);
        assertNull(entidad.getIdDescuento());
    }

    @Test
    void asignarIdProductoPermiteActualizarYLimpiar() {
        DescuentoProducto entidad = new DescuentoProducto();
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
        DescuentoProducto entidad = new DescuentoProducto(id);
        DescuentoProducto otra = new DescuentoProducto(id);
        DescuentoProducto tercera = new DescuentoProducto(id);
        assertEquals(entidad, entidad);
        assertEquals(entidad, otra);
        assertEquals(otra, entidad);
        assertEquals(otra, tercera);
        assertEquals(entidad, tercera);
        assertEquals(entidad.hashCode(), otra.hashCode());
        assertNotEquals(entidad, new DescuentoProducto(UUID.randomUUID()));
        assertNotEquals(entidad, null);
        assertNotEquals(entidad, new Object());
    }

    @Test
    void identificadorNullSeComparaSinExcepciones() {
        DescuentoProducto sinId = new DescuentoProducto();
        DescuentoProducto conId = new DescuentoProducto(UUID.randomUUID());
        // Comportamiento actual: dos entidades sin identificador se consideran iguales.
        assertEquals(sinId, new DescuentoProducto());
        assertNotEquals(sinId, conId);
        assertNotEquals(conId, sinId);
        assertEquals(0, sinId.hashCode());
    }

    @Test
    void hashCodeCorrespondeAlIdentificador() {
        UUID id = UUID.randomUUID();
        DescuentoProducto entidad = new DescuentoProducto(id);
        assertEquals(id.hashCode(), entidad.hashCode());
        entidad.setIdDescuentoProducto(null);
        assertEquals(0, entidad.hashCode());
    }

    @Test
    void toStringIncluyeClaseEIdentificador() {
        UUID id = UUID.randomUUID();
        assertEquals("sv.edu.ues.occ.ingenieria.ppi115_2026.cafefe.entity.DescuentoProducto[ idDescuentoProducto=" + id + " ]",
                new DescuentoProducto(id).toString());
        assertEquals("sv.edu.ues.occ.ingenieria.ppi115_2026.cafefe.entity.DescuentoProducto[ idDescuentoProducto=null ]",
                new DescuentoProducto().toString());
    }
}
