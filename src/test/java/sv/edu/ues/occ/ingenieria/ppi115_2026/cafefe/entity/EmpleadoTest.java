package sv.edu.ues.occ.ingenieria.ppi115_2026.cafefe.entity;

import java.util.UUID;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/** Pruebas unitarias de Empleado, sin persistencia ni servidor. */
class EmpleadoTest {

    @Test
    void constructorVacioInicializaAtributosEnNull() {
        Empleado entidad = new Empleado();
        assertNull(entidad.getIdEmpleado());
        assertNull(entidad.getNombre());
        assertNull(entidad.getApellido());
        assertNull(entidad.getActivo());
        assertNull(entidad.getComentarios());
    }

    @Test
    void constructorConIdConservaIdentificador() {
        UUID id = UUID.fromString("00000000-0000-0000-0000-000000000001");
        Empleado entidad = new Empleado(id);
        assertEquals(id, entidad.getIdEmpleado());
    }

    @Test
    void asignarIdEmpleadoPermiteActualizarYLimpiar() {
        Empleado entidad = new Empleado();
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
    void asignarNombrePermiteActualizarYLimpiar() {
        Empleado entidad = new Empleado();
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
    void asignarApellidoPermiteActualizarYLimpiar() {
        Empleado entidad = new Empleado();
        String inicial = "Valor inicial";
        String actualizado = "Valor actualizado";
        entidad.setApellido(inicial);
        assertEquals(inicial, entidad.getApellido());
        entidad.setApellido(actualizado);
        assertEquals(actualizado, entidad.getApellido());
        entidad.setApellido(null);
        assertNull(entidad.getApellido());
    }

    @Test
    void asignarActivoPermiteActualizarYLimpiar() {
        Empleado entidad = new Empleado();
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
        Empleado entidad = new Empleado();
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
    void igualdadDependeDelIdentificador() {
        UUID id = UUID.randomUUID();
        Empleado entidad = new Empleado(id);
        Empleado otra = new Empleado(id);
        Empleado tercera = new Empleado(id);
        assertEquals(entidad, entidad);
        assertEquals(entidad, otra);
        assertEquals(otra, entidad);
        assertEquals(otra, tercera);
        assertEquals(entidad, tercera);
        assertEquals(entidad.hashCode(), otra.hashCode());
        assertNotEquals(entidad, new Empleado(UUID.randomUUID()));
        assertNotEquals(entidad, null);
        assertNotEquals(entidad, new Object());
    }

    @Test
    void identificadorNullSeComparaSinExcepciones() {
        Empleado sinId = new Empleado();
        Empleado conId = new Empleado(UUID.randomUUID());
        // Comportamiento actual: dos entidades sin identificador se consideran iguales.
        assertEquals(sinId, new Empleado());
        assertNotEquals(sinId, conId);
        assertNotEquals(conId, sinId);
        assertEquals(0, sinId.hashCode());
    }

    @Test
    void hashCodeCorrespondeAlIdentificador() {
        UUID id = UUID.randomUUID();
        Empleado entidad = new Empleado(id);
        assertEquals(id.hashCode(), entidad.hashCode());
        entidad.setIdEmpleado(null);
        assertEquals(0, entidad.hashCode());
    }

    @Test
    void toStringIncluyeClaseEIdentificador() {
        UUID id = UUID.randomUUID();
        assertEquals("sv.edu.ues.occ.ingenieria.ppi115_2026.cafefe.entity.Empleado[ idEmpleado=" + id + " ]",
                new Empleado(id).toString());
        assertEquals("sv.edu.ues.occ.ingenieria.ppi115_2026.cafefe.entity.Empleado[ idEmpleado=null ]",
                new Empleado().toString());
    }
}
