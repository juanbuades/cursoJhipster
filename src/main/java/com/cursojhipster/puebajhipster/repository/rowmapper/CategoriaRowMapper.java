package com.cursojhipster.puebajhipster.repository.rowmapper;

import com.cursojhipster.puebajhipster.domain.Categoria;
import com.cursojhipster.puebajhipster.service.ColumnConverter;
import io.r2dbc.spi.Row;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;

/**
 * Converter between {@link Row} to {@link Categoria}, with proper type conversions.
 */
@Service
public class CategoriaRowMapper implements BiFunction<Row, String, Categoria> {

    private final ColumnConverter converter;

    public CategoriaRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link Categoria} stored in the database.
     */
    @Override
    public Categoria apply(Row row, String prefix) {
        Categoria entity = new Categoria();
        entity.setId(converter.fromRow(row, prefix + "_id", Long.class));
        entity.setNombre(converter.fromRow(row, prefix + "_nombre", String.class));
        entity.setImagenContentType(converter.fromRow(row, prefix + "_imagen_content_type", String.class));
        entity.setImagen(converter.fromRow(row, prefix + "_imagen", byte[].class));
        return entity;
    }
}
