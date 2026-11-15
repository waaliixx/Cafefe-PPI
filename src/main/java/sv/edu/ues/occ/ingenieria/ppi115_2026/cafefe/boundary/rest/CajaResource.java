package sv.edu.ues.occ.ingenieria.ppi115_2026.cafefe.boundary.rest;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import sv.edu.ues.occ.ingenieria.ppi115_2026.cafefe.control.CajaDAO;
import sv.edu.ues.occ.ingenieria.ppi115_2026.cafefe.entity.Caja;

/** HU4: cajas activas, para que el cajero elija en cuál cobra. Solo lectura. */
@Path("caja")
@RequestScoped
@Produces(MediaType.APPLICATION_JSON)
public class CajaResource {

    @Inject
    private CajaDAO cajaDAO;

    @GET
    public Response activas() {
        List<Map<String, Object>> cajas = cajaDAO.findByActivo(Boolean.TRUE).stream()
                .sorted(Comparator.comparing(Caja::getNombre, Comparator.nullsLast(String.CASE_INSENSITIVE_ORDER)))
                .map(c -> {
                    Map<String, Object> dto = new LinkedHashMap<>();
                    dto.put("idCaja", c.getIdCaja().toString());
                    dto.put("nombre", c.getNombre());
                    dto.put("observaciones", c.getObservaciones());
                    return dto;
                }).toList();
        return Response.ok(cajas).build();
    }
}