package com.cursojhipster.puebajhipster.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import javax.validation.constraints.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

/**
 * A Director.
 */
@Table("director")
public class Director implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column("id")
    private Long id;

    @Size(min = 3, max = 50)
    @Column("nombre")
    private String nombre;

    @Size(min = 3, max = 70)
    @Column("apellidos")
    private String apellidos;

    @Transient
    @JsonIgnoreProperties(value = { "estreno", "director" }, allowSetters = true)
    private Set<Pelicula> peliculas = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Director id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombre() {
        return this.nombre;
    }

    public Director nombre(String nombre) {
        this.setNombre(nombre);
        return this;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellidos() {
        return this.apellidos;
    }

    public Director apellidos(String apellidos) {
        this.setApellidos(apellidos);
        return this;
    }

    public void setApellidos(String apellidos) {
        this.apellidos = apellidos;
    }

    public Set<Pelicula> getPeliculas() {
        return this.peliculas;
    }

    public void setPeliculas(Set<Pelicula> peliculas) {
        if (this.peliculas != null) {
            this.peliculas.forEach(i -> i.setDirector(null));
        }
        if (peliculas != null) {
            peliculas.forEach(i -> i.setDirector(this));
        }
        this.peliculas = peliculas;
    }

    public Director peliculas(Set<Pelicula> peliculas) {
        this.setPeliculas(peliculas);
        return this;
    }

    public Director addPelicula(Pelicula pelicula) {
        this.peliculas.add(pelicula);
        pelicula.setDirector(this);
        return this;
    }

    public Director removePelicula(Pelicula pelicula) {
        this.peliculas.remove(pelicula);
        pelicula.setDirector(null);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Director)) {
            return false;
        }
        return id != null && id.equals(((Director) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Director{" +
            "id=" + getId() +
            ", nombre='" + getNombre() + "'" +
            ", apellidos='" + getApellidos() + "'" +
            "}";
    }
}
