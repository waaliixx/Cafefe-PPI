/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package sv.edu.ues.occ.ingenieria.ppi115_2026.cafefe.control;

import java.util.Set;
import java.util.UUID;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

/**
 *
 * @author walixx
 */
public class PreparacionEstadoTest {
   @Test void transicionesValidas() {
        assertTrue(PreparacionTemporal.transicionValida("PENDIENTE", "PREPARANDO"));
        assertTrue(PreparacionTemporal.transicionValida("PREPARANDO", "LISTO"));
        assertTrue(PreparacionTemporal.transicionValida("LISTO", "PREPARANDO"));
        assertFalse(PreparacionTemporal.transicionValida("PENDIENTE", "LISTO"));
    }
    @Test void mantieneEstadoEnLaInstanciaYPermiteDespachoAlCompletar() {
        PreparacionTemporal memoria = new PreparacionTemporal();
        UUID orden = UUID.randomUUID(), a = UUID.randomUUID(), b = UUID.randomUUID();
        assertEquals("PENDIENTE", memoria.estado(orden, a));
        memoria.cambiar(orden, a, "PREPARANDO");
        memoria.cambiar(orden, a, "LISTO");
        assertThrows(jakarta.ws.rs.ClientErrorException.class, () -> memoria.despachar(orden, Set.of(a, b)));
        memoria.cambiar(orden, b, "PREPARANDO");
        memoria.cambiar(orden, b, "LISTO");
        memoria.despachar(orden, Set.of(a, b));
        assertTrue(memoria.despachada(orden));
        assertNotNull(memoria.fechaDespacho(orden));
        assertThrows(jakarta.ws.rs.ClientErrorException.class, () -> memoria.cambiar(orden, a, "PREPARANDO"));
    }
    @Test void nuevaInstanciaNoConservaEstados() {
        UUID orden = UUID.randomUUID(), linea = UUID.randomUUID();
        PreparacionTemporal antes = new PreparacionTemporal();
        antes.cambiar(orden, linea, "PREPARANDO");
        assertEquals("PENDIENTE", new PreparacionTemporal().estado(orden, linea));
    } 
}
