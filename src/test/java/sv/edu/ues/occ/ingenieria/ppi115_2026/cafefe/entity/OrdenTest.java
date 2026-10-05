package sv.edu.ues.occ.ingenieria.ppi115_2026.cafefe.entity;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/** Pruebas unitarias de Orden, sin persistencia ni servidor. */
class OrdenTest {

    @Test
    void constructorVacioInicializaAtributosEnNull() {
        Orden entidad = new Orden();
        assertNull(entidad.getIdOrden());
        assertNull(entidad.getFechaCreacion());
        assertNull(entidad.getOrdenProductoCollection());
        assertNull(entidad.getIdEmpleadoRol());
    }

    @Test
    void constructorConIdConservaIdentificador() {
        UUID id = UUID.fromString("00000000-0000-0000-0000-000000000001");
        Orden entidad = new Orden(id);
        assertEquals(id, entidad.getIdOrden());
    }

    @Test
    void asignarIdOrdenPermiteActualizarYLimpiar() {
        Orden entidad = new Orden();
        UUID inicial = UUID.fromString("00000000-0000-0000-0000-000000000001");
        UUID actualizado = UUID.fromString("00000000-0000-0000-0000-000000000002");
        entidad.setIdOrden(inicial);
        assertEquals(inicial, entidad.getIdOrden());
        entidad.setIdOrden(actualizado);
        assertEquals(actualizado, entidad.getIdOrden());
        entidad.setIdOrden(null);
        assertNull(entidad.getIdOrden());
    }

    @Test
    void asignarFechaCreacionPermiteActualizarYLimpiar() {
        Orden entidad = new Orden();
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
    void asignarOrdenProductoCollectionPermiteActualizarYLimpiar() {
        Orden entidad = new Orden();
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
    void asignarIdEmpleadoRolPermiteActualizarYLimpiar() {
        Orden entidad = new Orden();
        EmpleadoRol inicial = new EmpleadoRol(UUID.fromString("00000000-0000-0000-0000-000000000001"));
        EmpleadoRol actualizado = new EmpleadoRol(UUID.fromString("00000000-0000-0000-0000-000000000002"));
        entidad.setIdEmpleadoRol(inicial);
        assertSame(inicial, entidad.getIdEmpleadoRol());
        entidad.setIdEmpleadoRol(actualizado);
        assertSame(actualizado, entidad.getIdEmpleadoRol());
        entidad.setIdEmpleadoRol(null);
        assertNull(entidad.getIdEmpleadoRol());
    }

    @Test
    void igualdadDependeDelIdentificador() {
        UUID id = UUID.randomUUID();
        Orden entidad = new Orden(id);
        Orden otra = new Orden(id);
        Orden tercera = new Orden(id);
        assertEquals(entidad, entidad);
        assertEquals(entidad, otra);
        assertEquals(otra, entidad);
        assertEquals(otra, tercera);
        assertEquals(entidad, tercera);
        assertEquals(entidad.hashCode(), otra.hashCode());
        assertNotEquals(entidad, new Orden(UUID.randomUUID()));
        assertNotEquals(entidad, null);
        assertNotEquals(entidad, new Object());
    }

    @Test
    void identificadorNullSeComparaSinExcepciones() {
        Orden sinId = new Orden();
        Orden conId = new Orden(UUID.randomUUID());
        // Comportamiento actual: dos entidades sin identificador se consideran iguales.
        assertEquals(sinId, new Orden());
        assertNotEquals(sinId, conId);
        assertNotEquals(conId, sinId);
        assertEquals(0, sinId.hashCode());
    }

    @Test
    void hashCodeCorrespondeAlIdentificador() {
        UUID id = UUID.randomUUID();
        Orden entidad = new Orden(id);
        assertEquals(id.hashCode(), entidad.hashCode());
        entidad.setIdOrden(null);
        assertEquals(0, entidad.hashCode());
    }

    @Test
    void toStringIncluyeClaseEIdentificador() {
        UUID id = UUID.randomUUID();
        assertEquals("sv.edu.ues.occ.ingenieria.ppi115_2026.cafefe.entity.Orden[ idOrden=" + id + " ]",
                new Orden(id).toString());
        assertEquals("sv.edu.ues.occ.ingenieria.ppi115_2026.cafefe.entity.Orden[ idOrden=null ]",
                new Orden().toString());
    }
}
