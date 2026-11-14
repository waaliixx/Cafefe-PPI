/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package sv.edu.ues.occ.ingenieria.ppi115_2026.cafefe.control;

import jakarta.ejb.ApplicationException;

/**
 *
 * @author walixx
 */
/**
 * Error de negocio al cobrar una orden. Al ser {@code @ApplicationException}
 * con {@code rollback = true}, el contenedor revierte la transacción pero la
 * excepción llega intacta a quien llama (nuestra capa REST la traduce a HTTP).
 */
@ApplicationException(rollback = true)
public class CobroException extends RuntimeException{
    
    private static final long serialVersionUID = 1L;

    public enum Codigo {
        /** Datos faltantes o mal formados (400). */
        DATOS_INVALIDOS,
        /** Orden, caja o empleado inexistente (404). */
        NO_ENCONTRADO,
        /** La orden no tiene líneas por cobrar (409). */
        ORDEN_YA_PAGADA,
        /** Caja o empleado inactivos, o empleado sin rol de cajero (403). */
        NO_AUTORIZADO,
        /** Los pagos no alcanzan para cubrir el total (422). */
        SALDO_INSUFICIENTE,
        /** Lo pagado sin efectivo supera el total, no hay cómo dar vuelto (422). */
        EXCESO_NO_EFECTIVO
    }

    private final Codigo codigo;

    public CobroException(Codigo codigo, String mensaje) {
        super(mensaje);
        this.codigo = codigo;
    }

    public Codigo getCodigo() {
        return codigo;
    }
}
