package sv.edu.ues.occ.ingenieria.ppi115_2026.cafefe.entity;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/** Pruebas unitarias de TipoCaracteristica, sin persistencia ni servidor. */
class TipoCaracteristicaTest {

    @Test
    void constructorVacioInicializaAtributosEnNull() {
        TipoCaracteristica entidad = new TipoCaracteristica();
        assertNull(entidad.getIdTipoCaracteristica());
        assertNull(entidad.getNombre());
        assertNull(entidad.getExpresionRegular());
        assertNull(entidad.getActivo());
        assertNull(entidad.getObservaciones());
        assertNull(entidad.getCaracteristicaCollection());
    }

    @Test
    void constructorConIdConservaIdentificador() {
        UUID id = UUID.fromString("00000000-0000-0000-0000-000000000001");
        TipoCaracteristica entidad = new TipoCaracteristica(id);
        assertEquals(id, entidad.getIdTipoCaracteristica());
    }

    @Test
    void asignarIdTipoCaracteristicaPermiteActualizarYLimpiar() {
        TipoCaracteristica entidad = new TipoCaracteristica();
        UUID inicial = UUID.fromString("00000000-0000-0000-0000-000000000001");
        UUID actualizado = UUID.fromString("00000000-0000-0000-0000-000000000002");
        entidad.setIdTipoCaracteristica(inicial);
        assertEquals(inicial, entidad.getIdTipoCaracteristica());
        entidad.setIdTipoCaracteristica(actualizado);
        assertEquals(actualizado, entidad.getIdTipoCaracteristica());
        entidad.setIdTipoCaracteristica(null);
        assertNull(entidad.getIdTipoCaracteristica());
    }

    @Test
    void asignarNombrePermiteActualizarYLimpiar() {
        TipoCaracteristica entidad = new TipoCaracteristica();
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
    void asignarExpresionRegularPermiteActualizarYLimpiar() {
        TipoCaracteristica entidad = new TipoCaracteristica();
        String inicial = "Valor inicial";
        String actualizado = "Valor actualizado";
        entidad.setExpresionRegular(inicial);
        assertEquals(inicial, entidad.getExpresionRegular());
        entidad.setExpresionRegular(actualizado);
        assertEquals(actualizado, entidad.getExpresionRegular());
        entidad.setExpresionRegular(null);
        assertNull(entidad.getExpresionRegular());
    }

    @Test
    void asignarActivoPermiteActualizarYLimpiar() {
        TipoCaracteristica entidad = new TipoCaracteristica();
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
        TipoCaracteristica entidad = new TipoCaracteristica();
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
    void asignarCaracteristicaCollectionPermiteActualizarYLimpiar() {
        TipoCaracteristica entidad = new TipoCaracteristica();
        Collection<Caracteristica> inicial = new ArrayList<>(List.of(new Caracteristica(UUID.randomUUID())));
        Collection<Caracteristica> actualizado = new ArrayList<>();
        entidad.setCaracteristicaCollection(inicial);
        assertSame(inicial, entidad.getCaracteristicaCollection());
        entidad.setCaracteristicaCollection(actualizado);
        assertSame(actualizado, entidad.getCaracteristicaCollection());
        entidad.setCaracteristicaCollection(null);
        assertNull(entidad.getCaracteristicaCollection());
    }

    @Test
    void igualdadDependeDelIdentificador() {
        UUID id = UUID.randomUUID();
        TipoCaracteristica entidad = new TipoCaracteristica(id);
        TipoCaracteristica otra = new TipoCaracteristica(id);
        TipoCaracteristica tercera = new TipoCaracteristica(id);
        assertEquals(entidad, entidad);
        assertEquals(entidad, otra);
        assertEquals(otra, entidad);
        assertEquals(otra, tercera);
        assertEquals(entidad, tercera);
        assertEquals(entidad.hashCode(), otra.hashCode());
        assertNotEquals(entidad, new TipoCaracteristica(UUID.randomUUID()));
        assertNotEquals(entidad, null);
        assertNotEquals(entidad, new Object());
    }

    @Test
    void identificadorNullSeComparaSinExcepciones() {
        TipoCaracteristica sinId = new TipoCaracteristica();
        TipoCaracteristica conId = new TipoCaracteristica(UUID.randomUUID());
        // Comportamiento actual: dos entidades sin identificador se consideran iguales.
        assertEquals(sinId, new TipoCaracteristica());
        assertNotEquals(sinId, conId);
        assertNotEquals(conId, sinId);
        assertEquals(0, sinId.hashCode());
    }

    @Test
    void hashCodeCorrespondeAlIdentificador() {
        UUID id = UUID.randomUUID();
        TipoCaracteristica entidad = new TipoCaracteristica(id);
        assertEquals(id.hashCode(), entidad.hashCode());
        entidad.setIdTipoCaracteristica(null);
        assertEquals(0, entidad.hashCode());
    }

    @Test
    void toStringIncluyeClaseEIdentificador() {
        UUID id = UUID.randomUUID();
        assertEquals("sv.edu.ues.occ.ingenieria.ppi115_2026.cafefe.entity.TipoCaracteristica[ idTipoCaracteristica=" + id + " ]",
                new TipoCaracteristica(id).toString());
        assertEquals("sv.edu.ues.occ.ingenieria.ppi115_2026.cafefe.entity.TipoCaracteristica[ idTipoCaracteristica=null ]",
                new TipoCaracteristica().toString());
    }
}
