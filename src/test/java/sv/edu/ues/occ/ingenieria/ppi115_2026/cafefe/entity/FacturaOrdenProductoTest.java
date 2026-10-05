package sv.edu.ues.occ.ingenieria.ppi115_2026.cafefe.entity;

import java.math.BigDecimal;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/** Pruebas unitarias de FacturaOrdenProducto, sin persistencia ni servidor. */
class FacturaOrdenProductoTest {

    @Test
    void constructorVacioInicializaAtributosEnNull() {
        FacturaOrdenProducto entidad = new FacturaOrdenProducto();
        assertNull(entidad.getIdFacturaOrdenProducto());
        assertNull(entidad.getPrecio());
        assertNull(entidad.getObservaciones());
        assertNull(entidad.getIdFactura());
        assertNull(entidad.getIdOrdenProducto());
    }

    @Test
    void constructorConIdConservaIdentificador() {
        UUID id = UUID.fromString("00000000-0000-0000-0000-000000000001");
        FacturaOrdenProducto entidad = new FacturaOrdenProducto(id);
        assertEquals(id, entidad.getIdFacturaOrdenProducto());
    }

    @Test
    void asignarIdFacturaOrdenProductoPermiteActualizarYLimpiar() {
        FacturaOrdenProducto entidad = new FacturaOrdenProducto();
        UUID inicial = UUID.fromString("00000000-0000-0000-0000-000000000001");
        UUID actualizado = UUID.fromString("00000000-0000-0000-0000-000000000002");
        entidad.setIdFacturaOrdenProducto(inicial);
        assertEquals(inicial, entidad.getIdFacturaOrdenProducto());
        entidad.setIdFacturaOrdenProducto(actualizado);
        assertEquals(actualizado, entidad.getIdFacturaOrdenProducto());
        entidad.setIdFacturaOrdenProducto(null);
        assertNull(entidad.getIdFacturaOrdenProducto());
    }

    @Test
    void asignarPrecioPermiteActualizarYLimpiar() {
        FacturaOrdenProducto entidad = new FacturaOrdenProducto();
        BigDecimal inicial = new BigDecimal("2.50");
        BigDecimal actualizado = new BigDecimal("3.75");
        entidad.setPrecio(inicial);
        assertEquals(inicial, entidad.getPrecio());
        entidad.setPrecio(actualizado);
        assertEquals(actualizado, entidad.getPrecio());
        entidad.setPrecio(null);
        assertNull(entidad.getPrecio());
    }

    @Test
    void asignarObservacionesPermiteActualizarYLimpiar() {
        FacturaOrdenProducto entidad = new FacturaOrdenProducto();
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
    void asignarIdFacturaPermiteActualizarYLimpiar() {
        FacturaOrdenProducto entidad = new FacturaOrdenProducto();
        Factura inicial = new Factura(UUID.fromString("00000000-0000-0000-0000-000000000001"));
        Factura actualizado = new Factura(UUID.fromString("00000000-0000-0000-0000-000000000002"));
        entidad.setIdFactura(inicial);
        assertSame(inicial, entidad.getIdFactura());
        entidad.setIdFactura(actualizado);
        assertSame(actualizado, entidad.getIdFactura());
        entidad.setIdFactura(null);
        assertNull(entidad.getIdFactura());
    }

    @Test
    void asignarIdOrdenProductoPermiteActualizarYLimpiar() {
        FacturaOrdenProducto entidad = new FacturaOrdenProducto();
        OrdenProducto inicial = new OrdenProducto(UUID.fromString("00000000-0000-0000-0000-000000000001"));
        OrdenProducto actualizado = new OrdenProducto(UUID.fromString("00000000-0000-0000-0000-000000000002"));
        entidad.setIdOrdenProducto(inicial);
        assertSame(inicial, entidad.getIdOrdenProducto());
        entidad.setIdOrdenProducto(actualizado);
        assertSame(actualizado, entidad.getIdOrdenProducto());
        entidad.setIdOrdenProducto(null);
        assertNull(entidad.getIdOrdenProducto());
    }

    @Test
    void igualdadDependeDelIdentificador() {
        UUID id = UUID.randomUUID();
        FacturaOrdenProducto entidad = new FacturaOrdenProducto(id);
        FacturaOrdenProducto otra = new FacturaOrdenProducto(id);
        FacturaOrdenProducto tercera = new FacturaOrdenProducto(id);
        assertEquals(entidad, entidad);
        assertEquals(entidad, otra);
        assertEquals(otra, entidad);
        assertEquals(otra, tercera);
        assertEquals(entidad, tercera);
        assertEquals(entidad.hashCode(), otra.hashCode());
        assertNotEquals(entidad, new FacturaOrdenProducto(UUID.randomUUID()));
        assertNotEquals(entidad, null);
        assertNotEquals(entidad, new Object());
    }

    @Test
    void identificadorNullSeComparaSinExcepciones() {
        FacturaOrdenProducto sinId = new FacturaOrdenProducto();
        FacturaOrdenProducto conId = new FacturaOrdenProducto(UUID.randomUUID());
        // Comportamiento actual: dos entidades sin identificador se consideran iguales.
        assertEquals(sinId, new FacturaOrdenProducto());
        assertNotEquals(sinId, conId);
        assertNotEquals(conId, sinId);
        assertEquals(0, sinId.hashCode());
    }

    @Test
    void hashCodeCorrespondeAlIdentificador() {
        UUID id = UUID.randomUUID();
        FacturaOrdenProducto entidad = new FacturaOrdenProducto(id);
        assertEquals(id.hashCode(), entidad.hashCode());
        entidad.setIdFacturaOrdenProducto(null);
        assertEquals(0, entidad.hashCode());
    }

    @Test
    void toStringIncluyeClaseEIdentificador() {
        UUID id = UUID.randomUUID();
        assertEquals("sv.edu.ues.occ.ingenieria.ppi115_2026.cafefe.entity.FacturaOrdenProducto[ idFacturaOrdenProducto=" + id + " ]",
                new FacturaOrdenProducto(id).toString());
        assertEquals("sv.edu.ues.occ.ingenieria.ppi115_2026.cafefe.entity.FacturaOrdenProducto[ idFacturaOrdenProducto=null ]",
                new FacturaOrdenProducto().toString());
    }
}
