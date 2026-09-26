/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package sv.edu.ues.occ.ingenieria.ppi115_2026.cafefe.entity;

import jakarta.persistence.Basic;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.NamedQueries;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.io.Serializable;
import java.util.Collection;
import java.util.Date;

/**
 *
 * @author walixx
 */
@Entity
@Table(name = "descuento")
@NamedQueries({
    @NamedQuery(name = "Descuento.findAll", query = "SELECT d FROM Descuento d"),
    @NamedQuery(name = "Descuento.findByNombre", query = "SELECT d FROM Descuento d WHERE d.nombre = :nombre"),
    @NamedQuery(name = "Descuento.findByFechaDesde", query = "SELECT d FROM Descuento d WHERE d.fechaDesde = :fechaDesde"),
    @NamedQuery(name = "Descuento.findByFechaHasta", query = "SELECT d FROM Descuento d WHERE d.fechaHasta = :fechaHasta"),
    @NamedQuery(name = "Descuento.findByObservaciones", query = "SELECT d FROM Descuento d WHERE d.observaciones = :observaciones")})
public class Descuento implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @NotNull
    @Lob
    @Column(name = "id_descuento")
    private Object idDescuento;
    @Lob
    @Column(name = "id_tipo_descuento")
    private Object idTipoDescuento;
    @Size(max = 155)
    @Column(name = "nombre")
    private String nombre;
    @Column(name = "fecha_desde")
    @Temporal(TemporalType.TIMESTAMP)
    private Date fechaDesde;
    @Column(name = "fecha_hasta")
    @Temporal(TemporalType.TIMESTAMP)
    private Date fechaHasta;
    @Size(max = 2147483647)
    @Column(name = "observaciones")
    private String observaciones;
    @OneToMany(mappedBy = "idDescuento")
    private Collection<DescuentoProducto> descuentoProductoCollection;

    public Descuento() {
    }

    public Descuento(Object idDescuento) {
        this.idDescuento = idDescuento;
    }

    public Object getIdDescuento() {
        return idDescuento;
    }

    public void setIdDescuento(Object idDescuento) {
        this.idDescuento = idDescuento;
    }

    public Object getIdTipoDescuento() {
        return idTipoDescuento;
    }

    public void setIdTipoDescuento(Object idTipoDescuento) {
        this.idTipoDescuento = idTipoDescuento;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
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

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }

    public Collection<DescuentoProducto> getDescuentoProductoCollection() {
        return descuentoProductoCollection;
    }

    public void setDescuentoProductoCollection(Collection<DescuentoProducto> descuentoProductoCollection) {
        this.descuentoProductoCollection = descuentoProductoCollection;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (idDescuento != null ? idDescuento.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Descuento)) {
            return false;
        }
        Descuento other = (Descuento) object;
        if ((this.idDescuento == null && other.idDescuento != null) || (this.idDescuento != null && !this.idDescuento.equals(other.idDescuento))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "sv.edu.ues.occ.ingenieria.ppi115_2026.cafefe.entity.Descuento[ idDescuento=" + idDescuento + " ]";
    }
    
}
