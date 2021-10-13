package com.cursojhipster.puebajhipster.web.rest;

import com.cursojhipster.puebajhipster.domain.Director;
import com.cursojhipster.puebajhipster.repository.DirectorRepository;
import com.cursojhipster.puebajhipster.web.rest.errors.BadRequestAlertException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.reactive.ResponseUtil;

/**
 * REST controller for managing {@link com.cursojhipster.puebajhipster.domain.Director}.
 */
@RestController
@RequestMapping("/api")
@Transactional
public class DirectorResource {

    private final Logger log = LoggerFactory.getLogger(DirectorResource.class);

    private static final String ENTITY_NAME = "director";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final DirectorRepository directorRepository;

    public DirectorResource(DirectorRepository directorRepository) {
        this.directorRepository = directorRepository;
    }

    /**
     * {@code POST  /directors} : Create a new director.
     *
     * @param director the director to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new director, or with status {@code 400 (Bad Request)} if the director has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/directors")
    public Mono<ResponseEntity<Director>> createDirector(@Valid @RequestBody Director director) throws URISyntaxException {
        log.debug("REST request to save Director : {}", director);
        if (director.getId() != null) {
            throw new BadRequestAlertException("A new director cannot already have an ID", ENTITY_NAME, "idexists");
        }
        return directorRepository
            .save(director)
            .map(result -> {
                try {
                    return ResponseEntity
                        .created(new URI("/api/directors/" + result.getId()))
                        .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
                        .body(result);
                } catch (URISyntaxException e) {
                    throw new RuntimeException(e);
                }
            });
    }

    /**
     * {@code PUT  /directors/:id} : Updates an existing director.
     *
     * @param id the id of the director to save.
     * @param director the director to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated director,
     * or with status {@code 400 (Bad Request)} if the director is not valid,
     * or with status {@code 500 (Internal Server Error)} if the director couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/directors/{id}")
    public Mono<ResponseEntity<Director>> updateDirector(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody Director director
    ) throws URISyntaxException {
        log.debug("REST request to update Director : {}, {}", id, director);
        if (director.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, director.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return directorRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                return directorRepository
                    .save(director)
                    .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)))
                    .map(result ->
                        ResponseEntity
                            .ok()
                            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
                            .body(result)
                    );
            });
    }

    /**
     * {@code PATCH  /directors/:id} : Partial updates given fields of an existing director, field will ignore if it is null
     *
     * @param id the id of the director to save.
     * @param director the director to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated director,
     * or with status {@code 400 (Bad Request)} if the director is not valid,
     * or with status {@code 404 (Not Found)} if the director is not found,
     * or with status {@code 500 (Internal Server Error)} if the director couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/directors/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public Mono<ResponseEntity<Director>> partialUpdateDirector(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody Director director
    ) throws URISyntaxException {
        log.debug("REST request to partial update Director partially : {}, {}", id, director);
        if (director.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, director.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return directorRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                Mono<Director> result = directorRepository
                    .findById(director.getId())
                    .map(existingDirector -> {
                        if (director.getNombre() != null) {
                            existingDirector.setNombre(director.getNombre());
                        }
                        if (director.getApellidos() != null) {
                            existingDirector.setApellidos(director.getApellidos());
                        }

                        return existingDirector;
                    })
                    .flatMap(directorRepository::save);

                return result
                    .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)))
                    .map(res ->
                        ResponseEntity
                            .ok()
                            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, res.getId().toString()))
                            .body(res)
                    );
            });
    }

    /**
     * {@code GET  /directors} : get all the directors.
     *
     * @param pageable the pagination information.
     * @param request a {@link ServerHttpRequest} request.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of directors in body.
     */
    @GetMapping("/directors")
    public Mono<ResponseEntity<List<Director>>> getAllDirectors(Pageable pageable, ServerHttpRequest request) {
        log.debug("REST request to get a page of Directors");
        return directorRepository
            .count()
            .zipWith(directorRepository.findAllBy(pageable).collectList())
            .map(countWithEntities -> {
                return ResponseEntity
                    .ok()
                    .headers(
                        PaginationUtil.generatePaginationHttpHeaders(
                            UriComponentsBuilder.fromHttpRequest(request),
                            new PageImpl<>(countWithEntities.getT2(), pageable, countWithEntities.getT1())
                        )
                    )
                    .body(countWithEntities.getT2());
            });
    }

    /**
     * {@code GET  /directors/:id} : get the "id" director.
     *
     * @param id the id of the director to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the director, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/directors/{id}")
    public Mono<ResponseEntity<Director>> getDirector(@PathVariable Long id) {
        log.debug("REST request to get Director : {}", id);
        Mono<Director> director = directorRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(director);
    }

    /**
     * {@code DELETE  /directors/:id} : delete the "id" director.
     *
     * @param id the id of the director to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/directors/{id}")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    public Mono<ResponseEntity<Void>> deleteDirector(@PathVariable Long id) {
        log.debug("REST request to delete Director : {}", id);
        return directorRepository
            .deleteById(id)
            .map(result ->
                ResponseEntity
                    .noContent()
                    .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString()))
                    .build()
            );
    }
}
