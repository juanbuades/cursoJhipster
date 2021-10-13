package com.cursojhipster.puebajhipster.repository;

import com.cursojhipster.puebajhipster.domain.Director;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data SQL reactive repository for the Director entity.
 */
@SuppressWarnings("unused")
@Repository
public interface DirectorRepository extends R2dbcRepository<Director, Long>, DirectorRepositoryInternal {
    Flux<Director> findAllBy(Pageable pageable);

    // just to avoid having unambigous methods
    @Override
    Flux<Director> findAll();

    @Override
    Mono<Director> findById(Long id);

    @Override
    <S extends Director> Mono<S> save(S entity);
}

interface DirectorRepositoryInternal {
    <S extends Director> Mono<S> insert(S entity);
    <S extends Director> Mono<S> save(S entity);
    Mono<Integer> update(Director entity);

    Flux<Director> findAll();
    Mono<Director> findById(Long id);
    Flux<Director> findAllBy(Pageable pageable);
    Flux<Director> findAllBy(Pageable pageable, Criteria criteria);
}
