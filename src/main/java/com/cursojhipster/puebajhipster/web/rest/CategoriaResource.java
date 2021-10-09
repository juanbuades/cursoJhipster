package com.cursojhipster.puebajhipster.web.rest;

import com.cursojhipster.puebajhipster.domain.Categoria;
import com.cursojhipster.puebajhipster.repository.CategoriaRepository;
import com.cursojhipster.puebajhipster.web.rest.errors.BadRequestAlertException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.reactive.ResponseUtil;

/**
 * REST controller for managing {@link com.cursojhipster.puebajhipster.domain.Categoria}.
 */
@RestController
@RequestMapping("/api")
@Transactional
public class CategoriaResource {

    private final Logger log = LoggerFactory.getLogger(CategoriaResource.class);

    private static final String ENTITY_NAME = "categoria";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final CategoriaRepository categoriaRepository;

    public CategoriaResource(CategoriaRepository categoriaRepository) {
        this.categoriaRepository = categoriaRepository;
    }

    /**
     * {@code POST  /categorias} : Create a new categoria.
     *
     * @param categoria the categoria to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new categoria, or with status {@code 400 (Bad Request)} if the categoria has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/categorias")
    public Mono<ResponseEntity<Categoria>> createCategoria(@Valid @RequestBody Categoria categoria) throws URISyntaxException {
        log.debug("REST request to save Categoria : {}", categoria);
        if (categoria.getId() != null) {
            throw new BadRequestAlertException("A new categoria cannot already have an ID", ENTITY_NAME, "idexists");
        }
        return categoriaRepository
            .save(categoria)
            .map(result -> {
                try {
                    return ResponseEntity
                        .created(new URI("/api/categorias/" + result.getId()))
                        .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
                        .body(result);
                } catch (URISyntaxException e) {
                    throw new RuntimeException(e);
                }
            });
    }

    /**
     * {@code PUT  /categorias/:id} : Updates an existing categoria.
     *
     * @param id the id of the categoria to save.
     * @param categoria the categoria to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated categoria,
     * or with status {@code 400 (Bad Request)} if the categoria is not valid,
     * or with status {@code 500 (Internal Server Error)} if the categoria couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/categorias/{id}")
    public Mono<ResponseEntity<Categoria>> updateCategoria(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody Categoria categoria
    ) throws URISyntaxException {
        log.debug("REST request to update Categoria : {}, {}", id, categoria);
        if (categoria.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, categoria.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return categoriaRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                return categoriaRepository
                    .save(categoria)
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
     * {@code PATCH  /categorias/:id} : Partial updates given fields of an existing categoria, field will ignore if it is null
     *
     * @param id the id of the categoria to save.
     * @param categoria the categoria to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated categoria,
     * or with status {@code 400 (Bad Request)} if the categoria is not valid,
     * or with status {@code 404 (Not Found)} if the categoria is not found,
     * or with status {@code 500 (Internal Server Error)} if the categoria couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/categorias/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public Mono<ResponseEntity<Categoria>> partialUpdateCategoria(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody Categoria categoria
    ) throws URISyntaxException {
        log.debug("REST request to partial update Categoria partially : {}, {}", id, categoria);
        if (categoria.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, categoria.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return categoriaRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                Mono<Categoria> result = categoriaRepository
                    .findById(categoria.getId())
                    .map(existingCategoria -> {
                        if (categoria.getNombre() != null) {
                            existingCategoria.setNombre(categoria.getNombre());
                        }
                        if (categoria.getImagen() != null) {
                            existingCategoria.setImagen(categoria.getImagen());
                        }
                        if (categoria.getImagenContentType() != null) {
                            existingCategoria.setImagenContentType(categoria.getImagenContentType());
                        }

                        return existingCategoria;
                    })
                    .flatMap(categoriaRepository::save);

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
     * {@code GET  /categorias} : get all the categorias.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of categorias in body.
     */
    @GetMapping("/categorias")
    public Mono<List<Categoria>> getAllCategorias() {
        log.debug("REST request to get all Categorias");
        return categoriaRepository.findAll().collectList();
    }

    /**
     * {@code GET  /categorias} : get all the categorias as a stream.
     * @return the {@link Flux} of categorias.
     */
    @GetMapping(value = "/categorias", produces = MediaType.APPLICATION_NDJSON_VALUE)
    public Flux<Categoria> getAllCategoriasAsStream() {
        log.debug("REST request to get all Categorias as a stream");
        return categoriaRepository.findAll();
    }

    /**
     * {@code GET  /categorias/:id} : get the "id" categoria.
     *
     * @param id the id of the categoria to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the categoria, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/categorias/{id}")
    public Mono<ResponseEntity<Categoria>> getCategoria(@PathVariable Long id) {
        log.debug("REST request to get Categoria : {}", id);
        Mono<Categoria> categoria = categoriaRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(categoria);
    }

    /**
     * {@code DELETE  /categorias/:id} : delete the "id" categoria.
     *
     * @param id the id of the categoria to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/categorias/{id}")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    public Mono<ResponseEntity<Void>> deleteCategoria(@PathVariable Long id) {
        log.debug("REST request to delete Categoria : {}", id);
        return categoriaRepository
            .deleteById(id)
            .map(result ->
                ResponseEntity
                    .noContent()
                    .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString()))
                    .build()
            );
    }
}
