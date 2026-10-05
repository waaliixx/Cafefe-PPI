package sv.edu.ues.occ.ingenieria.ppi115_2026.cafefe.entity;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/** Pruebas unitarias de Caja, sin persistencia ni servidor. */
class CajaTest {

    @Test
    void constructorVacioInicializaAtributosEnNull() {
        Caja entidad = new Caja();
        assertNull(entidad.getIdCaja());
        assertNull(entidad.getNombre());
        assertNull(entidad.getActivo());
        assertNull(entidad.getObservaciones());
        assertNull(entidad.getFacturaCollection());
    }

    @Test
    void constructorConIdConservaIdentificador() {
        UUID id = UUID.fromString("00000000-0000-0000-0000-000000000001");
        Caja entidad = new Caja(id);
        assertEquals(id, entidad.getIdCaja());
    }

    @Test
    void asignarIdCajaPermiteActualizarYLimpiar() {
        Caja entidad = new Caja();
        UUID inicial = UUID.fromString("00000000-0000-0000-0000-000000000001");
        UUID actualizado = UUID.fromString("00000000-0000-0000-0000-000000000002");
        entidad.setIdCaja(inicial);
        assertEquals(inicial, entidad.getIdCaja());
        entidad.setIdCaja(actualizado);
        assertEquals(actualizado, entidad.getIdCaja());
        entidad.setIdCaja(null);
        assertNull(entidad.getIdCaja());
    }

    @Test
    void asignarNombrePermiteActualizarYLimpiar() {
        Caja entidad = new Caja();
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
        Caja entidad = new Caja();
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
        Caja entidad = new Caja();
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
    void asignarFacturaCollectionPermiteActualizarYLimpiar() {
        Caja entidad = new Caja();
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
    void igualdadDependeDelIdentificador() {
        UUID id = UUID.randomUUID();
        Caja entidad = new Caja(id);
        Caja otra = new Caja(id);
        Caja tercera = new Caja(id);
        assertEquals(entidad, entidad);
        assertEquals(entidad, otra);
        assertEquals(otra, entidad);
        assertEquals(otra, tercera);
        assertEquals(entidad, tercera);
        assertEquals(entidad.hashCode(), otra.hashCode());
        assertNotEquals(entidad, new Caja(UUID.randomUUID()));
        assertNotEquals(entidad, null);
        assertNotEquals(entidad, new Object());
    }

    @Test
    void identificadorNullSeComparaSinExcepciones() {
        Caja sinId = new Caja();
        Caja conId = new Caja(UUID.randomUUID());
        // Comportamiento actual: dos entidades sin identificador se consideran iguales.
        assertEquals(sinId, new Caja());
        assertNotEquals(sinId, conId);
        assertNotEquals(conId, sinId);
        assertEquals(0, sinId.hashCode());
    }

    @Test
    void hashCodeCorrespondeAlIdentificador() {
        UUID id = UUID.randomUUID();
        Caja entidad = new Caja(id);
        assertEquals(id.hashCode(), entidad.hashCode());
        entidad.setIdCaja(null);
        assertEquals(0, entidad.hashCode());
    }

    @Test
    void toStringIncluyeClaseEIdentificador() {
        UUID id = UUID.randomUUID();
        assertEquals("sv.edu.ues.occ.ingenieria.ppi115_2026.cafefe.entity.Caja[ idCaja=" + id + " ]",
                new Caja(id).toString());
        assertEquals("sv.edu.ues.occ.ingenieria.ppi115_2026.cafefe.entity.Caja[ idCaja=null ]",
                new Caja().toString());
    }
}
