package com.cursojhipster.puebajhipster.repository;

import com.cursojhipster.puebajhipster.domain.Pelicula;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data SQL reactive repository for the Pelicula entity.
 */
@SuppressWarnings("unused")
@Repository
public interface PeliculaRepository extends R2dbcRepository<Pelicula, Long>, PeliculaRepositoryInternal {
    Flux<Pelicula> findAllBy(Pageable pageable);

    @Query("SELECT * FROM pelicula entity WHERE entity.id not in (select pelicula_id from estreno)")
    Flux<Pelicula> findAllWhereEstrenoIsNull();

    @Query("SELECT * FROM pelicula entity WHERE entity.director_id = :id")
    Flux<Pelicula> findByDirector(Long id);

    @Query("SELECT * FROM pelicula entity WHERE entity.director_id IS NULL")
    Flux<Pelicula> findAllWhereDirectorIsNull();

    // just to avoid having unambigous methods
    @Override
    Flux<Pelicula> findAll();

    @Override
    Mono<Pelicula> findById(Long id);

    @Override
    <S extends Pelicula> Mono<S> save(S entity);
}

interface PeliculaRepositoryInternal {
    <S extends Pelicula> Mono<S> insert(S entity);
    <S extends Pelicula> Mono<S> save(S entity);
    Mono<Integer> update(Pelicula entity);

    Flux<Pelicula> findAll();
    Mono<Pelicula> findById(Long id);
    Flux<Pelicula> findAllBy(Pageable pageable);
    Flux<Pelicula> findAllBy(Pageable pageable, Criteria criteria);
}
