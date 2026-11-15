package sv.edu.ues.occ.ingenieria.ppi115_2026.cafefe.boundary.rest;

import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.container.ContainerResponseContext;
import jakarta.ws.rs.container.ContainerResponseFilter;
import jakarta.ws.rs.container.PreMatching;
import jakarta.ws.rs.core.MultivaluedMap;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.Provider;

/** Permite que el frontend (otro origen) consuma la API. Antes de producción conviene limitar el origen. */
@Provider
@PreMatching
public class CorsFilter implements ContainerRequestFilter, ContainerResponseFilter {

    static final String ORIGEN_PERMITIDO = "*";
    static final String METODOS = "GET, POST, PUT, DELETE, OPTIONS, HEAD";
    static final String CABECERAS_BASE = "origin, accept, content-type";
    static final String CABECERAS_EXPUESTAS
            = "Location, Total-registros, Wrong-Param, App-Error, Entity-Not-Found, Content-Disposition";
    static final int MAX_AGE = 2 * 60 * 60;

    /** La petición previa (preflight) se responde aquí, sin llegar a ningún recurso. */
    @Override
    public void filter(ContainerRequestContext peticion) {
        if ("OPTIONS".equalsIgnoreCase(peticion.getMethod())
                && peticion.getHeaderString("Access-Control-Request-Method") != null) {
            peticion.abortWith(Response.ok().build());
        }
    }

    @Override
    public void filter(ContainerRequestContext peticion, ContainerResponseContext respuesta) {
        MultivaluedMap<String, Object> cabeceras = respuesta.getHeaders();
        String pedidas = peticion.getHeaderString("Access-Control-Request-Headers");
        cabeceras.putSingle("Access-Control-Allow-Origin", ORIGEN_PERMITIDO);
        cabeceras.putSingle("Access-Control-Allow-Methods", METODOS);
        cabeceras.putSingle("Access-Control-Allow-Headers",
                pedidas == null || pedidas.isBlank() ? CABECERAS_BASE : pedidas);
        cabeceras.putSingle("Access-Control-Expose-Headers", CABECERAS_EXPUESTAS);
        cabeceras.putSingle("Access-Control-Max-Age", MAX_AGE);
    }
}
