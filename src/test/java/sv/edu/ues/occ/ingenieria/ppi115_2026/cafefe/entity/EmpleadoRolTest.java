package sv.edu.ues.occ.ingenieria.ppi115_2026.cafefe.entity;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/** Pruebas unitarias de EmpleadoRol, sin persistencia ni servidor. */
class EmpleadoRolTest {

    @Test
    void constructorVacioInicializaAtributosEnNull() {
        EmpleadoRol entidad = new EmpleadoRol();
        assertNull(entidad.getIdEmpleadoRol());
        assertNull(entidad.getIdEmpleado());
        assertNull(entidad.getActivo());
        assertNull(entidad.getObservaciones());
        assertNull(entidad.getIdRol());
        assertNull(entidad.getFacturaCollection());
        assertNull(entidad.getOrdenCollection());
    }

    @Test
    void constructorConIdConservaIdentificador() {
        UUID id = UUID.fromString("00000000-0000-0000-0000-000000000001");
        EmpleadoRol entidad = new EmpleadoRol(id);
        assertEquals(id, entidad.getIdEmpleadoRol());
    }

    @Test
    void asignarIdEmpleadoRolPermiteActualizarYLimpiar() {
        EmpleadoRol entidad = new EmpleadoRol();
        UUID inicial = UUID.fromString("00000000-0000-0000-0000-000000000001");
        UUID actualizado = UUID.fromString("00000000-0000-0000-0000-000000000002");
        entidad.setIdEmpleadoRol(inicial);
        assertEquals(inicial, entidad.getIdEmpleadoRol());
        entidad.setIdEmpleadoRol(actualizado);
        assertEquals(actualizado, entidad.getIdEmpleadoRol());
        entidad.setIdEmpleadoRol(null);
        assertNull(entidad.getIdEmpleadoRol());
    }

    @Test
    void asignarIdEmpleadoPermiteActualizarYLimpiar() {
        EmpleadoRol entidad = new EmpleadoRol();
        UUID inicial = UUID.fromString("00000000-0000-0000-0000-000000000001");
        UUID actualizado = UUID.fromString("00000000-0000-0000-0000-000000000002");
        entidad.setIdEmpleado(inicial);
        assertEquals(inicial, entidad.getIdEmpleado());
        entidad.setIdEmpleado(actualizado);
        assertEquals(actualizado, entidad.getIdEmpleado());
        entidad.setIdEmpleado(null);
        assertNull(entidad.getIdEmpleado());
    }

    @Test
    void asignarActivoPermiteActualizarYLimpiar() {
        EmpleadoRol entidad = new EmpleadoRol();
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
        EmpleadoRol entidad = new EmpleadoRol();
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
    void asignarIdRolPermiteActualizarYLimpiar() {
        EmpleadoRol entidad = new EmpleadoRol();
        Rol inicial = new Rol(UUID.fromString("00000000-0000-0000-0000-000000000001"));
        Rol actualizado = new Rol(UUID.fromString("00000000-0000-0000-0000-000000000002"));
        entidad.setIdRol(inicial);
        assertSame(inicial, entidad.getIdRol());
        entidad.setIdRol(actualizado);
        assertSame(actualizado, entidad.getIdRol());
        entidad.setIdRol(null);
        assertNull(entidad.getIdRol());
    }

    @Test
    void asignarFacturaCollectionPermiteActualizarYLimpiar() {
        EmpleadoRol entidad = new EmpleadoRol();
        Collection<Factura> inicial = new ArrayList<>(List.of(new Factura(UUID.randomUUID())));
        Collection<Factura> actualizado = new ArrayList<>();
        entidad.setFacturaCollection(inicial);
        assertSame(inicial, entidad.getFacturaCollection());
        entidad.setFacturaCollection(actualizado);
        assertSame(actualizado, entidad.getFacturaCollection());
        entidad.setFacturaCollection(null);
        assertNull(entidad.getFacturaCollection());
    }

    @Test
    void asignarOrdenCollectionPermiteActualizarYLimpiar() {
        EmpleadoRol entidad = new EmpleadoRol();
        Collection<Orden> inicial = new ArrayList<>(List.of(new Orden(UUID.randomUUID())));
        Collection<Orden> actualizado = new ArrayList<>();
        entidad.setOrdenCollection(inicial);
        assertSame(inicial, entidad.getOrdenCollection());
        entidad.setOrdenCollection(actualizado);
        assertSame(actualizado, entidad.getOrdenCollection());
        entidad.setOrdenCollection(null);
        assertNull(entidad.getOrdenCollection());
    }

    @Test
    void igualdadDependeDelIdentificador() {
        UUID id = UUID.randomUUID();
        EmpleadoRol entidad = new EmpleadoRol(id);
        EmpleadoRol otra = new EmpleadoRol(id);
        EmpleadoRol tercera = new EmpleadoRol(id);
        assertEquals(entidad, entidad);
        assertEquals(entidad, otra);
        assertEquals(otra, entidad);
        assertEquals(otra, tercera);
        assertEquals(entidad, tercera);
        assertEquals(entidad.hashCode(), otra.hashCode());
        assertNotEquals(entidad, new EmpleadoRol(UUID.randomUUID()));
        assertNotEquals(entidad, null);
        assertNotEquals(entidad, new Object());
    }

    @Test
    void identificadorNullSeComparaSinExcepciones() {
        EmpleadoRol sinId = new EmpleadoRol();
        EmpleadoRol conId = new EmpleadoRol(UUID.randomUUID());
        // Comportamiento actual: dos entidades sin identificador se consideran iguales.
        assertEquals(sinId, new EmpleadoRol());
        assertNotEquals(sinId, conId);
        assertNotEquals(conId, sinId);
        assertEquals(0, sinId.hashCode());
    }

    @Test
    void hashCodeCorrespondeAlIdentificador() {
        UUID id = UUID.randomUUID();
        EmpleadoRol entidad = new EmpleadoRol(id);
        assertEquals(id.hashCode(), entidad.hashCode());
        entidad.setIdEmpleadoRol(null);
        assertEquals(0, entidad.hashCode());
    }

    @Test
    void toStringIncluyeClaseEIdentificador() {
        UUID id = UUID.randomUUID();
        assertEquals("sv.edu.ues.occ.ingenieria.ppi115_2026.cafefe.entity.EmpleadoRol[ idEmpleadoRol=" + id + " ]",
                new EmpleadoRol(id).toString());
        assertEquals("sv.edu.ues.occ.ingenieria.ppi115_2026.cafefe.entity.EmpleadoRol[ idEmpleadoRol=null ]",
                new EmpleadoRol().toString());
    }
}
