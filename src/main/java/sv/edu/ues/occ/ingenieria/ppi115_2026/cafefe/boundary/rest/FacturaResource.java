package sv.edu.ues.occ.ingenieria.ppi115_2026.cafefe.boundary.rest;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.UUID;
import sv.edu.ues.occ.ingenieria.ppi115_2026.cafefe.control.TicketService;

//H4

@Path("factura")
@RequestScoped
public class FacturaResource {

    public static final String PDF = "application/pdf";

    @Inject
    private TicketService ticketService;

    @GET
    @Path("{id}/ticket")
    @Produces({PDF, MediaType.APPLICATION_JSON})
    public Response ticket(@PathParam("id") UUID id) {
        TicketService.Ticket ticket = ticketService.ticket(id);
        if (ticket == null) {
            return ErrorMapper.noEncontrado(id, "La factura no existe.");
        }
        return Response.ok(TicketPdf.generar(ticket), PDF)
                .header("Content-Disposition", "inline; filename=\"ticket-" + id + ".pdf\"")
                .build();
    }
}