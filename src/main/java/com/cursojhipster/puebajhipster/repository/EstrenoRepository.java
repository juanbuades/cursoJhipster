package com.cursojhipster.puebajhipster.repository;

import com.cursojhipster.puebajhipster.domain.Estreno;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data SQL reactive repository for the Estreno entity.
 */
@SuppressWarnings("unused")
@Repository
public interface EstrenoRepository extends R2dbcRepository<Estreno, Long>, EstrenoRepositoryInternal {
    Flux<Estreno> findAllBy(Pageable pageable);

    @Query("SELECT * FROM estreno entity WHERE entity.pelicula_id = :id")
    Flux<Estreno> findByPelicula(Long id);

    @Query("SELECT * FROM estreno entity WHERE entity.pelicula_id IS NULL")
    Flux<Estreno> findAllWherePeliculaIsNull();

    // just to avoid having unambigous methods
    @Override
    Flux<Estreno> findAll();

    @Override
    Mono<Estreno> findById(Long id);

    @Override
    <S extends Estreno> Mono<S> save(S entity);
}

interface EstrenoRepositoryInternal {
    <S extends Estreno> Mono<S> insert(S entity);
    <S extends Estreno> Mono<S> save(S entity);
    Mono<Integer> update(Estreno entity);

    Flux<Estreno> findAll();
    Mono<Estreno> findById(Long id);
    Flux<Estreno> findAllBy(Pageable pageable);
    Flux<Estreno> findAllBy(Pageable pageable, Criteria criteria);
}
