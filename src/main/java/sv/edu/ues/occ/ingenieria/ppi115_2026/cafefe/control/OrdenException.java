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
 * Error de negocio al crear una orden. Igual que {@link CobroException}, al ser
 * {@code @ApplicationException} con {@code rollback = true} el contenedor revierte
 * la transacción y la excepción llega intacta a la capa REST.
 */
@ApplicationException(rollback = true)
public class OrdenException extends RuntimeException{
     private static final long serialVersionUID = 1L;

    public enum Codigo {
        /** Datos faltantes o mal formados (400). */
        DATOS_INVALIDOS,
        /** Producto o empleado inexistente (404). */
        NO_ENCONTRADO,
        /** El empleado no puede tomar órdenes (403). */
        NO_AUTORIZADO,
        /** El producto no está activo (409). */
        PRODUCTO_INACTIVO
    }

    private final Codigo codigo;

    public OrdenException(Codigo codigo, String mensaje) {
        super(mensaje);
        this.codigo = codigo;
    }

    public Codigo getCodigo() {
        return codigo;
    }
}
