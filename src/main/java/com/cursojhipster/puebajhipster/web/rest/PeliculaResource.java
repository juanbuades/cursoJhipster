package com.cursojhipster.puebajhipster.web.rest;

import com.cursojhipster.puebajhipster.domain.Pelicula;
import com.cursojhipster.puebajhipster.repository.PeliculaRepository;
import com.cursojhipster.puebajhipster.service.PeliculaService;
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
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.reactive.ResponseUtil;

/**
 * REST controller for managing {@link com.cursojhipster.puebajhipster.domain.Pelicula}.
 */
@RestController
@RequestMapping("/api")
public class PeliculaResource {

    private final Logger log = LoggerFactory.getLogger(PeliculaResource.class);

    private static final String ENTITY_NAME = "pelicula";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final PeliculaService peliculaService;

    private final PeliculaRepository peliculaRepository;

    public PeliculaResource(PeliculaService peliculaService, PeliculaRepository peliculaRepository) {
        this.peliculaService = peliculaService;
        this.peliculaRepository = peliculaRepository;
    }

    /**
     * {@code POST  /peliculas} : Create a new pelicula.
     *
     * @param pelicula the pelicula to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new pelicula, or with status {@code 400 (Bad Request)} if the pelicula has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/peliculas")
    public Mono<ResponseEntity<Pelicula>> createPelicula(@Valid @RequestBody Pelicula pelicula) throws URISyntaxException {
        log.debug("REST request to save Pelicula : {}", pelicula);
        if (pelicula.getId() != null) {
            throw new BadRequestAlertException("A new pelicula cannot already have an ID", ENTITY_NAME, "idexists");
        }
        return peliculaService
            .save(pelicula)
            .map(result -> {
                try {
                    return ResponseEntity
                        .created(new URI("/api/peliculas/" + result.getId()))
                        .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
                        .body(result);
                } catch (URISyntaxException e) {
                    throw new RuntimeException(e);
                }
            });
    }

    /**
     * {@code PUT  /peliculas/:id} : Updates an existing pelicula.
     *
     * @param id the id of the pelicula to save.
     * @param pelicula the pelicula to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated pelicula,
     * or with status {@code 400 (Bad Request)} if the pelicula is not valid,
     * or with status {@code 500 (Internal Server Error)} if the pelicula couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/peliculas/{id}")
    public Mono<ResponseEntity<Pelicula>> updatePelicula(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody Pelicula pelicula
    ) throws URISyntaxException {
        log.debug("REST request to update Pelicula : {}, {}", id, pelicula);
        if (pelicula.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, pelicula.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return peliculaRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                return peliculaService
                    .save(pelicula)
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
     * {@code PATCH  /peliculas/:id} : Partial updates given fields of an existing pelicula, field will ignore if it is null
     *
     * @param id the id of the pelicula to save.
     * @param pelicula the pelicula to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated pelicula,
     * or with status {@code 400 (Bad Request)} if the pelicula is not valid,
     * or with status {@code 404 (Not Found)} if the pelicula is not found,
     * or with status {@code 500 (Internal Server Error)} if the pelicula couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/peliculas/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public Mono<ResponseEntity<Pelicula>> partialUpdatePelicula(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody Pelicula pelicula
    ) throws URISyntaxException {
        log.debug("REST request to partial update Pelicula partially : {}, {}", id, pelicula);
        if (pelicula.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, pelicula.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return peliculaRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                Mono<Pelicula> result = peliculaService.partialUpdate(pelicula);

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
     * {@code GET  /peliculas} : get all the peliculas.
     *
     * @param pageable the pagination information.
     * @param request a {@link ServerHttpRequest} request.
     * @param filter the filter of the request.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of peliculas in body.
     */
    @GetMapping("/peliculas")
    public Mono<ResponseEntity<List<Pelicula>>> getAllPeliculas(
        Pageable pageable,
        ServerHttpRequest request,
        @RequestParam(required = false) String filter
    ) {
        if ("estreno-is-null".equals(filter)) {
            log.debug("REST request to get all Peliculas where estreno is null");
            return peliculaService.findAllWhereEstrenoIsNull().collectList().map(ResponseEntity::ok);
        }
        log.debug("REST request to get a page of Peliculas");
        return peliculaService
            .countAll()
            .zipWith(peliculaService.findAll(pageable).collectList())
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
     * {@code GET  /peliculas/:id} : get the "id" pelicula.
     *
     * @param id the id of the pelicula to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the pelicula, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/peliculas/{id}")
    public Mono<ResponseEntity<Pelicula>> getPelicula(@PathVariable Long id) {
        log.debug("REST request to get Pelicula : {}", id);
        Mono<Pelicula> pelicula = peliculaService.findOne(id);
        return ResponseUtil.wrapOrNotFound(pelicula);
    }

    /**
     * {@code DELETE  /peliculas/:id} : delete the "id" pelicula.
     *
     * @param id the id of the pelicula to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/peliculas/{id}")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    public Mono<ResponseEntity<Void>> deletePelicula(@PathVariable Long id) {
        log.debug("REST request to delete Pelicula : {}", id);
        return peliculaService
            .delete(id)
            .map(result ->
                ResponseEntity
                    .noContent()
                    .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString()))
                    .build()
            );
    }
}
