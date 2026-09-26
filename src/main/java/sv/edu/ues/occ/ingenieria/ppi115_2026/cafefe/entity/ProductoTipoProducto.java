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
@Table(name = "producto_tipo_producto")
@NamedQueries({
    @NamedQuery(name = "ProductoTipoProducto.findAll", query = "SELECT p FROM ProductoTipoProducto p"),
    @NamedQuery(name = "ProductoTipoProducto.findByFechaCreacion", query = "SELECT p FROM ProductoTipoProducto p WHERE p.fechaCreacion = :fechaCreacion"),
    @NamedQuery(name = "ProductoTipoProducto.findByObservaciones", query = "SELECT p FROM ProductoTipoProducto p WHERE p.observaciones = :observaciones")})
public class ProductoTipoProducto implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @NotNull
    @Lob
    @Column(name = "id_producto_tipo_producto")
    private Object idProductoTipoProducto;
    @Column(name = "fecha_creacion")
    @Temporal(TemporalType.TIMESTAMP)
    private Date fechaCreacion;
    @Size(max = 2147483647)
    @Column(name = "observaciones")
    private String observaciones;
    @JoinColumn(name = "id_producto", referencedColumnName = "id_producto")
    @ManyToOne
    private Producto idProducto;
    @JoinColumn(name = "id_tipo_producto", referencedColumnName = "id_tipo_producto")
    @ManyToOne
    private TipoProducto idTipoProducto;

    public ProductoTipoProducto() {
    }

    public ProductoTipoProducto(Object idProductoTipoProducto) {
        this.idProductoTipoProducto = idProductoTipoProducto;
    }

    public Object getIdProductoTipoProducto() {
        return idProductoTipoProducto;
    }

    public void setIdProductoTipoProducto(Object idProductoTipoProducto) {
        this.idProductoTipoProducto = idProductoTipoProducto;
    }

    public Date getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(Date fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }

    public Producto getIdProducto() {
        return idProducto;
    }

    public void setIdProducto(Producto idProducto) {
        this.idProducto = idProducto;
    }

    public TipoProducto getIdTipoProducto() {
        return idTipoProducto;
    }

    public void setIdTipoProducto(TipoProducto idTipoProducto) {
        this.idTipoProducto = idTipoProducto;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (idProductoTipoProducto != null ? idProductoTipoProducto.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof ProductoTipoProducto)) {
            return false;
        }
        ProductoTipoProducto other = (ProductoTipoProducto) object;
        if ((this.idProductoTipoProducto == null && other.idProductoTipoProducto != null) || (this.idProductoTipoProducto != null && !this.idProductoTipoProducto.equals(other.idProductoTipoProducto))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "sv.edu.ues.occ.ingenieria.ppi115_2026.cafefe.entity.ProductoTipoProducto[ idProductoTipoProducto=" + idProductoTipoProducto + " ]";
    }
    
}
