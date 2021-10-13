package com.cursojhipster.puebajhipster.repository.rowmapper;

import com.cursojhipster.puebajhipster.domain.Pelicula;
import com.cursojhipster.puebajhipster.service.ColumnConverter;
import io.r2dbc.spi.Row;
import java.time.Instant;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;

/**
 * Converter between {@link Row} to {@link Pelicula}, with proper type conversions.
 */
@Service
public class PeliculaRowMapper implements BiFunction<Row, String, Pelicula> {

    private final ColumnConverter converter;

    public PeliculaRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link Pelicula} stored in the database.
     */
    @Override
    public Pelicula apply(Row row, String prefix) {
        Pelicula entity = new Pelicula();
        entity.setId(converter.fromRow(row, prefix + "_id", Long.class));
        entity.setTitulo(converter.fromRow(row, prefix + "_titulo", String.class));
        entity.setFechaEstreno(converter.fromRow(row, prefix + "_fecha_estreno", Instant.class));
        entity.setDecripcion(converter.fromRow(row, prefix + "_decripcion", String.class));
        entity.setEnCines(converter.fromRow(row, prefix + "_en_cines", Boolean.class));
        entity.setDirectorId(converter.fromRow(row, prefix + "_director_id", Long.class));
        return entity;
    }
}
