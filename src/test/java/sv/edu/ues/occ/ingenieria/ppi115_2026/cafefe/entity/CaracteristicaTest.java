package sv.edu.ues.occ.ingenieria.ppi115_2026.cafefe.entity;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/** Pruebas unitarias de Caracteristica, sin persistencia ni servidor. */
class CaracteristicaTest {

    @Test
    void constructorVacioInicializaAtributosEnNull() {
        Caracteristica entidad = new Caracteristica();
        assertNull(entidad.getIdCaracteristica());
        assertNull(entidad.getNombre());
        assertNull(entidad.getActivo());
        assertNull(entidad.getObservaciones());
        assertNull(entidad.getIdTipoCaracteristica());
        assertNull(entidad.getProductoCaracteristicaCollection());
    }

    @Test
    void constructorConIdConservaIdentificador() {
        UUID id = UUID.fromString("00000000-0000-0000-0000-000000000001");
        Caracteristica entidad = new Caracteristica(id);
        assertEquals(id, entidad.getIdCaracteristica());
    }

    @Test
    void asignarIdCaracteristicaPermiteActualizarYLimpiar() {
        Caracteristica entidad = new Caracteristica();
        UUID inicial = UUID.fromString("00000000-0000-0000-0000-000000000001");
        UUID actualizado = UUID.fromString("00000000-0000-0000-0000-000000000002");
        entidad.setIdCaracteristica(inicial);
        assertEquals(inicial, entidad.getIdCaracteristica());
        entidad.setIdCaracteristica(actualizado);
        assertEquals(actualizado, entidad.getIdCaracteristica());
        entidad.setIdCaracteristica(null);
        assertNull(entidad.getIdCaracteristica());
    }

    @Test
    void asignarNombrePermiteActualizarYLimpiar() {
        Caracteristica entidad = new Caracteristica();
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
        Caracteristica entidad = new Caracteristica();
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
        Caracteristica entidad = new Caracteristica();
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
    void asignarIdTipoCaracteristicaPermiteActualizarYLimpiar() {
        Caracteristica entidad = new Caracteristica();
        TipoCaracteristica inicial = new TipoCaracteristica(UUID.fromString("00000000-0000-0000-0000-000000000001"));
        TipoCaracteristica actualizado = new TipoCaracteristica(UUID.fromString("00000000-0000-0000-0000-000000000002"));
        entidad.setIdTipoCaracteristica(inicial);
        assertSame(inicial, entidad.getIdTipoCaracteristica());
        entidad.setIdTipoCaracteristica(actualizado);
        assertSame(actualizado, entidad.getIdTipoCaracteristica());
        entidad.setIdTipoCaracteristica(null);
        assertNull(entidad.getIdTipoCaracteristica());
    }

    @Test
    void asignarProductoCaracteristicaCollectionPermiteActualizarYLimpiar() {
        Caracteristica entidad = new Caracteristica();
        Collection<ProductoCaracteristica> inicial = new ArrayList<>(List.of(new ProductoCaracteristica(UUID.randomUUID())));
        Collection<ProductoCaracteristica> actualizado = new ArrayList<>();
        entidad.setProductoCaracteristicaCollection(inicial);
        assertSame(inicial, entidad.getProductoCaracteristicaCollection());
        entidad.setProductoCaracteristicaCollection(actualizado);
        assertSame(actualizado, entidad.getProductoCaracteristicaCollection());
        entidad.setProductoCaracteristicaCollection(null);
        assertNull(entidad.getProductoCaracteristicaCollection());
    }

    @Test
    void igualdadDependeDelIdentificador() {
        UUID id = UUID.randomUUID();
        Caracteristica entidad = new Caracteristica(id);
        Caracteristica otra = new Caracteristica(id);
        Caracteristica tercera = new Caracteristica(id);
        assertEquals(entidad, entidad);
        assertEquals(entidad, otra);
        assertEquals(otra, entidad);
        assertEquals(otra, tercera);
        assertEquals(entidad, tercera);
        assertEquals(entidad.hashCode(), otra.hashCode());
        assertNotEquals(entidad, new Caracteristica(UUID.randomUUID()));
        assertNotEquals(entidad, null);
        assertNotEquals(entidad, new Object());
    }

    @Test
    void identificadorNullSeComparaSinExcepciones() {
        Caracteristica sinId = new Caracteristica();
        Caracteristica conId = new Caracteristica(UUID.randomUUID());
        // Comportamiento actual: dos entidades sin identificador se consideran iguales.
        assertEquals(sinId, new Caracteristica());
        assertNotEquals(sinId, conId);
        assertNotEquals(conId, sinId);
        assertEquals(0, sinId.hashCode());
    }

    @Test
    void hashCodeCorrespondeAlIdentificador() {
        UUID id = UUID.randomUUID();
        Caracteristica entidad = new Caracteristica(id);
        assertEquals(id.hashCode(), entidad.hashCode());
        entidad.setIdCaracteristica(null);
        assertEquals(0, entidad.hashCode());
    }

    @Test
    void toStringIncluyeClaseEIdentificador() {
        UUID id = UUID.randomUUID();
        assertEquals("sv.edu.ues.occ.ingenieria.ppi115_2026.cafefe.entity.Caracteristica[ idCaracteristica=" + id + " ]",
                new Caracteristica(id).toString());
        assertEquals("sv.edu.ues.occ.ingenieria.ppi115_2026.cafefe.entity.Caracteristica[ idCaracteristica=null ]",
                new Caracteristica().toString());
    }
}
