/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package sv.edu.ues.occ.ingenieria.ppi115_2026.cafefe.entity;

import jakarta.persistence.Basic;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.NamedQueries;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.io.Serializable;
import java.util.Date;

/**
 *
 * @author walixx
 */
@Entity
@Table(name = "descuento_producto")
@NamedQueries({
    @NamedQuery(name = "DescuentoProducto.findAll", query = "SELECT d FROM DescuentoProducto d"),
    @NamedQuery(name = "DescuentoProducto.findByFechaDesde", query = "SELECT d FROM DescuentoProducto d WHERE d.fechaDesde = :fechaDesde"),
    @NamedQuery(name = "DescuentoProducto.findByFechaHasta", query = "SELECT d FROM DescuentoProducto d WHERE d.fechaHasta = :fechaHasta"),
    @NamedQuery(name = "DescuentoProducto.findByValor", query = "SELECT d FROM DescuentoProducto d WHERE d.valor = :valor"),
    @NamedQuery(name = "DescuentoProducto.findByObservaciones", query = "SELECT d FROM DescuentoProducto d WHERE d.observaciones = :observaciones")})
public class DescuentoProducto implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @NotNull
    @Lob
    @Column(name = "id_descuento_producto")
    private Object idDescuentoProducto;
    @Column(name = "fecha_desde")
    @Temporal(TemporalType.TIMESTAMP)
    private Date fechaDesde;
    @Column(name = "fecha_hasta")
    @Temporal(TemporalType.TIMESTAMP)
    private Date fechaHasta;
    @Column(name = "valor")
    private Integer valor;
    @Size(max = 2147483647)
    @Column(name = "observaciones")
    private String observaciones;
    @JoinColumn(name = "id_descuento", referencedColumnName = "id_descuento")
    @ManyToOne
    private Descuento idDescuento;
    @JoinColumn(name = "id_producto", referencedColumnName = "id_producto")
    @ManyToOne
    private Producto idProducto;

    public DescuentoProducto() {
    }

    public DescuentoProducto(Object idDescuentoProducto) {
        this.idDescuentoProducto = idDescuentoProducto;
    }

    public Object getIdDescuentoProducto() {
        return idDescuentoProducto;
    }

    public void setIdDescuentoProducto(Object idDescuentoProducto) {
        this.idDescuentoProducto = idDescuentoProducto;
    }

    public Date getFechaDesde() {
        return fechaDesde;
    }

    public void setFechaDesde(Date fechaDesde) {
        this.fechaDesde = fechaDesde;
    }

    public Date getFechaHasta() {
        return fechaHasta;
    }

    public void setFechaHasta(Date fechaHasta) {
        this.fechaHasta = fechaHasta;
    }

    public Integer getValor() {
        return valor;
    }

    public void setValor(Integer valor) {
        this.valor = valor;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }

    public Descuento getIdDescuento() {
        return idDescuento;
    }

    public void setIdDescuento(Descuento idDescuento) {
        this.idDescuento = idDescuento;
    }

    public Producto getIdProducto() {
        return idProducto;
    }

    public void setIdProducto(Producto idProducto) {
        this.idProducto = idProducto;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (idDescuentoProducto != null ? idDescuentoProducto.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof DescuentoProducto)) {
            return false;
        }
        DescuentoProducto other = (DescuentoProducto) object;
        if ((this.idDescuentoProducto == null && other.idDescuentoProducto != null) || (this.idDescuentoProducto != null && !this.idDescuentoProducto.equals(other.idDescuentoProducto))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "sv.edu.ues.occ.ingenieria.ppi115_2026.cafefe.entity.DescuentoProducto[ idDescuentoProducto=" + idDescuentoProducto + " ]";
    }
    
}
