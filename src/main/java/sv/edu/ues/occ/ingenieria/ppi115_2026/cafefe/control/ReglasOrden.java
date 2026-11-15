package sv.edu.ues.occ.ingenieria.ppi115_2026.cafefe.control;

import java.text.Normalizer;
import java.util.Locale;
import java.util.Set;
import sv.edu.ues.occ.ingenieria.ppi115_2026.cafefe.entity.Rol;

/** Reglas de las órdenes que no consultan la base de datos. */
public final class ReglasOrden {

    /** Las observaciones de cada línea empiezan así: "Cliente: Ana | sin azúcar". */
    public static final String PREFIJO_CLIENTE = "Cliente: ";
    public static final String SEPARADOR = " | ";

    /** Roles del mesero virtual, en minúsculas y sin tildes. */
    static final Set<String> ROLES_EN_LINEA = Set.of("tienda en linea", "mesero virtual");

    private ReglasOrden() {
    }

    /** "  Tienda  en LÍNEA " → "tienda en linea" (para comparar sin importar tildes ni mayúsculas). */
    public static String normalizar(String texto) {
        if (texto == null) {
            return "";
        }
        String sinTildes = Normalizer.normalize(texto.trim(), Normalizer.Form.NFD).replaceAll("\\p{M}", "");
        return sinTildes.toLowerCase(Locale.ROOT).replaceAll("\\s+", " ");
    }

    public static boolean esRolEnLinea(Rol rol) {
        return rol != null && ROLES_EN_LINEA.contains(normalizar(rol.getNombre()));
    }

    public static String formatearObservaciones(String cliente, String nota) {
        if (nota == null || nota.isBlank()) {
            return PREFIJO_CLIENTE + cliente;
        }
        return PREFIJO_CLIENTE + cliente + SEPARADOR + nota.trim();
    }

    /** "Cliente: Ana | sin azúcar" → "Ana"; "Mesa 4" → null. */
    public static String clienteDesde(String observaciones) {
        if (observaciones == null || !observaciones.startsWith(PREFIJO_CLIENTE)) {
            return null;
        }
        String resto = observaciones.substring(PREFIJO_CLIENTE.length());
        int corte = resto.indexOf(SEPARADOR);
        String cliente = (corte >= 0 ? resto.substring(0, corte) : resto).trim();
        return cliente.isEmpty() ? null : cliente;
    }

    /** Lo que debe leer el barista, sin el cliente: "Cliente: Ana | sin azúcar" → "sin azúcar". */
    public static String notaDe(String observaciones) {
        if (observaciones == null || observaciones.isBlank()) {
            return null;
        }
        if (!observaciones.startsWith(PREFIJO_CLIENTE)) {
            return observaciones.trim();
        }
        int corte = observaciones.indexOf(SEPARADOR);
        if (corte < 0) {
            return null;
        }
        String nota = observaciones.substring(corte + SEPARADOR.length()).trim();
        return nota.isEmpty() ? null : nota;
    }
}