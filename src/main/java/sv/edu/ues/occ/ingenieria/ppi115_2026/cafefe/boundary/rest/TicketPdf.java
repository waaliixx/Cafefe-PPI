package sv.edu.ues.occ.ingenieria.ppi115_2026.cafefe.boundary.rest;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.charset.Charset;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import sv.edu.ues.occ.ingenieria.ppi115_2026.cafefe.control.TicketService.LineaTicket;
import sv.edu.ues.occ.ingenieria.ppi115_2026.cafefe.control.TicketService.PagoTicket;
import sv.edu.ues.occ.ingenieria.ppi115_2026.cafefe.control.TicketService.Ticket;

//Dibujo del Ticket sin librerias externas

public final class TicketPdf {

    private static final Charset CP1252 = Charset.forName("windows-1252");
    static final float ANCHO = 226f;   // 80 mm en puntos
    static final float MARGEN = 12f;
    static final ZoneId ZONA = ZoneId.of("America/El_Salvador");
    static final DateTimeFormatter FORMATO_FECHA = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    private TicketPdf() {
    }

    /** Una fila: texto a la izquierda, importe a la derecha y estilo (N normal, B negrita, C centrado, T título, L línea). */
    record Fila(String izquierda, String derecha, char estilo, float alto) {
    }

    public static byte[] generar(Ticket t) {
        List<Fila> filas = new ArrayList<>();
        agregar(filas, "Cafefe", null, 'T');
        agregar(filas, "Ticket de venta", null, 'C');
        agregar(filas, null, null, 'L');
        agregar(filas, "Factura: " + corto(t.idFactura()), null, 'N');
        agregar(filas, "Fecha: " + (t.fecha() != null ? FORMATO_FECHA.format(t.fecha().atZone(ZONA)) : "-"), null, 'N');
        agregar(filas, "Cliente: " + valor(t.cliente()), null, 'N');
        agregar(filas, "Caja: " + valor(t.caja()), null, 'N');
        agregar(filas, "Atendió: " + valor(t.cajero()), null, 'N');
        if (!"Pagada".equalsIgnoreCase(t.estado())) {
            agregar(filas, "Estado: " + valor(t.estado()), null, 'B');
        }
        agregar(filas, null, null, 'L');
        for (LineaTicket l : t.lineas()) {
            agregar(filas, l.cantidad() + " x " + l.producto(), dinero(l.subtotal()), 'N');
            if (l.cantidad() > 1) {
                agregar(filas, "(" + dinero(l.precioUnitario()) + " c/u)", null, 'N');
            }
        }
        agregar(filas, null, null, 'L');
        agregar(filas, "TOTAL", dinero(t.total()), 'B');
        for (PagoTicket p : t.pagos()) {
            String forma = p.tipoPago() + (p.referencia() != null ? " (" + p.referencia() + ")" : "");
            agregar(filas, forma, dinero(p.monto()), 'N');
        }
        agregar(filas, "Pagado", dinero(t.pagado()), 'B');
        agregar(filas, "Vuelto", dinero(t.vuelto()), 'B');
        agregar(filas, null, null, 'L');
        agregar(filas, "¡Gracias por su visita!", null, 'C');
        return escribir(filas, "Ticket " + t.idFactura());
    }

    /** Agrega la fila; si el texto es largo lo parte en varias, con el importe en la primera. */
    static void agregar(List<Fila> filas, String izquierda, String derecha, char estilo) {
        if (estilo == 'L') {
            filas.add(new Fila("", null, 'L', 7f));
            return;
        }
        float alto = estilo == 'T' ? 20f : 11f;
        int max = estilo == 'T' ? 22 : (derecha != null ? 34 : 46);
        List<String> partes = envolver(izquierda == null ? "" : izquierda, max);
        for (int i = 0; i < partes.size(); i++) {
            filas.add(new Fila(partes.get(i), i == 0 ? derecha : null, estilo, alto));
        }
    }

    static List<String> envolver(String texto, int max) {
        List<String> lineas = new ArrayList<>();
        String actual = "";
        for (String palabra : texto.replaceAll("\\p{Cntrl}", " ").trim().split("\\s+")) {
            while (palabra.length() > max) {
                if (!actual.isEmpty()) {
                    lineas.add(actual);
                    actual = "";
                }
                lineas.add(palabra.substring(0, max));
                palabra = palabra.substring(max);
            }
            if (actual.isEmpty()) {
                actual = palabra;
            } else if (actual.length() + 1 + palabra.length() <= max) {
                actual += " " + palabra;
            } else {
                lineas.add(actual);
                actual = palabra;
            }
        }
        if (!actual.isEmpty() || lineas.isEmpty()) {
            lineas.add(actual);
        }
        return lineas;
    }

    private static byte[] escribir(List<Fila> filas, String titulo) {
        float alto = 2 * MARGEN;
        for (Fila f : filas) {
            alto += f.alto();
        }
        StringBuilder c = new StringBuilder();
        float y = alto - MARGEN;
        for (Fila f : filas) {
            y -= f.alto();
            if (f.estilo() == 'L') {
                float medio = y + f.alto() / 2;
                c.append("0.5 w 0.6 G ").append(num(MARGEN)).append(' ').append(num(medio)).append(" m ")
                        .append(num(ANCHO - MARGEN)).append(' ').append(num(medio)).append(" l S\n");
                continue;
            }
            boolean negrita = f.estilo() == 'B' || f.estilo() == 'T';
            float tam = f.estilo() == 'T' ? 14f : 8f;
            String fuente = negrita ? "/F2" : "/F1";
            float x = MARGEN;
            if (f.estilo() == 'C' || f.estilo() == 'T') {
                x = Math.max(MARGEN, (ANCHO - ancho(f.izquierda(), tam, negrita)) / 2);
            }
            texto(c, fuente, tam, x, y + 3, f.izquierda());
            if (f.derecha() != null) {
                texto(c, fuente, tam, ANCHO - MARGEN - ancho(f.derecha(), tam, negrita), y + 3, f.derecha());
            }
        }
        try {
            byte[] contenido = c.toString().getBytes(CP1252);
            ByteArrayOutputStream o = new ByteArrayOutputStream();
            List<Integer> posiciones = new ArrayList<>();
            texto(o, "%PDF-1.4\n");
            posiciones.add(o.size());
            texto(o, "1 0 obj\n<< /Type /Catalog /Pages 2 0 R >>\nendobj\n");
            posiciones.add(o.size());
            texto(o, "2 0 obj\n<< /Type /Pages /Kids [3 0 R] /Count 1 >>\nendobj\n");
            posiciones.add(o.size());
            texto(o, "3 0 obj\n<< /Type /Page /Parent 2 0 R /MediaBox [0 0 " + num(ANCHO) + " " + num(alto)
                    + "] /Resources << /Font << /F1 4 0 R /F2 5 0 R >> >> /Contents 6 0 R >>\nendobj\n");
            posiciones.add(o.size());
            texto(o, "4 0 obj\n<< /Type /Font /Subtype /Type1 /BaseFont /Helvetica /Encoding /WinAnsiEncoding >>\nendobj\n");
            posiciones.add(o.size());
            texto(o, "5 0 obj\n<< /Type /Font /Subtype /Type1 /BaseFont /Helvetica-Bold /Encoding /WinAnsiEncoding >>\nendobj\n");
            posiciones.add(o.size());
            texto(o, "6 0 obj\n<< /Length " + contenido.length + " >>\nstream\n");
            o.write(contenido);
            texto(o, "endstream\nendobj\n");
            int xref = o.size();
            texto(o, "xref\n0 7\n0000000000 65535 f \n");
            for (int p : posiciones) {
                texto(o, String.format(Locale.ROOT, "%010d 00000 n \n", p));
            }
            texto(o, "trailer\n<< /Size 7 /Root 1 0 R /Info << /Title (" + escapar(titulo)
                    + ") /Creator (Cafefe) >> >>\nstartxref\n" + xref + "\n%%EOF\n");
            return o.toByteArray();
        } catch (IOException ex) {
            throw new IllegalStateException("No se pudo generar el PDF del ticket", ex);
        }
    }

    private static void texto(StringBuilder c, String fuente, float tam, float x, float y, String valor) {
        c.append("BT ").append(fuente).append(' ').append(num(tam)).append(" Tf ")
                .append(num(x)).append(' ').append(num(y)).append(" Td (")
                .append(escapar(valor)).append(") Tj ET\n");
    }

    private static void texto(ByteArrayOutputStream o, String s) throws IOException {
        o.write(s.getBytes(CP1252));
    }

    private static String escapar(String s) {
        return s.replace("\\", "\\\\").replace("(", "\\(").replace(")", "\\)");
    }

    private static String num(float v) {
        return String.format(Locale.ROOT, "%.2f", v);
    }

    /** Ancho aproximado en puntos (suficiente para centrar y alinear a la derecha). */
    static float ancho(String s, float tam, boolean negrita) {
        float total = 0;
        for (char ch : s.toCharArray()) {
            if (ch == ' ' || ch == '.' || ch == ',' || ch == ':') {
                total += 0.278f;
            } else if (Character.isDigit(ch) || ch == '$') {
                total += 0.556f;
            } else {
                total += Character.isUpperCase(ch) ? 0.68f : 0.52f;
            }
        }
        return total * tam * (negrita ? 1.05f : 1f);
    }

    static String dinero(BigDecimal valor) {
        return "$" + (valor == null ? "0.00" : valor.setScale(2, RoundingMode.HALF_UP).toPlainString());
    }

    /** Las 8 primeras letras del UUID bastan para identificar el ticket a la vista. */
    static String corto(String id) {
        return id == null ? "-" : id.substring(0, Math.min(8, id.length())).toUpperCase(Locale.ROOT);
    }

    private static String valor(String s) {
        return s == null || s.isBlank() ? "-" : s;
    }
}