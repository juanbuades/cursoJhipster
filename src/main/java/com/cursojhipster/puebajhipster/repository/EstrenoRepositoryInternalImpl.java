package com.cursojhipster.puebajhipster.repository;

import static org.springframework.data.relational.core.query.Criteria.where;
import static org.springframework.data.relational.core.query.Query.query;

import com.cursojhipster.puebajhipster.domain.Estreno;
import com.cursojhipster.puebajhipster.repository.rowmapper.EstrenoRowMapper;
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
 * Spring Data SQL reactive custom repository implementation for the Estreno entity.
 */
@SuppressWarnings("unused")
class EstrenoRepositoryInternalImpl implements EstrenoRepositoryInternal {

    private final DatabaseClient db;
    private final R2dbcEntityTemplate r2dbcEntityTemplate;
    private final EntityManager entityManager;

    private final PeliculaRowMapper peliculaMapper;
    private final EstrenoRowMapper estrenoMapper;

    private static final Table entityTable = Table.aliased("estreno", EntityManager.ENTITY_ALIAS);
    private static final Table peliculaTable = Table.aliased("pelicula", "pelicula");

    public EstrenoRepositoryInternalImpl(
        R2dbcEntityTemplate template,
        EntityManager entityManager,
        PeliculaRowMapper peliculaMapper,
        EstrenoRowMapper estrenoMapper
    ) {
        this.db = template.getDatabaseClient();
        this.r2dbcEntityTemplate = template;
        this.entityManager = entityManager;
        this.peliculaMapper = peliculaMapper;
        this.estrenoMapper = estrenoMapper;
    }

    @Override
    public Flux<Estreno> findAllBy(Pageable pageable) {
        return findAllBy(pageable, null);
    }

    @Override
    public Flux<Estreno> findAllBy(Pageable pageable, Criteria criteria) {
        return createQuery(pageable, criteria).all();
    }

    RowsFetchSpec<Estreno> createQuery(Pageable pageable, Criteria criteria) {
        List<Expression> columns = EstrenoSqlHelper.getColumns(entityTable, EntityManager.ENTITY_ALIAS);
        columns.addAll(PeliculaSqlHelper.getColumns(peliculaTable, "pelicula"));
        SelectFromAndJoinCondition selectFrom = Select
            .builder()
            .select(columns)
            .from(entityTable)
            .leftOuterJoin(peliculaTable)
            .on(Column.create("pelicula_id", entityTable))
            .equals(Column.create("id", peliculaTable));

        String select = entityManager.createSelect(selectFrom, Estreno.class, pageable, criteria);
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
    public Flux<Estreno> findAll() {
        return findAllBy(null, null);
    }

    @Override
    public Mono<Estreno> findById(Long id) {
        return createQuery(null, where("id").is(id)).one();
    }

    private Estreno process(Row row, RowMetadata metadata) {
        Estreno entity = estrenoMapper.apply(row, "e");
        entity.setPelicula(peliculaMapper.apply(row, "pelicula"));
        return entity;
    }

    @Override
    public <S extends Estreno> Mono<S> insert(S entity) {
        return entityManager.insert(entity);
    }

    @Override
    public <S extends Estreno> Mono<S> save(S entity) {
        if (entity.getId() == null) {
            return insert(entity);
        } else {
            return update(entity)
                .map(numberOfUpdates -> {
                    if (numberOfUpdates.intValue() <= 0) {
                        throw new IllegalStateException("Unable to update Estreno with id = " + entity.getId());
                    }
                    return entity;
                });
        }
    }

    @Override
    public Mono<Integer> update(Estreno entity) {
        //fixme is this the proper way?
        return r2dbcEntityTemplate.update(entity).thenReturn(1);
    }
}

class EstrenoSqlHelper {

    static List<Expression> getColumns(Table table, String columnPrefix) {
        List<Expression> columns = new ArrayList<>();
        columns.add(Column.aliased("id", table, columnPrefix + "_id"));
        columns.add(Column.aliased("fecha", table, columnPrefix + "_fecha"));
        columns.add(Column.aliased("lugar", table, columnPrefix + "_lugar"));

        columns.add(Column.aliased("pelicula_id", table, columnPrefix + "_pelicula_id"));
        return columns;
    }
}
