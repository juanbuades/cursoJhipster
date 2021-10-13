package com.cursojhipster.puebajhipster.service;

import com.cursojhipster.puebajhipster.domain.Pelicula;
import com.cursojhipster.puebajhipster.repository.PeliculaRepository;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Implementation for managing {@link Pelicula}.
 */
@Service
@Transactional
public class PeliculaService {

    private final Logger log = LoggerFactory.getLogger(PeliculaService.class);

    private final PeliculaRepository peliculaRepository;

    public PeliculaService(PeliculaRepository peliculaRepository) {
        this.peliculaRepository = peliculaRepository;
    }

    /**
     * Save a pelicula.
     *
     * @param pelicula the entity to save.
     * @return the persisted entity.
     */
    public Mono<Pelicula> save(Pelicula pelicula) {
        log.debug("Request to save Pelicula : {}", pelicula);
        return peliculaRepository.save(pelicula);
    }

    /**
     * Partially update a pelicula.
     *
     * @param pelicula the entity to update partially.
     * @return the persisted entity.
     */
    public Mono<Pelicula> partialUpdate(Pelicula pelicula) {
        log.debug("Request to partially update Pelicula : {}", pelicula);

        return peliculaRepository
            .findById(pelicula.getId())
            .map(existingPelicula -> {
                if (pelicula.getTitulo() != null) {
                    existingPelicula.setTitulo(pelicula.getTitulo());
                }
                if (pelicula.getFechaEstreno() != null) {
                    existingPelicula.setFechaEstreno(pelicula.getFechaEstreno());
                }
                if (pelicula.getDecripcion() != null) {
                    existingPelicula.setDecripcion(pelicula.getDecripcion());
                }
                if (pelicula.getEnCines() != null) {
                    existingPelicula.setEnCines(pelicula.getEnCines());
                }

                return existingPelicula;
            })
            .flatMap(peliculaRepository::save);
    }

    /**
     * Get all the peliculas.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Flux<Pelicula> findAll(Pageable pageable) {
        log.debug("Request to get all Peliculas");
        return peliculaRepository.findAllBy(pageable);
    }

    /**
     *  Get all the peliculas where Estreno is {@code null}.
     *  @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Flux<Pelicula> findAllWhereEstrenoIsNull() {
        log.debug("Request to get all peliculas where Estreno is null");
        return peliculaRepository.findAllWhereEstrenoIsNull();
    }

    /**
     * Returns the number of peliculas available.
     * @return the number of entities in the database.
     *
     */
    public Mono<Long> countAll() {
        return peliculaRepository.count();
    }

    /**
     * Get one pelicula by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Mono<Pelicula> findOne(Long id) {
        log.debug("Request to get Pelicula : {}", id);
        return peliculaRepository.findById(id);
    }

    /**
     * Delete the pelicula by id.
     *
     * @param id the id of the entity.
     * @return a Mono to signal the deletion
     */
    public Mono<Void> delete(Long id) {
        log.debug("Request to delete Pelicula : {}", id);
        return peliculaRepository.deleteById(id);
    }
}
