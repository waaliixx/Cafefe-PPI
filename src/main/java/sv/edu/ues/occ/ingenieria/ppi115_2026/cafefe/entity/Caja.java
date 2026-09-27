/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package sv.edu.ues.occ.ingenieria.ppi115_2026.cafefe.entity;

import jakarta.persistence.Basic;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.NamedQueries;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.io.Serializable;
import java.util.Collection;
import java.util.UUID;

/**
 *
 * @author walixx
 */
@Entity
@Table(name = "caja")
@NamedQueries({
    @NamedQuery(name = "Caja.findAll", query = "SELECT c FROM Caja c"),
    @NamedQuery(name = "Caja.findByNombre", query = "SELECT c FROM Caja c WHERE c.nombre = :nombre"),
    @NamedQuery(name = "Caja.findByActivo", query = "SELECT c FROM Caja c WHERE c.activo = :activo"),
    @NamedQuery(name = "Caja.findByObservaciones", query = "SELECT c FROM Caja c WHERE c.observaciones = :observaciones")})
public class Caja implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @NotNull
    @Column(name = "id_caja")
    private UUID idCaja;
    @Size(max = 155)
    @Column(name = "nombre")
    private String nombre;
    @Column(name = "activo")
    private Boolean activo;
    @Size(max = 2147483647)
    @Column(name = "observaciones")
    private String observaciones;
    @OneToMany(mappedBy = "idCaja")
    private Collection<Factura> facturaCollection;

    public Caja() {
    }

    public Caja(UUID idCaja) {
        this.idCaja = idCaja;
    }

    public UUID getIdCaja() {
        return idCaja;
    }

    public void setIdCaja(UUID idCaja) {
        this.idCaja = idCaja;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
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

    public Collection<Factura> getFacturaCollection() {
        return facturaCollection;
    }

    public void setFacturaCollection(Collection<Factura> facturaCollection) {
        this.facturaCollection = facturaCollection;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (idCaja != null ? idCaja.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Caja)) {
            return false;
        }
        Caja other = (Caja) object;
        if ((this.idCaja == null && other.idCaja != null) || (this.idCaja != null && !this.idCaja.equals(other.idCaja))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "sv.edu.ues.occ.ingenieria.ppi115_2026.cafefe.entity.Caja[ idCaja=" + idCaja + " ]";
    }
    
}
