package com.cursojhipster.puebajhipster.repository;

import static org.springframework.data.relational.core.query.Criteria.where;
import static org.springframework.data.relational.core.query.Query.query;

import com.cursojhipster.puebajhipster.domain.Pelicula;
import com.cursojhipster.puebajhipster.repository.rowmapper.DirectorRowMapper;
import com.cursojhipster.puebajhipster.repository.rowmapper.PeliculaRowMapper;
import com.cursojhipster.puebajhipster.service.EntityManager;
import io.r2dbc.spi.Row;
import io.r2dbc.spi.RowMetadata;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.function.BiFunction;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.data.relational.core.sql.Column;
import org.springframework.data.relational.core.sql.Expression;
import org.springframework.data.relational.core.sql.Select;
import org.springframework.data.relational.core.sql.SelectBuilder.SelectFromAndJoinCondition;
import org.springframework.data.relational.core.sql.Table;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.r2dbc.core.RowsFetchSpec;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data SQL reactive custom repository implementation for the Pelicula entity.
 */
@SuppressWarnings("unused")
class PeliculaRepositoryInternalImpl implements PeliculaRepositoryInternal {

    private final DatabaseClient db;
    private final R2dbcEntityTemplate r2dbcEntityTemplate;
    private final EntityManager entityManager;

    private final DirectorRowMapper directorMapper;
    private final PeliculaRowMapper peliculaMapper;

    private static final Table entityTable = Table.aliased("pelicula", EntityManager.ENTITY_ALIAS);
    private static final Table directorTable = Table.aliased("director", "director");

    public PeliculaRepositoryInternalImpl(
        R2dbcEntityTemplate template,
        EntityManager entityManager,
        DirectorRowMapper directorMapper,
        PeliculaRowMapper peliculaMapper
    ) {
        this.db = template.getDatabaseClient();
        this.r2dbcEntityTemplate = template;
        this.entityManager = entityManager;
        this.directorMapper = directorMapper;
        this.peliculaMapper = peliculaMapper;
    }

    @Override
    public Flux<Pelicula> findAllBy(Pageable pageable) {
        return findAllBy(pageable, null);
    }

    @Override
    public Flux<Pelicula> findAllBy(Pageable pageable, Criteria criteria) {
        return createQuery(pageable, criteria).all();
    }

    RowsFetchSpec<Pelicula> createQuery(Pageable pageable, Criteria criteria) {
        List<Expression> columns = PeliculaSqlHelper.getColumns(entityTable, EntityManager.ENTITY_ALIAS);
        columns.addAll(DirectorSqlHelper.getColumns(directorTable, "director"));
        SelectFromAndJoinCondition selectFrom = Select
            .builder()
            .select(columns)
            .from(entityTable)
            .leftOuterJoin(directorTable)
            .on(Column.create("director_id", entityTable))
            .equals(Column.create("id", directorTable));

        String select = entityManager.createSelect(selectFrom, Pelicula.class, pageable, criteria);
        String alias = entityTable.getReferenceName().getReference();
        String selectWhere = Optional
            .ofNullable(criteria)
            .map(crit ->
                new StringBuilder(select)
                    .append(" ")
                    .append("WHERE")
                    .append(" ")
                    .append(alias)
                    .append(".")
                    .append(crit.toString())
                    .toString()
            )
            .orElse(select); // TODO remove once https://github.com/spring-projects/spring-data-jdbc/issues/907 will be fixed
        return db.sql(selectWhere).map(this::process);
    }

    @Override
    public Flux<Pelicula> findAll() {
        return findAllBy(null, null);
    }

    @Override
    public Mono<Pelicula> findById(Long id) {
        return createQuery(null, where("id").is(id)).one();
    }

    private Pelicula process(Row row, RowMetadata metadata) {
        Pelicula entity = peliculaMapper.apply(row, "e");
        entity.setDirector(directorMapper.apply(row, "director"));
        return entity;
    }

    @Override
    public <S extends Pelicula> Mono<S> insert(S entity) {
        return entityManager.insert(entity);
    }

    @Override
    public <S extends Pelicula> Mono<S> save(S entity) {
        if (entity.getId() == null) {
            return insert(entity);
        } else {
            return update(entity)
                .map(numberOfUpdates -> {
                    if (numberOfUpdates.intValue() <= 0) {
                        throw new IllegalStateException("Unable to update Pelicula with id = " + entity.getId());
                    }
                    return entity;
                });
        }
    }

    @Override
    public Mono<Integer> update(Pelicula entity) {
        //fixme is this the proper way?
        return r2dbcEntityTemplate.update(entity).thenReturn(1);
    }
}

class PeliculaSqlHelper {

    static List<Expression> getColumns(Table table, String columnPrefix) {
        List<Expression> columns = new ArrayList<>();
        columns.add(Column.aliased("id", table, columnPrefix + "_id"));
        columns.add(Column.aliased("titulo", table, columnPrefix + "_titulo"));
        columns.add(Column.aliased("fecha_estreno", table, columnPrefix + "_fecha_estreno"));
        columns.add(Column.aliased("decripcion", table, columnPrefix + "_decripcion"));
        columns.add(Column.aliased("en_cines", table, columnPrefix + "_en_cines"));

        columns.add(Column.aliased("director_id", table, columnPrefix + "_director_id"));
        return columns;
    }
}
