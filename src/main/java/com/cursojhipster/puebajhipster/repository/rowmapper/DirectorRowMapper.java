package com.cursojhipster.puebajhipster.repository.rowmapper;

import com.cursojhipster.puebajhipster.domain.Director;
import com.cursojhipster.puebajhipster.service.ColumnConverter;
import io.r2dbc.spi.Row;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;

/**
 * Converter between {@link Row} to {@link Director}, with proper type conversions.
 */
@Service
public class DirectorRowMapper implements BiFunction<Row, String, Director> {

    private final ColumnConverter converter;

    public DirectorRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link Director} stored in the database.
     */
    @Override
    public Director apply(Row row, String prefix) {
        Director entity = new Director();
        entity.setId(converter.fromRow(row, prefix + "_id", Long.class));
        entity.setNombre(converter.fromRow(row, prefix + "_nombre", String.class));
        entity.setApellidos(converter.fromRow(row, prefix + "_apellidos", String.class));
        return entity;
    }
}
