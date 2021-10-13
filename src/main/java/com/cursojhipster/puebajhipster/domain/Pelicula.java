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
 * A Pelicula.
 */
@Table("pelicula")
public class Pelicula implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column("id")
    private Long id;

    @NotNull(message = "must not be null")
    @Size(min = 4, max = 50)
    @Column("titulo")
    private String titulo;

    @Column("fecha_estreno")
    private Instant fechaEstreno;

    @Size(min = 20, max = 500)
    @Column("decripcion")
    private String decripcion;

    @Column("en_cines")
    private Boolean enCines;

    @Transient
    private Estreno estreno;

    @Transient
    @JsonIgnoreProperties(value = { "peliculas" }, allowSetters = true)
    private Director director;

    @Column("director_id")
    private Long directorId;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Pelicula id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitulo() {
        return this.titulo;
    }

    public Pelicula titulo(String titulo) {
        this.setTitulo(titulo);
        return this;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public Instant getFechaEstreno() {
        return this.fechaEstreno;
    }

    public Pelicula fechaEstreno(Instant fechaEstreno) {
        this.setFechaEstreno(fechaEstreno);
        return this;
    }

    public void setFechaEstreno(Instant fechaEstreno) {
        this.fechaEstreno = fechaEstreno;
    }

    public String getDecripcion() {
        return this.decripcion;
    }

    public Pelicula decripcion(String decripcion) {
        this.setDecripcion(decripcion);
        return this;
    }

    public void setDecripcion(String decripcion) {
        this.decripcion = decripcion;
    }

    public Boolean getEnCines() {
        return this.enCines;
    }

    public Pelicula enCines(Boolean enCines) {
        this.setEnCines(enCines);
        return this;
    }

    public void setEnCines(Boolean enCines) {
        this.enCines = enCines;
    }

    public Estreno getEstreno() {
        return this.estreno;
    }

    public void setEstreno(Estreno estreno) {
        if (this.estreno != null) {
            this.estreno.setPelicula(null);
        }
        if (estreno != null) {
            estreno.setPelicula(this);
        }
        this.estreno = estreno;
    }

    public Pelicula estreno(Estreno estreno) {
        this.setEstreno(estreno);
        return this;
    }

    public Director getDirector() {
        return this.director;
    }

    public void setDirector(Director director) {
        this.director = director;
        this.directorId = director != null ? director.getId() : null;
    }

    public Pelicula director(Director director) {
        this.setDirector(director);
        return this;
    }

    public Long getDirectorId() {
        return this.directorId;
    }

    public void setDirectorId(Long director) {
        this.directorId = director;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Pelicula)) {
            return false;
        }
        return id != null && id.equals(((Pelicula) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Pelicula{" +
            "id=" + getId() +
            ", titulo='" + getTitulo() + "'" +
            ", fechaEstreno='" + getFechaEstreno() + "'" +
            ", decripcion='" + getDecripcion() + "'" +
            ", enCines='" + getEnCines() + "'" +
            "}";
    }
}
