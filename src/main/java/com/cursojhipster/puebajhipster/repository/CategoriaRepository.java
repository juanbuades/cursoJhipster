package com.cursojhipster.puebajhipster.repository;

import com.cursojhipster.puebajhipster.domain.Categoria;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data SQL reactive repository for the Categoria entity.
 */
@SuppressWarnings("unused")
@Repository
public interface CategoriaRepository extends R2dbcRepository<Categoria, Long>, CategoriaRepositoryInternal {
    // just to avoid having unambigous methods
    @Override
    Flux<Categoria> findAll();

    @Override
    Mono<Categoria> findById(Long id);

    @Override
    <S extends Categoria> Mono<S> save(S entity);
}

interface CategoriaRepositoryInternal {
    <S extends Categoria> Mono<S> insert(S entity);
    <S extends Categoria> Mono<S> save(S entity);
    Mono<Integer> update(Categoria entity);

    Flux<Categoria> findAll();
    Mono<Categoria> findById(Long id);
    Flux<Categoria> findAllBy(Pageable pageable);
    Flux<Categoria> findAllBy(Pageable pageable, Criteria criteria);
}
