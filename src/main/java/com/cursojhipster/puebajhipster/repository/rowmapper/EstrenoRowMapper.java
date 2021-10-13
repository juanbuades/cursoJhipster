package com.cursojhipster.puebajhipster.repository.rowmapper;

import com.cursojhipster.puebajhipster.domain.Estreno;
import com.cursojhipster.puebajhipster.service.ColumnConverter;
import io.r2dbc.spi.Row;
import java.time.Instant;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;

/**
 * Converter between {@link Row} to {@link Estreno}, with proper type conversions.
 */
@Service
public class EstrenoRowMapper implements BiFunction<Row, String, Estreno> {

    private final ColumnConverter converter;

    public EstrenoRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link Estreno} stored in the database.
     */
    @Override
    public Estreno apply(Row row, String prefix) {
        Estreno entity = new Estreno();
        entity.setId(converter.fromRow(row, prefix + "_id", Long.class));
        entity.setFecha(converter.fromRow(row, prefix + "_fecha", Instant.class));
        entity.setLugar(converter.fromRow(row, prefix + "_lugar", String.class));
        entity.setPeliculaId(converter.fromRow(row, prefix + "_pelicula_id", Long.class));
        return entity;
    }
}
