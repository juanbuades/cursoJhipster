package com.cursojhipster.puebajhipster.web.rest;

import com.cursojhipster.puebajhipster.domain.Estreno;
import com.cursojhipster.puebajhipster.repository.EstrenoRepository;
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
 * REST controller for managing {@link com.cursojhipster.puebajhipster.domain.Estreno}.
 */
@RestController
@RequestMapping("/api")
@Transactional
public class EstrenoResource {

    private final Logger log = LoggerFactory.getLogger(EstrenoResource.class);

    private static final String ENTITY_NAME = "estreno";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final EstrenoRepository estrenoRepository;

    public EstrenoResource(EstrenoRepository estrenoRepository) {
        this.estrenoRepository = estrenoRepository;
    }

    /**
     * {@code POST  /estrenos} : Create a new estreno.
     *
     * @param estreno the estreno to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new estreno, or with status {@code 400 (Bad Request)} if the estreno has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/estrenos")
    public Mono<ResponseEntity<Estreno>> createEstreno(@Valid @RequestBody Estreno estreno) throws URISyntaxException {
        log.debug("REST request to save Estreno : {}", estreno);
        if (estreno.getId() != null) {
            throw new BadRequestAlertException("A new estreno cannot already have an ID", ENTITY_NAME, "idexists");
        }
        return estrenoRepository
            .save(estreno)
            .map(result -> {
                try {
                    return ResponseEntity
                        .created(new URI("/api/estrenos/" + result.getId()))
                        .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
                        .body(result);
                } catch (URISyntaxException e) {
                    throw new RuntimeException(e);
                }
            });
    }

    /**
     * {@code PUT  /estrenos/:id} : Updates an existing estreno.
     *
     * @param id the id of the estreno to save.
     * @param estreno the estreno to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated estreno,
     * or with status {@code 400 (Bad Request)} if the estreno is not valid,
     * or with status {@code 500 (Internal Server Error)} if the estreno couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/estrenos/{id}")
    public Mono<ResponseEntity<Estreno>> updateEstreno(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody Estreno estreno
    ) throws URISyntaxException {
        log.debug("REST request to update Estreno : {}, {}", id, estreno);
        if (estreno.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, estreno.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return estrenoRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                return estrenoRepository
                    .save(estreno)
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
     * {@code PATCH  /estrenos/:id} : Partial updates given fields of an existing estreno, field will ignore if it is null
     *
     * @param id the id of the estreno to save.
     * @param estreno the estreno to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated estreno,
     * or with status {@code 400 (Bad Request)} if the estreno is not valid,
     * or with status {@code 404 (Not Found)} if the estreno is not found,
     * or with status {@code 500 (Internal Server Error)} if the estreno couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/estrenos/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public Mono<ResponseEntity<Estreno>> partialUpdateEstreno(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody Estreno estreno
    ) throws URISyntaxException {
        log.debug("REST request to partial update Estreno partially : {}, {}", id, estreno);
        if (estreno.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, estreno.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return estrenoRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                Mono<Estreno> result = estrenoRepository
                    .findById(estreno.getId())
                    .map(existingEstreno -> {
                        if (estreno.getFecha() != null) {
                            existingEstreno.setFecha(estreno.getFecha());
                        }
                        if (estreno.getLugar() != null) {
                            existingEstreno.setLugar(estreno.getLugar());
                        }

                        return existingEstreno;
                    })
                    .flatMap(estrenoRepository::save);

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
     * {@code GET  /estrenos} : get all the estrenos.
     *
     * @param pageable the pagination information.
     * @param request a {@link ServerHttpRequest} request.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of estrenos in body.
     */
    @GetMapping("/estrenos")
    public Mono<ResponseEntity<List<Estreno>>> getAllEstrenos(Pageable pageable, ServerHttpRequest request) {
        log.debug("REST request to get a page of Estrenos");
        return estrenoRepository
            .count()
            .zipWith(estrenoRepository.findAllBy(pageable).collectList())
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
     * {@code GET  /estrenos/:id} : get the "id" estreno.
     *
     * @param id the id of the estreno to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the estreno, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/estrenos/{id}")
    public Mono<ResponseEntity<Estreno>> getEstreno(@PathVariable Long id) {
        log.debug("REST request to get Estreno : {}", id);
        Mono<Estreno> estreno = estrenoRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(estreno);
    }

    /**
     * {@code DELETE  /estrenos/:id} : delete the "id" estreno.
     *
     * @param id the id of the estreno to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/estrenos/{id}")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    public Mono<ResponseEntity<Void>> deleteEstreno(@PathVariable Long id) {
        log.debug("REST request to delete Estreno : {}", id);
        return estrenoRepository
            .deleteById(id)
            .map(result ->
                ResponseEntity
                    .noContent()
                    .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString()))
                    .build()
            );
    }
}
