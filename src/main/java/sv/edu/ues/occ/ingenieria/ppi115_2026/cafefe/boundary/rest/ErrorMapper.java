package sv.edu.ues.occ.ingenieria.ppi115_2026.cafefe.boundary.rest;

import jakarta.json.bind.JsonbException;
import jakarta.json.stream.JsonParsingException;
import jakarta.validation.ConstraintViolationException;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import java.text.Normalizer;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import sv.edu.ues.occ.ingenieria.ppi115_2026.cafefe.control.CobroException;
import sv.edu.ues.occ.ingenieria.ppi115_2026.cafefe.control.OrdenService.ConfiguracionIncompletaException;
import sv.edu.ues.occ.ingenieria.ppi115_2026.cafefe.control.OrdenService.SolicitudInvalidaException;

/** Toda respuesta de error sale como JSON {"mensaje": ..., "codigo": ...}; el frontend muestra "mensaje". */
@Provider
public class ErrorMapper implements ExceptionMapper<Throwable> {

    private static final Logger LOG = Logger.getLogger(ErrorMapper.class.getName());
    static final int MAX_CABECERA = 200;
    static final int PROFUNDIDAD_MAXIMA = 10;

    @Override
    public Response toResponse(Throwable ex) {
        CobroException cobro = buscar(ex, CobroException.class);
        if (cobro != null) {
            return construir(estadoDe(cobro.getCodigo()), cobro.getCodigo().name(), cobro.getMessage());
        }
        SolicitudInvalidaException solicitud = buscar(ex, SolicitudInvalidaException.class);
        if (solicitud != null) {
            return invalido(solicitud.getWrongParam(), solicitud.getMessage());
        }
        ConfiguracionIncompletaException config = buscar(ex, ConfiguracionIncompletaException.class);
        if (config != null) {
            return construir(503, "CONFIGURACION_INCOMPLETA", config.getMessage());
        }
        if (buscar(ex, JsonbException.class) != null || buscar(ex, JsonParsingException.class) != null) {
            return construir(400, "JSON_INVALIDO", "El cuerpo de la petición no es un JSON válido.");
        }
        WebApplicationException web = buscar(ex, WebApplicationException.class);
        if (web != null) {
            int estado = web.getResponse().getStatus();
            return construir(estado, "HTTP_" + estado, mensajeHttp(estado, web.getMessage()));
        }
        ConstraintViolationException cve = buscar(ex, ConstraintViolationException.class);
        if (cve != null) {
            String resumen = cve.getConstraintViolations().stream()
                    .map(v -> v.getPropertyPath() + ": " + v.getMessage())
                    .sorted().collect(Collectors.joining("; "));
            return construir(422, "DATOS_INVALIDOS", resumen);
        }
        LOG.log(Level.SEVERE, "Error no controlado en la API", ex);
        return construir(500, "ERROR_INTERNO", "Ocurrió un error en el servidor.");
    }

    private static int estadoDe(CobroException.Codigo codigo) {
        return switch (codigo) {
            case DATOS_INVALIDOS -> 400;
            case NO_ENCONTRADO -> 404;
            case ORDEN_YA_PAGADA -> 409;
            case NO_AUTORIZADO -> 403;
            case SALDO_INSUFICIENTE, EXCESO_NO_EFECTIVO -> 422;
        };
    }

    private static String mensajeHttp(int estado, String original) {
        return switch (estado) {
            case 404 -> "Recurso no encontrado.";
            case 405 -> "Método no permitido.";
            case 406 -> "Formato de respuesta no soportado.";
            case 415 -> "Tipo de contenido no soportado.";
            default -> original;
        };
    }

    // ---------------------------------------------------------------- utilidades para los recursos

    static Response construir(int estado, String mensaje) {
        return construir(estado, null, mensaje);
    }

    static Response construir(int estado, String codigo, String mensaje) {
        String texto = mensaje == null || mensaje.isBlank() ? "Error en la petición." : mensaje;
        Map<String, Object> cuerpo = new LinkedHashMap<>();
        cuerpo.put("mensaje", texto);
        if (codigo != null) {
            cuerpo.put("codigo", codigo);
        }
        return Response.status(estado)
                .type(MediaType.APPLICATION_JSON)
                .header("App-Error", limpiarCabecera(texto))
                .entity(cuerpo)
                .build();
    }

    /** 422 con la cabecera Wrong-Param que indica qué dato falló. */
    static Response invalido(String wrongParam, String mensaje) {
        Response base = construir(422, "DATOS_INVALIDOS", mensaje);
        if (wrongParam == null || wrongParam.isBlank()) {
            return base;
        }
        return Response.fromResponse(base).header("Wrong-Param", limpiarCabecera(wrongParam)).build();
    }

    /** 404 con la cabecera Entity-Not-Found. */
    static Response noEncontrado(UUID id, String mensaje) {
        return Response.fromResponse(construir(404, "NO_ENCONTRADO", mensaje))
                .header("Entity-Not-Found", id != null ? id.toString() : "null").build();
    }

    /** Las cabeceras HTTP solo admiten ASCII: se quitan tildes y saltos de línea. */
    static String limpiarCabecera(String texto) {
        String s = Normalizer.normalize(texto, Normalizer.Form.NFD).replaceAll("\\p{M}", "");
        s = s.replaceAll("[\\r\\n\\t]+", " ").replaceAll("[^\\x20-\\x7E]", "?").trim();
        return s.length() > MAX_CABECERA ? s.substring(0, MAX_CABECERA) : s;
    }

    static <T extends Throwable> T buscar(Throwable ex, Class<T> tipo) {
        Throwable actual = ex;
        for (int i = 0; actual != null && i < PROFUNDIDAD_MAXIMA; i++) {
            if (tipo.isInstance(actual)) {
                return tipo.cast(actual);
            }
            if (actual.getCause() == actual) {
                break;
            }
            actual = actual.getCause();
        }
        return null;
    }
}