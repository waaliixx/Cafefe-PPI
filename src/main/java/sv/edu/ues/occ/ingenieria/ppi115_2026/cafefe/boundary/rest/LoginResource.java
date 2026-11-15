package sv.edu.ues.occ.ingenieria.ppi115_2026.cafefe.boundary.rest;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import sv.edu.ues.occ.ingenieria.ppi115_2026.cafefe.control.EmpleadoDAO;
import sv.edu.ues.occ.ingenieria.ppi115_2026.cafefe.control.EmpleadoRolDAO;
import sv.edu.ues.occ.ingenieria.ppi115_2026.cafefe.control.ReglasOrden;
import sv.edu.ues.occ.ingenieria.ppi115_2026.cafefe.entity.Empleado;
import sv.edu.ues.occ.ingenieria.ppi115_2026.cafefe.entity.EmpleadoRol;

/** HU1: usuario = nombre del empleado, contraseña = su identificador único. */
@Path("login")
@RequestScoped
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class LoginResource {

    private static final String MENSAJE_INVALIDO = "Credenciales inválidas.";

    @Inject
    private EmpleadoDAO empleadoDAO;
    @Inject
    private EmpleadoRolDAO empleadoRolDAO;

    public static class LoginRequest {

        public String nombre;
        public String uid;
    }

    @POST
    public Response autenticar(LoginRequest solicitud) {
        if (solicitud == null || solicitud.nombre == null || solicitud.nombre.isBlank()
                || solicitud.uid == null || solicitud.uid.isBlank()) {
            return invalido();
        }
        UUID idEmpleado;
        try {
            idEmpleado = UUID.fromString(solicitud.uid.trim());
        } catch (IllegalArgumentException e) {
            return invalido();
        }
        Empleado empleado = empleadoDAO.buscarPorId(idEmpleado);
        if (empleado == null || !Boolean.TRUE.equals(empleado.getActivo())
                || !coincideNombre(solicitud.nombre, empleado)) {
            return invalido();
        }
        // Solo roles activos; el mesero virtual no puede iniciar sesión.
        List<EmpleadoRol> roles = empleadoRolDAO.findByEmpleado(idEmpleado).stream()
                .filter(er -> Boolean.TRUE.equals(er.getActivo())
                && er.getIdRol() != null && Boolean.TRUE.equals(er.getIdRol().getActivo())
                && !ReglasOrden.esRolEnLinea(er.getIdRol()))
                .toList();
        if (roles.isEmpty()) {
            return invalido();
        }
        Map<String, Object> dtoEmpleado = new LinkedHashMap<>();
        dtoEmpleado.put("idEmpleado", empleado.getIdEmpleado().toString());
        dtoEmpleado.put("nombre", empleado.getNombre());
        dtoEmpleado.put("apellido", empleado.getApellido());
        Map<String, Object> respuesta = new LinkedHashMap<>();
        respuesta.put("empleado", dtoEmpleado);
        respuesta.put("roles", roles.stream().map(LoginResource::dtoRol).toList());
        return Response.ok(respuesta).build();
    }

    private static Map<String, Object> dtoRol(EmpleadoRol er) {
        Map<String, Object> dto = new LinkedHashMap<>();
        dto.put("idEmpleadoRol", er.getIdEmpleadoRol().toString());
        dto.put("idRol", er.getIdRol().getIdRol().toString());
        dto.put("rol", er.getIdRol().getNombre());
        return dto;
    }

    private static Response invalido() {
        return ErrorMapper.construir(401, "CREDENCIALES_INVALIDAS", MENSAJE_INVALIDO);
    }

    /** Acepta solo el nombre ("Laura") o nombre y apellido ("Laura Gómez"), sin importar tildes ni mayúsculas. */
    static boolean coincideNombre(String entrada, Empleado empleado) {
        String buscado = ReglasOrden.normalizar(entrada);
        String soloNombre = ReglasOrden.normalizar(empleado.getNombre());
        String completo = ReglasOrden.normalizar(empleado.getNombre() + " "
                + (empleado.getApellido() == null ? "" : empleado.getApellido()));
        return buscado.equals(soloNombre) || buscado.equals(completo);
    }
}