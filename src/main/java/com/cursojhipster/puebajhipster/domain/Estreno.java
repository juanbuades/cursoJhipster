package com.cursojhipster.puebajhipster.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import java.time.Instant;
import javax.validation.constraints.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

/**
 * A Estreno.
 */
@Table("estreno")
public class Estreno implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column("id")
    private Long id;

    @Column("fecha")
    private Instant fecha;

    @Size(min = 4, max = 150)
    @Column("lugar")
    private String lugar;

    @Transient
    private Pelicula pelicula;

    @Column("pelicula_id")
    private Long peliculaId;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Estreno id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Instant getFecha() {
        return this.fecha;
    }

    public Estreno fecha(Instant fecha) {
        this.setFecha(fecha);
        return this;
    }

    public void setFecha(Instant fecha) {
        this.fecha = fecha;
    }

    public String getLugar() {
        return this.lugar;
    }

    public Estreno lugar(String lugar) {
        this.setLugar(lugar);
        return this;
    }

    public void setLugar(String lugar) {
        this.lugar = lugar;
    }

    public Pelicula getPelicula() {
        return this.pelicula;
    }

    public void setPelicula(Pelicula pelicula) {
        this.pelicula = pelicula;
        this.peliculaId = pelicula != null ? pelicula.getId() : null;
    }

    public Estreno pelicula(Pelicula pelicula) {
        this.setPelicula(pelicula);
        return this;
    }

    public Long getPeliculaId() {
        return this.peliculaId;
    }

    public void setPeliculaId(Long pelicula) {
        this.peliculaId = pelicula;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Estreno)) {
            return false;
        }
        return id != null && id.equals(((Estreno) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Estreno{" +
            "id=" + getId() +
            ", fecha='" + getFecha() + "'" +
            ", lugar='" + getLugar() + "'" +
            "}";
    }
}
