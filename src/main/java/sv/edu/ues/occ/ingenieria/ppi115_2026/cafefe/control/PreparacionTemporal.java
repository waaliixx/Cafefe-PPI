/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package sv.edu.ues.occ.ingenieria.ppi115_2026.cafefe.control;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.ClientErrorException;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

/**
 *
 * @author walixx
 */
/** Estados compartidos por todos los clientes del mismo servidor, sin persistencia en BD. */
@ApplicationScoped
public class PreparacionTemporal {
    private final Map<UUID, Map<UUID, String>> estados = new HashMap<>();
    private final Map<UUID, Instant> despachadas = new HashMap<>();

    public synchronized String estado(UUID orden, UUID linea) {
        return estados.getOrDefault(orden, Map.of()).getOrDefault(linea, "PENDIENTE");
    }

    public synchronized boolean despachada(UUID orden) {
        return despachadas.containsKey(orden);
    }

    public synchronized String fechaDespacho(UUID orden) {
        Instant fecha = despachadas.get(orden);
        return fecha == null ? null : fecha.toString();
    }

    public synchronized void cambiar(UUID orden, UUID linea, String nuevo) {
        if (despachada(orden)) throw new ClientErrorException("La orden ya fue despachada.", 409);
        String actual = estado(orden, linea);
        if (!transicionValida(actual, nuevo))
            throw new OrdenService.SolicitudInvalidaException("estado", "Transición de preparación no permitida.");
        estados.computeIfAbsent(orden, key -> new HashMap<>()).put(linea, nuevo);
    }

    public synchronized void despachar(UUID orden, Set<UUID> lineas) {
        if (despachada(orden)) throw new ClientErrorException("La orden ya fue despachada.", 409);
        if (lineas == null || lineas.isEmpty() || lineas.stream().anyMatch(l -> !"LISTO".equals(estado(orden, l))))
            throw new ClientErrorException("Todos los productos deben estar LISTOS antes del despacho.", 409);
        despachadas.put(orden, Instant.now());
    }

    public static boolean transicionValida(String anterior, String nuevo) {
        if (anterior == null || nuevo == null) return false;
        return switch (anterior) {
            case "PENDIENTE" -> "PREPARANDO".equals(nuevo);
            case "PREPARANDO" -> "LISTO".equals(nuevo);
            case "LISTO" -> "PREPARANDO".equals(nuevo);
            default -> false;
        };
    }
}
