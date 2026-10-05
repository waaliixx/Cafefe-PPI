package sv.edu.ues.occ.ingenieria.ppi115_2026.cafefe.entity;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/** Pruebas unitarias de OrdenProducto, sin persistencia ni servidor. */
class OrdenProductoTest {

    @Test
    void constructorVacioInicializaAtributosEnNull() {
        OrdenProducto entidad = new OrdenProducto();
        assertNull(entidad.getIdOrdenProducto());
        assertNull(entidad.getPrecio());
        assertNull(entidad.getObservaciones());
        assertNull(entidad.getFacturaOrdenProductoCollection());
        assertNull(entidad.getIdOrden());
        assertNull(entidad.getIdProducto());
    }

    @Test
    void constructorConIdConservaIdentificador() {
        UUID id = UUID.fromString("00000000-0000-0000-0000-000000000001");
        OrdenProducto entidad = new OrdenProducto(id);
        assertEquals(id, entidad.getIdOrdenProducto());
    }

    @Test
    void asignarIdOrdenProductoPermiteActualizarYLimpiar() {
        OrdenProducto entidad = new OrdenProducto();
        UUID inicial = UUID.fromString("00000000-0000-0000-0000-000000000001");
        UUID actualizado = UUID.fromString("00000000-0000-0000-0000-000000000002");
        entidad.setIdOrdenProducto(inicial);
        assertEquals(inicial, entidad.getIdOrdenProducto());
        entidad.setIdOrdenProducto(actualizado);
        assertEquals(actualizado, entidad.getIdOrdenProducto());
        entidad.setIdOrdenProducto(null);
        assertNull(entidad.getIdOrdenProducto());
    }

    @Test
    void asignarPrecioPermiteActualizarYLimpiar() {
        OrdenProducto entidad = new OrdenProducto();
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
        OrdenProducto entidad = new OrdenProducto();
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
    void asignarFacturaOrdenProductoCollectionPermiteActualizarYLimpiar() {
        OrdenProducto entidad = new OrdenProducto();
        Collection<FacturaOrdenProducto> inicial = new ArrayList<>(List.of(new FacturaOrdenProducto(UUID.randomUUID())));
        Collection<FacturaOrdenProducto> actualizado = new ArrayList<>();
        entidad.setFacturaOrdenProductoCollection(inicial);
        assertSame(inicial, entidad.getFacturaOrdenProductoCollection());
        entidad.setFacturaOrdenProductoCollection(actualizado);
        assertSame(actualizado, entidad.getFacturaOrdenProductoCollection());
        entidad.setFacturaOrdenProductoCollection(null);
        assertNull(entidad.getFacturaOrdenProductoCollection());
    }

    @Test
    void asignarIdOrdenPermiteActualizarYLimpiar() {
        OrdenProducto entidad = new OrdenProducto();
        Orden inicial = new Orden(UUID.fromString("00000000-0000-0000-0000-000000000001"));
        Orden actualizado = new Orden(UUID.fromString("00000000-0000-0000-0000-000000000002"));
        entidad.setIdOrden(inicial);
        assertSame(inicial, entidad.getIdOrden());
        entidad.setIdOrden(actualizado);
        assertSame(actualizado, entidad.getIdOrden());
        entidad.setIdOrden(null);
        assertNull(entidad.getIdOrden());
    }

    @Test
    void asignarIdProductoPermiteActualizarYLimpiar() {
        OrdenProducto entidad = new OrdenProducto();
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
        OrdenProducto entidad = new OrdenProducto(id);
        OrdenProducto otra = new OrdenProducto(id);
        OrdenProducto tercera = new OrdenProducto(id);
        assertEquals(entidad, entidad);
        assertEquals(entidad, otra);
        assertEquals(otra, entidad);
        assertEquals(otra, tercera);
        assertEquals(entidad, tercera);
        assertEquals(entidad.hashCode(), otra.hashCode());
        assertNotEquals(entidad, new OrdenProducto(UUID.randomUUID()));
        assertNotEquals(entidad, null);
        assertNotEquals(entidad, new Object());
    }

    @Test
    void identificadorNullSeComparaSinExcepciones() {
        OrdenProducto sinId = new OrdenProducto();
        OrdenProducto conId = new OrdenProducto(UUID.randomUUID());
        // Comportamiento actual: dos entidades sin identificador se consideran iguales.
        assertEquals(sinId, new OrdenProducto());
        assertNotEquals(sinId, conId);
        assertNotEquals(conId, sinId);
        assertEquals(0, sinId.hashCode());
    }

    @Test
    void hashCodeCorrespondeAlIdentificador() {
        UUID id = UUID.randomUUID();
        OrdenProducto entidad = new OrdenProducto(id);
        assertEquals(id.hashCode(), entidad.hashCode());
        entidad.setIdOrdenProducto(null);
        assertEquals(0, entidad.hashCode());
    }

    @Test
    void toStringIncluyeClaseEIdentificador() {
        UUID id = UUID.randomUUID();
        assertEquals("sv.edu.ues.occ.ingenieria.ppi115_2026.cafefe.entity.OrdenProducto[ idOrdenProducto=" + id + " ]",
                new OrdenProducto(id).toString());
        assertEquals("sv.edu.ues.occ.ingenieria.ppi115_2026.cafefe.entity.OrdenProducto[ idOrdenProducto=null ]",
                new OrdenProducto().toString());
    }
}
