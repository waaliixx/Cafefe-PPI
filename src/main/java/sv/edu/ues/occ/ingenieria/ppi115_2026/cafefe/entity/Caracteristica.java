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
@Table(name = "caracteristica")
@NamedQueries({
    @NamedQuery(name = "Caracteristica.findAll", query = "SELECT c FROM Caracteristica c"),
    @NamedQuery(name = "Caracteristica.findByNombre", query = "SELECT c FROM Caracteristica c WHERE c.nombre = :nombre"),
    @NamedQuery(name = "Caracteristica.findByActivo", query = "SELECT c FROM Caracteristica c WHERE c.activo = :activo"),
    @NamedQuery(name = "Caracteristica.findByObservaciones", query = "SELECT c FROM Caracteristica c WHERE c.observaciones = :observaciones")})
public class Caracteristica implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @NotNull
    @Column(name = "id_caracteristica")
    private UUID idCaracteristica;
    @Size(max = 155)
    @Column(name = "nombre")
    private String nombre;
    @Column(name = "activo")
    private Boolean activo;
    @Size(max = 2147483647)
    @Column(name = "observaciones")
    private String observaciones;
    @JoinColumn(name = "id_tipo_caracteristica", referencedColumnName = "id_tipo_caracteristica")
    @ManyToOne
    private TipoCaracteristica idTipoCaracteristica;
    @OneToMany(mappedBy = "idCaracteristica")
    private Collection<ProductoCaracteristica> productoCaracteristicaCollection;

    public Caracteristica() {
    }

    public Caracteristica(UUID idCaracteristica) {
        this.idCaracteristica = idCaracteristica;
    }

    public UUID getIdCaracteristica() {
        return idCaracteristica;
    }

    public void setIdCaracteristica(UUID idCaracteristica) {
        this.idCaracteristica = idCaracteristica;
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

    public TipoCaracteristica getIdTipoCaracteristica() {
        return idTipoCaracteristica;
    }

    public void setIdTipoCaracteristica(TipoCaracteristica idTipoCaracteristica) {
        this.idTipoCaracteristica = idTipoCaracteristica;
    }

    public Collection<ProductoCaracteristica> getProductoCaracteristicaCollection() {
        return productoCaracteristicaCollection;
    }

    public void setProductoCaracteristicaCollection(Collection<ProductoCaracteristica> productoCaracteristicaCollection) {
        this.productoCaracteristicaCollection = productoCaracteristicaCollection;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (idCaracteristica != null ? idCaracteristica.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Caracteristica)) {
            return false;
        }
        Caracteristica other = (Caracteristica) object;
        if ((this.idCaracteristica == null && other.idCaracteristica != null) || (this.idCaracteristica != null && !this.idCaracteristica.equals(other.idCaracteristica))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "sv.edu.ues.occ.ingenieria.ppi115_2026.cafefe.entity.Caracteristica[ idCaracteristica=" + idCaracteristica + " ]";
    }
    
}
