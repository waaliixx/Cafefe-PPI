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
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.io.Serializable;
import java.util.Collection;

/**
 *
 * @author walixx
 */
@Entity
@Table(name = "empleado_rol")
@NamedQueries({
    @NamedQuery(name = "EmpleadoRol.findAll", query = "SELECT e FROM EmpleadoRol e"),
    @NamedQuery(name = "EmpleadoRol.findByActivo", query = "SELECT e FROM EmpleadoRol e WHERE e.activo = :activo"),
    @NamedQuery(name = "EmpleadoRol.findByObservaciones", query = "SELECT e FROM EmpleadoRol e WHERE e.observaciones = :observaciones")})
public class EmpleadoRol implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @NotNull
    @Lob
    @Column(name = "id_empleado_rol")
    private Object idEmpleadoRol;
    @Lob
    @Column(name = "id_empleado")
    private Object idEmpleado;
    @Column(name = "activo")
    private Boolean activo;
    @Size(max = 2147483647)
    @Column(name = "observaciones")
    private String observaciones;
    @JoinColumn(name = "id_rol", referencedColumnName = "id_rol")
    @ManyToOne
    private Rol idRol;
    @OneToMany(mappedBy = "idEmpleadoRol")
    private Collection<Factura> facturaCollection;
    @OneToMany(mappedBy = "idEmpleadoRol")
    private Collection<Orden> ordenCollection;

    public EmpleadoRol() {
    }

    public EmpleadoRol(Object idEmpleadoRol) {
        this.idEmpleadoRol = idEmpleadoRol;
    }

    public Object getIdEmpleadoRol() {
        return idEmpleadoRol;
    }

    public void setIdEmpleadoRol(Object idEmpleadoRol) {
        this.idEmpleadoRol = idEmpleadoRol;
    }

    public Object getIdEmpleado() {
        return idEmpleado;
    }

    public void setIdEmpleado(Object idEmpleado) {
        this.idEmpleado = idEmpleado;
    }

    public Boolean getActivo() {
        return activo;
    }

    public void setActivo(Boolean activo) {
        this.activo = activo;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }

    public Rol getIdRol() {
        return idRol;
    }

    public void setIdRol(Rol idRol) {
        this.idRol = idRol;
    }

    public Collection<Factura> getFacturaCollection() {
        return facturaCollection;
    }

    public void setFacturaCollection(Collection<Factura> facturaCollection) {
        this.facturaCollection = facturaCollection;
    }

    public Collection<Orden> getOrdenCollection() {
        return ordenCollection;
    }

    public void setOrdenCollection(Collection<Orden> ordenCollection) {
        this.ordenCollection = ordenCollection;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (idEmpleadoRol != null ? idEmpleadoRol.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof EmpleadoRol)) {
            return false;
        }
        EmpleadoRol other = (EmpleadoRol) object;
        if ((this.idEmpleadoRol == null && other.idEmpleadoRol != null) || (this.idEmpleadoRol != null && !this.idEmpleadoRol.equals(other.idEmpleadoRol))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "sv.edu.ues.occ.ingenieria.ppi115_2026.cafefe.entity.EmpleadoRol[ idEmpleadoRol=" + idEmpleadoRol + " ]";
    }
    
}
