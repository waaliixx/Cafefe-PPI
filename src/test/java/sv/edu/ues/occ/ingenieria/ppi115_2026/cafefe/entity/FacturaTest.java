package sv.edu.ues.occ.ingenieria.ppi115_2026.cafefe.entity;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/** Pruebas unitarias de Factura, sin persistencia ni servidor. */
class FacturaTest {

    @Test
    void constructorVacioInicializaAtributosEnNull() {
        Factura entidad = new Factura();
        assertNull(entidad.getIdFactura());
        assertNull(entidad.getCliente());
        assertNull(entidad.getFechaFacturacion());
        assertNull(entidad.getEstado());
        assertNull(entidad.getObservaciones());
        assertNull(entidad.getPagoCollection());
        assertNull(entidad.getFacturaOrdenProductoCollection());
        assertNull(entidad.getIdCaja());
        assertNull(entidad.getIdEmpleadoRol());
    }

    @Test
    void constructorConIdConservaIdentificador() {
        UUID id = UUID.fromString("00000000-0000-0000-0000-000000000001");
        Factura entidad = new Factura(id);
        assertEquals(id, entidad.getIdFactura());
    }

    @Test
    void asignarIdFacturaPermiteActualizarYLimpiar() {
        Factura entidad = new Factura();
        UUID inicial = UUID.fromString("00000000-0000-0000-0000-000000000001");
        UUID actualizado = UUID.fromString("00000000-0000-0000-0000-000000000002");
        entidad.setIdFactura(inicial);
        assertEquals(inicial, entidad.getIdFactura());
        entidad.setIdFactura(actualizado);
        assertEquals(actualizado, entidad.getIdFactura());
        entidad.setIdFactura(null);
        assertNull(entidad.getIdFactura());
    }

    @Test
    void asignarClientePermiteActualizarYLimpiar() {
        Factura entidad = new Factura();
        String inicial = "Valor inicial";
        String actualizado = "Valor actualizado";
        entidad.setCliente(inicial);
        assertEquals(inicial, entidad.getCliente());
        entidad.setCliente(actualizado);
        assertEquals(actualizado, entidad.getCliente());
        entidad.setCliente(null);
        assertNull(entidad.getCliente());
    }

    @Test
    void asignarFechaFacturacionPermiteActualizarYLimpiar() {
        Factura entidad = new Factura();
        Date inicial = new Date(1700000000000L);
        Date actualizado = new Date(1700086400000L);
        entidad.setFechaFacturacion(inicial);
        assertEquals(inicial, entidad.getFechaFacturacion());
        entidad.setFechaFacturacion(actualizado);
        assertEquals(actualizado, entidad.getFechaFacturacion());
        entidad.setFechaFacturacion(null);
        assertNull(entidad.getFechaFacturacion());
    }

    @Test
    void asignarEstadoPermiteActualizarYLimpiar() {
        Factura entidad = new Factura();
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
        Factura entidad = new Factura();
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
    void asignarPagoCollectionPermiteActualizarYLimpiar() {
        Factura entidad = new Factura();
        Collection<Pago> inicial = new ArrayList<>(List.of(new Pago(UUID.randomUUID())));
        Collection<Pago> actualizado = new ArrayList<>();
        entidad.setPagoCollection(inicial);
        assertSame(inicial, entidad.getPagoCollection());
        entidad.setPagoCollection(actualizado);
        assertSame(actualizado, entidad.getPagoCollection());
        entidad.setPagoCollection(null);
        assertNull(entidad.getPagoCollection());
    }

    @Test
    void asignarFacturaOrdenProductoCollectionPermiteActualizarYLimpiar() {
        Factura entidad = new Factura();
        Collection<FacturaOrdenProducto> inicial = new ArrayList<>(List.of(new FacturaOrdenProducto(UUID.randomUUID())));
        Collection<FacturaOrdenProducto> actualizado = new ArrayList<>();
        entidad.setFacturaOrdenProductoCollection(inicial);
        assertSame(inicial, entidad.getFacturaOrdenProductoCollection());
        entidad.setFacturaOrdenProductoCollection(actualizado);
        assertSame(actualizado, entidad.getFacturaOrdenProductoCollection());
        entidad.setFacturaOrdenProductoCollection(null);
        assertNull(entidad.getFacturaOrdenProductoCollection());
    }

    @Test
    void asignarIdCajaPermiteActualizarYLimpiar() {
        Factura entidad = new Factura();
        Caja inicial = new Caja(UUID.fromString("00000000-0000-0000-0000-000000000001"));
        Caja actualizado = new Caja(UUID.fromString("00000000-0000-0000-0000-000000000002"));
        entidad.setIdCaja(inicial);
        assertSame(inicial, entidad.getIdCaja());
        entidad.setIdCaja(actualizado);
        assertSame(actualizado, entidad.getIdCaja());
        entidad.setIdCaja(null);
        assertNull(entidad.getIdCaja());
    }

    @Test
    void asignarIdEmpleadoRolPermiteActualizarYLimpiar() {
        Factura entidad = new Factura();
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
        Factura entidad = new Factura(id);
        Factura otra = new Factura(id);
        Factura tercera = new Factura(id);
        assertEquals(entidad, entidad);
        assertEquals(entidad, otra);
        assertEquals(otra, entidad);
        assertEquals(otra, tercera);
        assertEquals(entidad, tercera);
        assertEquals(entidad.hashCode(), otra.hashCode());
        assertNotEquals(entidad, new Factura(UUID.randomUUID()));
        assertNotEquals(entidad, null);
        assertNotEquals(entidad, new Object());
    }

    @Test
    void identificadorNullSeComparaSinExcepciones() {
        Factura sinId = new Factura();
        Factura conId = new Factura(UUID.randomUUID());
        // Comportamiento actual: dos entidades sin identificador se consideran iguales.
        assertEquals(sinId, new Factura());
        assertNotEquals(sinId, conId);
        assertNotEquals(conId, sinId);
        assertEquals(0, sinId.hashCode());
    }

    @Test
    void hashCodeCorrespondeAlIdentificador() {
        UUID id = UUID.randomUUID();
        Factura entidad = new Factura(id);
        assertEquals(id.hashCode(), entidad.hashCode());
        entidad.setIdFactura(null);
        assertEquals(0, entidad.hashCode());
    }

    @Test
    void toStringIncluyeClaseEIdentificador() {
        UUID id = UUID.randomUUID();
        assertEquals("sv.edu.ues.occ.ingenieria.ppi115_2026.cafefe.entity.Factura[ idFactura=" + id + " ]",
                new Factura(id).toString());
        assertEquals("sv.edu.ues.occ.ingenieria.ppi115_2026.cafefe.entity.Factura[ idFactura=null ]",
                new Factura().toString());
    }
}
