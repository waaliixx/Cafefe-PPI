package sv.edu.ues.occ.ingenieria.ppi115_2026.cafefe.entity;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/** Pruebas unitarias de Rol, sin persistencia ni servidor. */
class RolTest {

    @Test
    void constructorVacioInicializaAtributosEnNull() {
        Rol entidad = new Rol();
        assertNull(entidad.getIdRol());
        assertNull(entidad.getNombre());
        assertNull(entidad.getActivo());
        assertNull(entidad.getObservaciones());
        assertNull(entidad.getEmpleadoRolCollection());
    }

    @Test
    void constructorConIdConservaIdentificador() {
        UUID id = UUID.fromString("00000000-0000-0000-0000-000000000001");
        Rol entidad = new Rol(id);
        assertEquals(id, entidad.getIdRol());
    }

    @Test
    void asignarIdRolPermiteActualizarYLimpiar() {
        Rol entidad = new Rol();
        UUID inicial = UUID.fromString("00000000-0000-0000-0000-000000000001");
        UUID actualizado = UUID.fromString("00000000-0000-0000-0000-000000000002");
        entidad.setIdRol(inicial);
        assertEquals(inicial, entidad.getIdRol());
        entidad.setIdRol(actualizado);
        assertEquals(actualizado, entidad.getIdRol());
        entidad.setIdRol(null);
        assertNull(entidad.getIdRol());
    }

    @Test
    void asignarNombrePermiteActualizarYLimpiar() {
        Rol entidad = new Rol();
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
        Rol entidad = new Rol();
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
        Rol entidad = new Rol();
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
    void asignarEmpleadoRolCollectionPermiteActualizarYLimpiar() {
        Rol entidad = new Rol();
        Collection<EmpleadoRol> inicial = new ArrayList<>(List.of(new EmpleadoRol(UUID.randomUUID())));
        Collection<EmpleadoRol> actualizado = new ArrayList<>();
        entidad.setEmpleadoRolCollection(inicial);
        assertSame(inicial, entidad.getEmpleadoRolCollection());
        entidad.setEmpleadoRolCollection(actualizado);
        assertSame(actualizado, entidad.getEmpleadoRolCollection());
        entidad.setEmpleadoRolCollection(null);
        assertNull(entidad.getEmpleadoRolCollection());
    }

    @Test
    void igualdadDependeDelIdentificador() {
        UUID id = UUID.randomUUID();
        Rol entidad = new Rol(id);
        Rol otra = new Rol(id);
        Rol tercera = new Rol(id);
        assertEquals(entidad, entidad);
        assertEquals(entidad, otra);
        assertEquals(otra, entidad);
        assertEquals(otra, tercera);
        assertEquals(entidad, tercera);
        assertEquals(entidad.hashCode(), otra.hashCode());
        assertNotEquals(entidad, new Rol(UUID.randomUUID()));
        assertNotEquals(entidad, null);
        assertNotEquals(entidad, new Object());
    }

    @Test
    void identificadorNullSeComparaSinExcepciones() {
        Rol sinId = new Rol();
        Rol conId = new Rol(UUID.randomUUID());
        // Comportamiento actual: dos entidades sin identificador se consideran iguales.
        assertEquals(sinId, new Rol());
        assertNotEquals(sinId, conId);
        assertNotEquals(conId, sinId);
        assertEquals(0, sinId.hashCode());
    }

    @Test
    void hashCodeCorrespondeAlIdentificador() {
        UUID id = UUID.randomUUID();
        Rol entidad = new Rol(id);
        assertEquals(id.hashCode(), entidad.hashCode());
        entidad.setIdRol(null);
        assertEquals(0, entidad.hashCode());
    }

    @Test
    void toStringIncluyeClaseEIdentificador() {
        UUID id = UUID.randomUUID();
        assertEquals("sv.edu.ues.occ.ingenieria.ppi115_2026.cafefe.entity.Rol[ idRol=" + id + " ]",
                new Rol(id).toString());
        assertEquals("sv.edu.ues.occ.ingenieria.ppi115_2026.cafefe.entity.Rol[ idRol=null ]",
                new Rol().toString());
    }
}
