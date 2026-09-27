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
@Table(name = "tipo_caracteristica")
@NamedQueries({
    @NamedQuery(name = "TipoCaracteristica.findAll", query = "SELECT t FROM TipoCaracteristica t"),
    @NamedQuery(name = "TipoCaracteristica.findByNombre", query = "SELECT t FROM TipoCaracteristica t WHERE t.nombre = :nombre"),
    @NamedQuery(name = "TipoCaracteristica.findByExpresionRegular", query = "SELECT t FROM TipoCaracteristica t WHERE t.expresionRegular = :expresionRegular"),
    @NamedQuery(name = "TipoCaracteristica.findByActivo", query = "SELECT t FROM TipoCaracteristica t WHERE t.activo = :activo"),
    @NamedQuery(name = "TipoCaracteristica.findByObservaciones", query = "SELECT t FROM TipoCaracteristica t WHERE t.observaciones = :observaciones")})
public class TipoCaracteristica implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @NotNull
    @Column(name = "id_tipo_caracteristica")
    private UUID idTipoCaracteristica;
    @Size(max = 155)
    @Column(name = "nombre")
    private String nombre;
    @Size(max = 2147483647)
    @Column(name = "expresion_regular")
    private String expresionRegular;
    @Column(name = "activo")
    private Boolean activo;
    @Size(max = 2147483647)
    @Column(name = "observaciones")
    private String observaciones;
    @OneToMany(mappedBy = "idTipoCaracteristica")
    private Collection<Caracteristica> caracteristicaCollection;

    public TipoCaracteristica() {
    }

    public TipoCaracteristica(UUID idTipoCaracteristica) {
        this.idTipoCaracteristica = idTipoCaracteristica;
    }

    public UUID getIdTipoCaracteristica() {
        return idTipoCaracteristica;
    }

    public void setIdTipoCaracteristica(UUID idTipoCaracteristica) {
        this.idTipoCaracteristica = idTipoCaracteristica;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getExpresionRegular() {
        return expresionRegular;
    }

    public void setExpresionRegular(String expresionRegular) {
        this.expresionRegular = expresionRegular;
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

    public Collection<Caracteristica> getCaracteristicaCollection() {
        return caracteristicaCollection;
    }

    public void setCaracteristicaCollection(Collection<Caracteristica> caracteristicaCollection) {
        this.caracteristicaCollection = caracteristicaCollection;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (idTipoCaracteristica != null ? idTipoCaracteristica.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof TipoCaracteristica)) {
            return false;
        }
        TipoCaracteristica other = (TipoCaracteristica) object;
        if ((this.idTipoCaracteristica == null && other.idTipoCaracteristica != null) || (this.idTipoCaracteristica != null && !this.idTipoCaracteristica.equals(other.idTipoCaracteristica))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "sv.edu.ues.occ.ingenieria.ppi115_2026.cafefe.entity.TipoCaracteristica[ idTipoCaracteristica=" + idTipoCaracteristica + " ]";
    }
    
}
