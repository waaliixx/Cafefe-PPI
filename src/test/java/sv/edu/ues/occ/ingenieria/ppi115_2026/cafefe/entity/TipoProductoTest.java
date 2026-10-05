package sv.edu.ues.occ.ingenieria.ppi115_2026.cafefe.entity;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/** Pruebas unitarias de TipoProducto, sin persistencia ni servidor. */
class TipoProductoTest {

    @Test
    void constructorVacioInicializaAtributosEnNull() {
        TipoProducto entidad = new TipoProducto();
        assertNull(entidad.getIdTipoProducto());
        assertNull(entidad.getNombre());
        assertNull(entidad.getActivo());
        assertNull(entidad.getObservaciones());
        assertNull(entidad.getProductoTipoProductoCollection());
    }

    @Test
    void constructorConIdConservaIdentificador() {
        UUID id = UUID.fromString("00000000-0000-0000-0000-000000000001");
        TipoProducto entidad = new TipoProducto(id);
        assertEquals(id, entidad.getIdTipoProducto());
    }

    @Test
    void asignarIdTipoProductoPermiteActualizarYLimpiar() {
        TipoProducto entidad = new TipoProducto();
        UUID inicial = UUID.fromString("00000000-0000-0000-0000-000000000001");
        UUID actualizado = UUID.fromString("00000000-0000-0000-0000-000000000002");
        entidad.setIdTipoProducto(inicial);
        assertEquals(inicial, entidad.getIdTipoProducto());
        entidad.setIdTipoProducto(actualizado);
        assertEquals(actualizado, entidad.getIdTipoProducto());
        entidad.setIdTipoProducto(null);
        assertNull(entidad.getIdTipoProducto());
    }

    @Test
    void asignarNombrePermiteActualizarYLimpiar() {
        TipoProducto entidad = new TipoProducto();
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
        TipoProducto entidad = new TipoProducto();
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
    void asignarObservacionesPermiteActualizarYLimpiar() {
        TipoProducto entidad = new TipoProducto();
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
    void asignarProductoTipoProductoCollectionPermiteActualizarYLimpiar() {
        TipoProducto entidad = new TipoProducto();
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
        TipoProducto entidad = new TipoProducto(id);
        TipoProducto otra = new TipoProducto(id);
        TipoProducto tercera = new TipoProducto(id);
        assertEquals(entidad, entidad);
        assertEquals(entidad, otra);
        assertEquals(otra, entidad);
        assertEquals(otra, tercera);
        assertEquals(entidad, tercera);
        assertEquals(entidad.hashCode(), otra.hashCode());
        assertNotEquals(entidad, new TipoProducto(UUID.randomUUID()));
        assertNotEquals(entidad, null);
        assertNotEquals(entidad, new Object());
    }

    @Test
    void identificadorNullSeComparaSinExcepciones() {
        TipoProducto sinId = new TipoProducto();
        TipoProducto conId = new TipoProducto(UUID.randomUUID());
        // Comportamiento actual: dos entidades sin identificador se consideran iguales.
        assertEquals(sinId, new TipoProducto());
        assertNotEquals(sinId, conId);
        assertNotEquals(conId, sinId);
        assertEquals(0, sinId.hashCode());
    }

    @Test
    void hashCodeCorrespondeAlIdentificador() {
        UUID id = UUID.randomUUID();
        TipoProducto entidad = new TipoProducto(id);
        assertEquals(id.hashCode(), entidad.hashCode());
        entidad.setIdTipoProducto(null);
        assertEquals(0, entidad.hashCode());
    }

    @Test
    void toStringIncluyeClaseEIdentificador() {
        UUID id = UUID.randomUUID();
        assertEquals("sv.edu.ues.occ.ingenieria.ppi115_2026.cafefe.entity.TipoProducto[ idTipoProducto=" + id + " ]",
                new TipoProducto(id).toString());
        assertEquals("sv.edu.ues.occ.ingenieria.ppi115_2026.cafefe.entity.TipoProducto[ idTipoProducto=null ]",
                new TipoProducto().toString());
    }
}
