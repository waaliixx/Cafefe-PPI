package sv.edu.ues.occ.ingenieria.ppi115_2026.cafefe.entity;

import java.util.UUID;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/** Pruebas unitarias de TipoDescuento, sin persistencia ni servidor. */
class TipoDescuentoTest {

    @Test
    void constructorVacioInicializaAtributosEnNull() {
        TipoDescuento entidad = new TipoDescuento();
        assertNull(entidad.getIdTipoDescuento());
        assertNull(entidad.getNombre());
        assertNull(entidad.getActivo());
        assertNull(entidad.getDescuentoMaximo());
        assertNull(entidad.getObservaciones());
    }

    @Test
    void constructorConIdConservaIdentificador() {
        UUID id = UUID.fromString("00000000-0000-0000-0000-000000000001");
        TipoDescuento entidad = new TipoDescuento(id);
        assertEquals(id, entidad.getIdTipoDescuento());
    }

    @Test
    void asignarIdTipoDescuentoPermiteActualizarYLimpiar() {
        TipoDescuento entidad = new TipoDescuento();
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
        TipoDescuento entidad = new TipoDescuento();
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
        TipoDescuento entidad = new TipoDescuento();
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
    void asignarDescuentoMaximoPermiteActualizarYLimpiar() {
        TipoDescuento entidad = new TipoDescuento();
        Integer inicial = 10;
        Integer actualizado = 20;
        entidad.setDescuentoMaximo(inicial);
        assertEquals(inicial, entidad.getDescuentoMaximo());
        entidad.setDescuentoMaximo(actualizado);
        assertEquals(actualizado, entidad.getDescuentoMaximo());
        entidad.setDescuentoMaximo(null);
        assertNull(entidad.getDescuentoMaximo());
    }

    @Test
    void asignarObservacionesPermiteActualizarYLimpiar() {
        TipoDescuento entidad = new TipoDescuento();
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
    void igualdadDependeDelIdentificador() {
        UUID id = UUID.randomUUID();
        TipoDescuento entidad = new TipoDescuento(id);
        TipoDescuento otra = new TipoDescuento(id);
        TipoDescuento tercera = new TipoDescuento(id);
        assertEquals(entidad, entidad);
        assertEquals(entidad, otra);
        assertEquals(otra, entidad);
        assertEquals(otra, tercera);
        assertEquals(entidad, tercera);
        assertEquals(entidad.hashCode(), otra.hashCode());
        assertNotEquals(entidad, new TipoDescuento(UUID.randomUUID()));
        assertNotEquals(entidad, null);
        assertNotEquals(entidad, new Object());
    }

    @Test
    void identificadorNullSeComparaSinExcepciones() {
        TipoDescuento sinId = new TipoDescuento();
        TipoDescuento conId = new TipoDescuento(UUID.randomUUID());
        // Comportamiento actual: dos entidades sin identificador se consideran iguales.
        assertEquals(sinId, new TipoDescuento());
        assertNotEquals(sinId, conId);
        assertNotEquals(conId, sinId);
        assertEquals(0, sinId.hashCode());
    }

    @Test
    void hashCodeCorrespondeAlIdentificador() {
        UUID id = UUID.randomUUID();
        TipoDescuento entidad = new TipoDescuento(id);
        assertEquals(id.hashCode(), entidad.hashCode());
        entidad.setIdTipoDescuento(null);
        assertEquals(0, entidad.hashCode());
    }

    @Test
    void toStringIncluyeClaseEIdentificador() {
        UUID id = UUID.randomUUID();
        assertEquals("sv.edu.ues.occ.ingenieria.ppi115_2026.cafefe.entity.TipoDescuento[ idTipoDescuento=" + id + " ]",
                new TipoDescuento(id).toString());
        assertEquals("sv.edu.ues.occ.ingenieria.ppi115_2026.cafefe.entity.TipoDescuento[ idTipoDescuento=null ]",
                new TipoDescuento().toString());
    }
}
