package sv.edu.ues.occ.ingenieria.ppi115_2026.cafefe.boundary.rest;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.DefaultValue;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.UUID;
import sv.edu.ues.occ.ingenieria.ppi115_2026.cafefe.control.CatalogoService;
import sv.edu.ues.occ.ingenieria.ppi115_2026.cafefe.entity.TipoProducto;

//HU2

@Path("tipo_producto")
@RequestScoped
@Produces(MediaType.APPLICATION_JSON)
public class TipoProductoResource {

    @Inject
    private CatalogoService catalogo;

    @GET
    public Response categorias(@QueryParam("soloActivos") @DefaultValue("false") boolean soloActivos) {
        return Response.ok(catalogo.categorias(soloActivos)).build();
    }

    @GET
    @Path("{id}/producto")
    public Response productos(@PathParam("id") UUID id,
            @QueryParam("soloActivos") @DefaultValue("false") boolean soloActivos) {
        TipoProducto tipo = catalogo.buscarCategoria(id);
        if (tipo == null) {
            return ErrorMapper.noEncontrado(id, "La categoría no existe.");
        }
        if (!Boolean.TRUE.equals(tipo.getActivo())) {
            return ErrorMapper.construir(409, "CATEGORIA_INACTIVA", "La categoría está inactiva.");
        }
        return Response.ok(catalogo.productosDeCategoria(id, soloActivos)).build();
    }
}