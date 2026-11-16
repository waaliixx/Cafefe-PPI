package sv.edu.ues.occ.ingenieria.ppi115_2026.cafefe.boundary.rest;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;
import java.math.BigDecimal;
import java.net.URI;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import sv.edu.ues.occ.ingenieria.ppi115_2026.cafefe.control.OrdenService;
import sv.edu.ues.occ.ingenieria.ppi115_2026.cafefe.control.PagoService;
import sv.edu.ues.occ.ingenieria.ppi115_2026.cafefe.control.PagoService.LineaPago;
import sv.edu.ues.occ.ingenieria.ppi115_2026.cafefe.control.PagoService.ResultadoCobro;

/**
 * POST orden                → HU2: el cliente crea su orden (201 + Location)
 * GET  orden/pendiente      → HU3 y HU4: órdenes con productos sin cobrar
 * GET  orden/pendiente/{id} → una orden con sus productos pendientes
 * POST orden/{id}/cobro     → HU4: cobrar (201 + Location hacia el ticket PDF)
 */
@Path("orden")
@RequestScoped
@Produces(MediaType.APPLICATION_JSON)
public class OrdenResource {

    @Inject
    private OrdenService ordenService;
    @Inject
    private PagoService pagoService;

    /** Cuerpo de POST orden/{id}/cobro. */
    public static class SolicitudCobro {

        public String idCaja;
        public String idEmpleadoRol;
        public String cliente;
        public List<PagoSolicitado> pagos;
    }

    /** Una forma de pago: {"tipoPago": "Tarjeta", "monto": 5.00, "referenciaExterna": "AUT-1"}. */
    public static class PagoSolicitado {

        public String tipoPago;
        public BigDecimal monto;
        public String referenciaExterna;
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response crear(OrdenService.SolicitudOrden solicitud, @Context UriInfo uriInfo) {
        Map<String, Object> resultado = ordenService.crearOrden(solicitud);
        URI location = uriInfo.getAbsolutePathBuilder()
                .path("pendiente").path(String.valueOf(resultado.get("idOrden"))).build();
        return Response.created(location).entity(resultado).build();
    }

    @GET
    @Path("pendiente")
    public Response pendientes() {
        return Response.ok(ordenService.pendientes()).build();
    }

    @GET
    @Path("pendiente/{id}")
    public Response pendiente(@PathParam("id") UUID id) {
        Map<String, Object> orden = ordenService.pendiente(id);
        if (orden == null) {
            return ErrorMapper.noEncontrado(id, "La orden no existe.");
        }
        return Response.ok(orden).build();
    }

    public static class CambioEstado {
        public String estado;
    }

    @GET
    @Path("preparacion")
    public Response preparacion() {
        return Response.ok(ordenService.preparacion()).build();
    }

    @GET
    @Path("{id}/preparacion")
    public Response detallePreparacion(@PathParam("id") UUID id) {
        Map<String, Object> detalle = ordenService.detallePreparacion(id);
        return detalle == null ? ErrorMapper.noEncontrado(id, "La orden no existe.") : Response.ok(detalle).build();
    }

    @PUT
    @Path("{id}/producto/{idLinea}/preparacion")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response cambiarEstado(@PathParam("id") UUID id, @PathParam("idLinea") UUID idLinea, CambioEstado cambio) {
        if (cambio == null || cambio.estado == null)
            return ErrorMapper.invalido("estado", "Indique el estado de preparación.");
        return Response.ok(ordenService.cambiarPreparacion(id, idLinea, cambio.estado)).build();
    }

    @PUT
    @Path("{id}/despacho")
    public Response despachar(@PathParam("id") UUID id) {
        return Response.ok(ordenService.despachar(id)).build();
    }

    @POST
    @Path("{id}/cobro")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response cobrar(@PathParam("id") UUID id, SolicitudCobro solicitud, @Context UriInfo uriInfo) {
        if (solicitud == null) {
            return ErrorMapper.invalido("registro", "El cuerpo del cobro es obligatorio.");
        }
        UUID idCaja = aUuid(solicitud.idCaja);
        if (idCaja == null) {
            return ErrorMapper.invalido("idCaja", "Seleccione la caja.");
        }
        UUID idCajero = aUuid(solicitud.idEmpleadoRol);
        if (idCajero == null) {
            return ErrorMapper.invalido("idEmpleadoRol", "Falta el cajero que cobra.");
        }
        List<LineaPago> pagos = null;
        if (solicitud.pagos != null) {
            pagos = new ArrayList<>();
            for (PagoSolicitado p : solicitud.pagos) {
                pagos.add(p == null ? null : new LineaPago(p.tipoPago, p.monto, p.referenciaExterna));
            }
        }
        // Si el cajero no escribe un nombre, se usa el que dio el cliente al hacer su orden.
        String cliente = solicitud.cliente;
        if (cliente == null || cliente.isBlank()) {
            cliente = ordenService.clienteDeOrden(id);
        }
        // Las reglas de cobro (caja, cajero, montos, vuelto) las valida PagoService y lanza CobroException.
        ResultadoCobro r = pagoService.cobrar(id, idCaja, idCajero, cliente, pagos);

        String idFactura = r.factura().getIdFactura().toString();
        Map<String, Object> cuerpo = new LinkedHashMap<>();
        cuerpo.put("idFactura", idFactura);
        cuerpo.put("idOrden", id.toString());
        cuerpo.put("cliente", r.factura().getCliente());
        cuerpo.put("total", r.total());
        cuerpo.put("pagado", r.pagado());
        cuerpo.put("vuelto", r.vuelto());
        URI ticket = uriInfo.getBaseUriBuilder().path("factura").path(idFactura).path("ticket").build();
        return Response.created(ticket).entity(cuerpo).build();
    }

    private static UUID aUuid(String texto) {
        if (texto == null || texto.isBlank()) {
            return null;
        }
        try {
            return UUID.fromString(texto.trim());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}
