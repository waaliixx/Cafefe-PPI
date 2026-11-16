package sv.edu.ues.occ.ingenieria.ppi115_2026.cafefe;

import jakarta.ws.rs.ApplicationPath;
import jakarta.ws.rs.core.Application;
import java.util.Set;

import sv.edu.ues.occ.ingenieria.ppi115_2026.cafefe.boundary.rest.CajaResource;
import sv.edu.ues.occ.ingenieria.ppi115_2026.cafefe.boundary.rest.CorsFilter;
import sv.edu.ues.occ.ingenieria.ppi115_2026.cafefe.boundary.rest.ErrorMapper;
import sv.edu.ues.occ.ingenieria.ppi115_2026.cafefe.boundary.rest.FacturaResource;
import sv.edu.ues.occ.ingenieria.ppi115_2026.cafefe.boundary.rest.LoginResource;
import sv.edu.ues.occ.ingenieria.ppi115_2026.cafefe.boundary.rest.OrdenResource;
import sv.edu.ues.occ.ingenieria.ppi115_2026.cafefe.boundary.rest.TipoProductoResource;

@ApplicationPath("resources")
public class JakartaRestConfiguration extends Application {

    @Override
    public Set<Class<?>> getClasses() {
        return Set.of(
                TipoProductoResource.class,
                LoginResource.class,
                OrdenResource.class,
                CajaResource.class,
                FacturaResource.class,
                CorsFilter.class,
                ErrorMapper.class
        );
    }
}
