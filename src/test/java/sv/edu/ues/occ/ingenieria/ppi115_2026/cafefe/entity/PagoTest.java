package sv.edu.ues.occ.ingenieria.ppi115_2026.cafefe.entity;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/** Pruebas unitarias de Pago, sin persistencia ni servidor. */
class PagoTest {

    @Test
    void constructorVacioInicializaAtributosEnNull() {
        Pago entidad = new Pago();
        assertNull(entidad.getIdPago());
        assertNull(entidad.getEstado());
        assertNull(entidad.getObservaciones());
        assertNull(entidad.getIdFactura());
        assertNull(entidad.getPagoDetalleCollection());
    }

    @Test
    void constructorConIdConservaIdentificador() {
        UUID id = UUID.fromString("00000000-0000-0000-0000-000000000001");
        Pago entidad = new Pago(id);
        assertEquals(id, entidad.getIdPago());
    }

    @Test
    void asignarIdPagoPermiteActualizarYLimpiar() {
        Pago entidad = new Pago();
        UUID inicial = UUID.fromString("00000000-0000-0000-0000-000000000001");
        UUID actualizado = UUID.fromString("00000000-0000-0000-0000-000000000002");
        entidad.setIdPago(inicial);
        assertEquals(inicial, entidad.getIdPago());
        entidad.setIdPago(actualizado);
        assertEquals(actualizado, entidad.getIdPago());
        entidad.setIdPago(null);
        assertNull(entidad.getIdPago());
    }

    @Test
    void asignarEstadoPermiteActualizarYLimpiar() {
        Pago entidad = new Pago();
        String inicial = "Valor inicial";
        String actualizado = "Valor actualizado";
        entidad.setEstado(inicial);
        assertEquals(inicial, entidad.getEstado());
        entidad.setEstado(actualizado);
        assertEquals(actualizado, entidad.getEstado());
        entidad.setEstado(null);
        assertNull(entidad.getEstado());
    }

    @Test
    void asignarObservacionesPermiteActualizarYLimpiar() {
        Pago entidad = new Pago();
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
    void asignarIdFacturaPermiteActualizarYLimpiar() {
        Pago entidad = new Pago();
        Factura inicial = new Factura(UUID.fromString("00000000-0000-0000-0000-000000000001"));
        Factura actualizado = new Factura(UUID.fromString("00000000-0000-0000-0000-000000000002"));
        entidad.setIdFactura(inicial);
        assertSame(inicial, entidad.getIdFactura());
        entidad.setIdFactura(actualizado);
        assertSame(actualizado, entidad.getIdFactura());
        entidad.setIdFactura(null);
        assertNull(entidad.getIdFactura());
    }

    @Test
    void asignarPagoDetalleCollectionPermiteActualizarYLimpiar() {
        Pago entidad = new Pago();
        Collection<PagoDetalle> inicial = new ArrayList<>(List.of(new PagoDetalle(UUID.randomUUID())));
        Collection<PagoDetalle> actualizado = new ArrayList<>();
        entidad.setPagoDetalleCollection(inicial);
        assertSame(inicial, entidad.getPagoDetalleCollection());
        entidad.setPagoDetalleCollection(actualizado);
        assertSame(actualizado, entidad.getPagoDetalleCollection());
        entidad.setPagoDetalleCollection(null);
        assertNull(entidad.getPagoDetalleCollection());
    }

    @Test
    void igualdadDependeDelIdentificador() {
        UUID id = UUID.randomUUID();
        Pago entidad = new Pago(id);
        Pago otra = new Pago(id);
        Pago tercera = new Pago(id);
        assertEquals(entidad, entidad);
        assertEquals(entidad, otra);
        assertEquals(otra, entidad);
        assertEquals(otra, tercera);
        assertEquals(entidad, tercera);
        assertEquals(entidad.hashCode(), otra.hashCode());
        assertNotEquals(entidad, new Pago(UUID.randomUUID()));
        assertNotEquals(entidad, null);
        assertNotEquals(entidad, new Object());
    }

    @Test
    void identificadorNullSeComparaSinExcepciones() {
        Pago sinId = new Pago();
        Pago conId = new Pago(UUID.randomUUID());
        // Comportamiento actual: dos entidades sin identificador se consideran iguales.
        assertEquals(sinId, new Pago());
        assertNotEquals(sinId, conId);
        assertNotEquals(conId, sinId);
        assertEquals(0, sinId.hashCode());
    }

    @Test
    void hashCodeCorrespondeAlIdentificador() {
        UUID id = UUID.randomUUID();
        Pago entidad = new Pago(id);
        assertEquals(id.hashCode(), entidad.hashCode());
        entidad.setIdPago(null);
        assertEquals(0, entidad.hashCode());
    }

    @Test
    void toStringIncluyeClaseEIdentificador() {
        UUID id = UUID.randomUUID();
        assertEquals("sv.edu.ues.occ.ingenieria.ppi115_2026.cafefe.entity.Pago[ idPago=" + id + " ]",
                new Pago(id).toString());
        assertEquals("sv.edu.ues.occ.ingenieria.ppi115_2026.cafefe.entity.Pago[ idPago=null ]",
                new Pago().toString());
    }
}
