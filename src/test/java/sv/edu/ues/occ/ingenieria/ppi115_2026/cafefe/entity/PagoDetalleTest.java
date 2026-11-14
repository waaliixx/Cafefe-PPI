package sv.edu.ues.occ.ingenieria.ppi115_2026.cafefe.entity;

import java.math.BigDecimal;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/** Pruebas unitarias de PagoDetalle, sin persistencia ni servidor. */
class PagoDetalleTest {

    @Test
    void constructorVacioInicializaAtributosEnNull() {
        PagoDetalle entidad = new PagoDetalle();
        assertNull(entidad.getIdPagoDetalle());
        assertNull(entidad.getMonto());
        assertNull(entidad.getTipoPago());
        assertNull(entidad.getObservaciones());
        assertNull(entidad.getReferenciaExterna());
        assertNull(entidad.getIdPago());
    }

    @Test
    void constructorConIdConservaIdentificador() {
        UUID id = UUID.fromString("00000000-0000-0000-0000-000000000001");
        PagoDetalle entidad = new PagoDetalle(id);
        assertEquals(id, entidad.getIdPagoDetalle());
    }

    @Test
    void asignarIdPagoDetallePermiteActualizarYLimpiar() {
        PagoDetalle entidad = new PagoDetalle();
        UUID inicial = UUID.fromString("00000000-0000-0000-0000-000000000001");
        UUID actualizado = UUID.fromString("00000000-0000-0000-0000-000000000002");
        entidad.setIdPagoDetalle(inicial);
        assertEquals(inicial, entidad.getIdPagoDetalle());
        entidad.setIdPagoDetalle(actualizado);
        assertEquals(actualizado, entidad.getIdPagoDetalle());
        entidad.setIdPagoDetalle(null);
        assertNull(entidad.getIdPagoDetalle());
    }

    @Test
    void asignarMontoPermiteActualizarYLimpiar() {
        PagoDetalle entidad = new PagoDetalle();
        BigDecimal inicial = new BigDecimal("2.50");
        BigDecimal actualizado = new BigDecimal("3.75");
        entidad.setMonto(inicial);
        assertEquals(inicial, entidad.getMonto());
        entidad.setMonto(actualizado);
        assertEquals(actualizado, entidad.getMonto());
        entidad.setMonto(null);
        assertNull(entidad.getMonto());
    }

    @Test
    void asignarTipoPagoPermiteActualizarYLimpiar() {
        PagoDetalle entidad = new PagoDetalle();
        String inicial = "Valor inicial";
        String actualizado = "Valor actualizado";
        entidad.setTipoPago(inicial);
        assertEquals(inicial, entidad.getTipoPago());
        entidad.setTipoPago(actualizado);
        assertEquals(actualizado, entidad.getTipoPago());
        entidad.setTipoPago(null);
        assertNull(entidad.getTipoPago());
    }

    @Test
    void asignarObservacionesPermiteActualizarYLimpiar() {
        PagoDetalle entidad = new PagoDetalle();
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
    void asignarReferenciaExternaPermiteActualizarYLimpiar() {
        PagoDetalle entidad = new PagoDetalle();
        String inicial = "Valor inicial";
        String actualizado = "Valor actualizado";
        entidad.setReferenciaExterna(inicial);
        assertEquals(inicial, entidad.getReferenciaExterna());
        entidad.setReferenciaExterna(actualizado);
        assertEquals(actualizado, entidad.getReferenciaExterna());
        entidad.setReferenciaExterna(null);
        assertNull(entidad.getReferenciaExterna());
    }

    @Test
    void asignarIdPagoPermiteActualizarYLimpiar() {
        PagoDetalle entidad = new PagoDetalle();
        Pago inicial = new Pago(UUID.fromString("00000000-0000-0000-0000-000000000001"));
        Pago actualizado = new Pago(UUID.fromString("00000000-0000-0000-0000-000000000002"));
        entidad.setIdPago(inicial);
        assertSame(inicial, entidad.getIdPago());
        entidad.setIdPago(actualizado);
        assertSame(actualizado, entidad.getIdPago());
        entidad.setIdPago(null);
        assertNull(entidad.getIdPago());
    }

    @Test
    void igualdadDependeDelIdentificador() {
        UUID id = UUID.randomUUID();
        PagoDetalle entidad = new PagoDetalle(id);
        PagoDetalle otra = new PagoDetalle(id);
        PagoDetalle tercera = new PagoDetalle(id);
        assertEquals(entidad, entidad);
        assertEquals(entidad, otra);
        assertEquals(otra, entidad);
        assertEquals(otra, tercera);
        assertEquals(entidad, tercera);
        assertEquals(entidad.hashCode(), otra.hashCode());
        assertNotEquals(entidad, new PagoDetalle(UUID.randomUUID()));
        assertNotEquals(entidad, null);
        assertNotEquals(entidad, new Object());
    }

    @Test
    void identificadorNullSeComparaSinExcepciones() {
        PagoDetalle sinId = new PagoDetalle();
        PagoDetalle conId = new PagoDetalle(UUID.randomUUID());
        // Comportamiento actual: dos entidades sin identificador se consideran iguales.
        assertEquals(sinId, new PagoDetalle());
        assertNotEquals(sinId, conId);
        assertNotEquals(conId, sinId);
        assertEquals(0, sinId.hashCode());
    }

    @Test
    void hashCodeCorrespondeAlIdentificador() {
        UUID id = UUID.randomUUID();
        PagoDetalle entidad = new PagoDetalle(id);
        assertEquals(id.hashCode(), entidad.hashCode());
        entidad.setIdPagoDetalle(null);
        assertEquals(0, entidad.hashCode());
    }

    @Test
    void toStringIncluyeClaseEIdentificador() {
        UUID id = UUID.randomUUID();
        assertEquals("sv.edu.ues.occ.ingenieria.ppi115_2026.cafefe.entity.PagoDetalle[ idPagoDetalle=" + id + " ]",
                new PagoDetalle(id).toString());
        assertEquals("sv.edu.ues.occ.ingenieria.ppi115_2026.cafefe.entity.PagoDetalle[ idPagoDetalle=null ]",
                new PagoDetalle().toString());
    }
}
