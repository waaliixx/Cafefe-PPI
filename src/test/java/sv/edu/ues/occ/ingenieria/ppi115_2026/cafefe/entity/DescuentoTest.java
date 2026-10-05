package sv.edu.ues.occ.ingenieria.ppi115_2026.cafefe.entity;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/** Pruebas unitarias de Descuento, sin persistencia ni servidor. */
class DescuentoTest {

    @Test
    void constructorVacioInicializaAtributosEnNull() {
        Descuento entidad = new Descuento();
        assertNull(entidad.getIdDescuento());
        assertNull(entidad.getIdTipoDescuento());
        assertNull(entidad.getNombre());
        assertNull(entidad.getFechaDesde());
        assertNull(entidad.getFechaHasta());
        assertNull(entidad.getObservaciones());
        assertNull(entidad.getDescuentoProductoCollection());
    }

    @Test
    void constructorConIdConservaIdentificador() {
        UUID id = UUID.fromString("00000000-0000-0000-0000-000000000001");
        Descuento entidad = new Descuento(id);
        assertEquals(id, entidad.getIdDescuento());
    }

    @Test
    void asignarIdDescuentoPermiteActualizarYLimpiar() {
        Descuento entidad = new Descuento();
        UUID inicial = UUID.fromString("00000000-0000-0000-0000-000000000001");
        UUID actualizado = UUID.fromString("00000000-0000-0000-0000-000000000002");
        entidad.setIdDescuento(inicial);
        assertEquals(inicial, entidad.getIdDescuento());
        entidad.setIdDescuento(actualizado);
        assertEquals(actualizado, entidad.getIdDescuento());
        entidad.setIdDescuento(null);
        assertNull(entidad.getIdDescuento());
    }

    @Test
    void asignarIdTipoDescuentoPermiteActualizarYLimpiar() {
        Descuento entidad = new Descuento();
        UUID inicial = UUID.fromString("00000000-0000-0000-0000-000000000001");
        UUID actualizado = UUID.fromString("00000000-0000-0000-0000-000000000002");
        entidad.setIdTipoDescuento(inicial);
        assertEquals(inicial, entidad.getIdTipoDescuento());
        entidad.setIdTipoDescuento(actualizado);
        assertEquals(actualizado, entidad.getIdTipoDescuento());
        entidad.setIdTipoDescuento(null);
        assertNull(entidad.getIdTipoDescuento());
    }

    @Test
    void asignarNombrePermiteActualizarYLimpiar() {
        Descuento entidad = new Descuento();
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
    void asignarFechaDesdePermiteActualizarYLimpiar() {
        Descuento entidad = new Descuento();
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
        Descuento entidad = new Descuento();
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
    void asignarObservacionesPermiteActualizarYLimpiar() {
        Descuento entidad = new Descuento();
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
    void asignarDescuentoProductoCollectionPermiteActualizarYLimpiar() {
        Descuento entidad = new Descuento();
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
    void igualdadDependeDelIdentificador() {
        UUID id = UUID.randomUUID();
        Descuento entidad = new Descuento(id);
        Descuento otra = new Descuento(id);
        Descuento tercera = new Descuento(id);
        assertEquals(entidad, entidad);
        assertEquals(entidad, otra);
        assertEquals(otra, entidad);
        assertEquals(otra, tercera);
        assertEquals(entidad, tercera);
        assertEquals(entidad.hashCode(), otra.hashCode());
        assertNotEquals(entidad, new Descuento(UUID.randomUUID()));
        assertNotEquals(entidad, null);
        assertNotEquals(entidad, new Object());
    }

    @Test
    void identificadorNullSeComparaSinExcepciones() {
        Descuento sinId = new Descuento();
        Descuento conId = new Descuento(UUID.randomUUID());
        // Comportamiento actual: dos entidades sin identificador se consideran iguales.
        assertEquals(sinId, new Descuento());
        assertNotEquals(sinId, conId);
        assertNotEquals(conId, sinId);
        assertEquals(0, sinId.hashCode());
    }

    @Test
    void hashCodeCorrespondeAlIdentificador() {
        UUID id = UUID.randomUUID();
        Descuento entidad = new Descuento(id);
        assertEquals(id.hashCode(), entidad.hashCode());
        entidad.setIdDescuento(null);
        assertEquals(0, entidad.hashCode());
    }

    @Test
    void toStringIncluyeClaseEIdentificador() {
        UUID id = UUID.randomUUID();
        assertEquals("sv.edu.ues.occ.ingenieria.ppi115_2026.cafefe.entity.Descuento[ idDescuento=" + id + " ]",
                new Descuento(id).toString());
        assertEquals("sv.edu.ues.occ.ingenieria.ppi115_2026.cafefe.entity.Descuento[ idDescuento=null ]",
                new Descuento().toString());
    }
}
