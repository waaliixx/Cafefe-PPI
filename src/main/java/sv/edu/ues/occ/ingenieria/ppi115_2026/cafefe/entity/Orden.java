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
import jakarta.persistence.ManyToOne;
import jakarta.persistence.NamedQueries;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Collection;
import java.util.Date;
import java.util.UUID;

/**
 *
 * @author walixx
 */
@Entity
@Table(name = "orden")
@NamedQueries({
    @NamedQuery(name = "Orden.findAll", query = "SELECT o FROM Orden o"),
    @NamedQuery(name = "Orden.findByFechaCreacion", query = "SELECT o FROM Orden o WHERE o.fechaCreacion = :fechaCreacion")})
public class Orden implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @NotNull
    @Column(name = "id_orden")
    private UUID idOrden;
    @Column(name = "fecha_creacion")
    @Temporal(TemporalType.TIMESTAMP)
    private Date fechaCreacion;
    @OneToMany(mappedBy = "idOrden")
    private Collection<OrdenProducto> ordenProductoCollection;
    @JoinColumn(name = "id_empleado_rol", referencedColumnName = "id_empleado_rol")
    @ManyToOne
    private EmpleadoRol idEmpleadoRol;

    public Orden() {
    }

    public Orden(UUID idOrden) {
        this.idOrden = idOrden;
    }

    public UUID getIdOrden() {
        return idOrden;
    }

    public void setIdOrden(UUID idOrden) {
        this.idOrden = idOrden;
    }

    public Date getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(Date fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    public Collection<OrdenProducto> getOrdenProductoCollection() {
        return ordenProductoCollection;
    }

    public void setOrdenProductoCollection(Collection<OrdenProducto> ordenProductoCollection) {
        this.ordenProductoCollection = ordenProductoCollection;
    }

    public EmpleadoRol getIdEmpleadoRol() {
        return idEmpleadoRol;
    }

    public void setIdEmpleadoRol(EmpleadoRol idEmpleadoRol) {
        this.idEmpleadoRol = idEmpleadoRol;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (idOrden != null ? idOrden.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Orden)) {
            return false;
        }
        Orden other = (Orden) object;
        if ((this.idOrden == null && other.idOrden != null) || (this.idOrden != null && !this.idOrden.equals(other.idOrden))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "sv.edu.ues.occ.ingenieria.ppi115_2026.cafefe.entity.Orden[ idOrden=" + idOrden + " ]";
    }
    
}
