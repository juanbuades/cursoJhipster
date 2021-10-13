package com.cursojhipster.puebajhipster.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;

import com.cursojhipster.puebajhipster.IntegrationTest;
import com.cursojhipster.puebajhipster.domain.Pelicula;
import com.cursojhipster.puebajhipster.repository.PeliculaRepository;
import com.cursojhipster.puebajhipster.service.EntityManager;
import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.reactive.server.WebTestClient;

/**
 * Integration tests for the {@link PeliculaResource} REST controller.
 */
@IntegrationTest
@AutoConfigureWebTestClient
@WithMockUser
class PeliculaResourceIT {

    private static final String DEFAULT_TITULO = "AAAAAAAAAA";
    private static final String UPDATED_TITULO = "BBBBBBBBBB";

    private static final Instant DEFAULT_FECHA_ESTRENO = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_FECHA_ESTRENO = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String DEFAULT_DECRIPCION = "AAAAAAAAAAAAAAAAAAAA";
    private static final String UPDATED_DECRIPCION = "BBBBBBBBBBBBBBBBBBBB";

    private static final Boolean DEFAULT_EN_CINES = false;
    private static final Boolean UPDATED_EN_CINES = true;

    private static final String ENTITY_API_URL = "/api/peliculas";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private PeliculaRepository peliculaRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private WebTestClient webTestClient;

    private Pelicula pelicula;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Pelicula createEntity(EntityManager em) {
        Pelicula pelicula = new Pelicula()
            .titulo(DEFAULT_TITULO)
            .fechaEstreno(DEFAULT_FECHA_ESTRENO)
            .decripcion(DEFAULT_DECRIPCION)
            .enCines(DEFAULT_EN_CINES);
        return pelicula;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Pelicula createUpdatedEntity(EntityManager em) {
        Pelicula pelicula = new Pelicula()
            .titulo(UPDATED_TITULO)
            .fechaEstreno(UPDATED_FECHA_ESTRENO)
            .decripcion(UPDATED_DECRIPCION)
            .enCines(UPDATED_EN_CINES);
        return pelicula;
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll(Pelicula.class).block();
        } catch (Exception e) {
            // It can fail, if other entities are still referring this - it will be removed later.
        }
    }

    @AfterEach
    public void cleanup() {
        deleteEntities(em);
    }

    @BeforeEach
    public void initTest() {
        deleteEntities(em);
        pelicula = createEntity(em);
    }

    @Test
    void createPelicula() throws Exception {
        int databaseSizeBeforeCreate = peliculaRepository.findAll().collectList().block().size();
        // Create the Pelicula
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(pelicula))
            .exchange()
            .expectStatus()
            .isCreated();

        // Validate the Pelicula in the database
        List<Pelicula> peliculaList = peliculaRepository.findAll().collectList().block();
        assertThat(peliculaList).hasSize(databaseSizeBeforeCreate + 1);
        Pelicula testPelicula = peliculaList.get(peliculaList.size() - 1);
        assertThat(testPelicula.getTitulo()).isEqualTo(DEFAULT_TITULO);
        assertThat(testPelicula.getFechaEstreno()).isEqualTo(DEFAULT_FECHA_ESTRENO);
        assertThat(testPelicula.getDecripcion()).isEqualTo(DEFAULT_DECRIPCION);
        assertThat(testPelicula.getEnCines()).isEqualTo(DEFAULT_EN_CINES);
    }

    @Test
    void createPeliculaWithExistingId() throws Exception {
        // Create the Pelicula with an existing ID
        pelicula.setId(1L);

        int databaseSizeBeforeCreate = peliculaRepository.findAll().collectList().block().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(pelicula))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Pelicula in the database
        List<Pelicula> peliculaList = peliculaRepository.findAll().collectList().block();
        assertThat(peliculaList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    void checkTituloIsRequired() throws Exception {
        int databaseSizeBeforeTest = peliculaRepository.findAll().collectList().block().size();
        // set the field null
        pelicula.setTitulo(null);

        // Create the Pelicula, which fails.

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(pelicula))
            .exchange()
            .expectStatus()
            .isBadRequest();

        List<Pelicula> peliculaList = peliculaRepository.findAll().collectList().block();
        assertThat(peliculaList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    void getAllPeliculas() {
        // Initialize the database
        peliculaRepository.save(pelicula).block();

        // Get all the peliculaList
        webTestClient
            .get()
            .uri(ENTITY_API_URL + "?sort=id,desc")
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.[*].id")
            .value(hasItem(pelicula.getId().intValue()))
            .jsonPath("$.[*].titulo")
            .value(hasItem(DEFAULT_TITULO))
            .jsonPath("$.[*].fechaEstreno")
            .value(hasItem(DEFAULT_FECHA_ESTRENO.toString()))
            .jsonPath("$.[*].decripcion")
            .value(hasItem(DEFAULT_DECRIPCION))
            .jsonPath("$.[*].enCines")
            .value(hasItem(DEFAULT_EN_CINES.booleanValue()));
    }

    @Test
    void getPelicula() {
        // Initialize the database
        peliculaRepository.save(pelicula).block();

        // Get the pelicula
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, pelicula.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(pelicula.getId().intValue()))
            .jsonPath("$.titulo")
            .value(is(DEFAULT_TITULO))
            .jsonPath("$.fechaEstreno")
            .value(is(DEFAULT_FECHA_ESTRENO.toString()))
            .jsonPath("$.decripcion")
            .value(is(DEFAULT_DECRIPCION))
            .jsonPath("$.enCines")
            .value(is(DEFAULT_EN_CINES.booleanValue()));
    }

    @Test
    void getNonExistingPelicula() {
        // Get the pelicula
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putNewPelicula() throws Exception {
        // Initialize the database
        peliculaRepository.save(pelicula).block();

        int databaseSizeBeforeUpdate = peliculaRepository.findAll().collectList().block().size();

        // Update the pelicula
        Pelicula updatedPelicula = peliculaRepository.findById(pelicula.getId()).block();
        updatedPelicula.titulo(UPDATED_TITULO).fechaEstreno(UPDATED_FECHA_ESTRENO).decripcion(UPDATED_DECRIPCION).enCines(UPDATED_EN_CINES);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, updatedPelicula.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(updatedPelicula))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Pelicula in the database
        List<Pelicula> peliculaList = peliculaRepository.findAll().collectList().block();
        assertThat(peliculaList).hasSize(databaseSizeBeforeUpdate);
        Pelicula testPelicula = peliculaList.get(peliculaList.size() - 1);
        assertThat(testPelicula.getTitulo()).isEqualTo(UPDATED_TITULO);
        assertThat(testPelicula.getFechaEstreno()).isEqualTo(UPDATED_FECHA_ESTRENO);
        assertThat(testPelicula.getDecripcion()).isEqualTo(UPDATED_DECRIPCION);
        assertThat(testPelicula.getEnCines()).isEqualTo(UPDATED_EN_CINES);
    }

    @Test
    void putNonExistingPelicula() throws Exception {
        int databaseSizeBeforeUpdate = peliculaRepository.findAll().collectList().block().size();
        pelicula.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, pelicula.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(pelicula))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Pelicula in the database
        List<Pelicula> peliculaList = peliculaRepository.findAll().collectList().block();
        assertThat(peliculaList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchPelicula() throws Exception {
        int databaseSizeBeforeUpdate = peliculaRepository.findAll().collectList().block().size();
        pelicula.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(pelicula))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Pelicula in the database
        List<Pelicula> peliculaList = peliculaRepository.findAll().collectList().block();
        assertThat(peliculaList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamPelicula() throws Exception {
        int databaseSizeBeforeUpdate = peliculaRepository.findAll().collectList().block().size();
        pelicula.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(pelicula))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Pelicula in the database
        List<Pelicula> peliculaList = peliculaRepository.findAll().collectList().block();
        assertThat(peliculaList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdatePeliculaWithPatch() throws Exception {
        // Initialize the database
        peliculaRepository.save(pelicula).block();

        int databaseSizeBeforeUpdate = peliculaRepository.findAll().collectList().block().size();

        // Update the pelicula using partial update
        Pelicula partialUpdatedPelicula = new Pelicula();
        partialUpdatedPelicula.setId(pelicula.getId());

        partialUpdatedPelicula.enCines(UPDATED_EN_CINES);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedPelicula.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedPelicula))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Pelicula in the database
        List<Pelicula> peliculaList = peliculaRepository.findAll().collectList().block();
        assertThat(peliculaList).hasSize(databaseSizeBeforeUpdate);
        Pelicula testPelicula = peliculaList.get(peliculaList.size() - 1);
        assertThat(testPelicula.getTitulo()).isEqualTo(DEFAULT_TITULO);
        assertThat(testPelicula.getFechaEstreno()).isEqualTo(DEFAULT_FECHA_ESTRENO);
        assertThat(testPelicula.getDecripcion()).isEqualTo(DEFAULT_DECRIPCION);
        assertThat(testPelicula.getEnCines()).isEqualTo(UPDATED_EN_CINES);
    }

    @Test
    void fullUpdatePeliculaWithPatch() throws Exception {
        // Initialize the database
        peliculaRepository.save(pelicula).block();

        int databaseSizeBeforeUpdate = peliculaRepository.findAll().collectList().block().size();

        // Update the pelicula using partial update
        Pelicula partialUpdatedPelicula = new Pelicula();
        partialUpdatedPelicula.setId(pelicula.getId());

        partialUpdatedPelicula
            .titulo(UPDATED_TITULO)
            .fechaEstreno(UPDATED_FECHA_ESTRENO)
            .decripcion(UPDATED_DECRIPCION)
            .enCines(UPDATED_EN_CINES);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedPelicula.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedPelicula))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Pelicula in the database
        List<Pelicula> peliculaList = peliculaRepository.findAll().collectList().block();
        assertThat(peliculaList).hasSize(databaseSizeBeforeUpdate);
        Pelicula testPelicula = peliculaList.get(peliculaList.size() - 1);
        assertThat(testPelicula.getTitulo()).isEqualTo(UPDATED_TITULO);
        assertThat(testPelicula.getFechaEstreno()).isEqualTo(UPDATED_FECHA_ESTRENO);
        assertThat(testPelicula.getDecripcion()).isEqualTo(UPDATED_DECRIPCION);
        assertThat(testPelicula.getEnCines()).isEqualTo(UPDATED_EN_CINES);
    }

    @Test
    void patchNonExistingPelicula() throws Exception {
        int databaseSizeBeforeUpdate = peliculaRepository.findAll().collectList().block().size();
        pelicula.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, pelicula.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(pelicula))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Pelicula in the database
        List<Pelicula> peliculaList = peliculaRepository.findAll().collectList().block();
        assertThat(peliculaList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchPelicula() throws Exception {
        int databaseSizeBeforeUpdate = peliculaRepository.findAll().collectList().block().size();
        pelicula.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(pelicula))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Pelicula in the database
        List<Pelicula> peliculaList = peliculaRepository.findAll().collectList().block();
        assertThat(peliculaList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamPelicula() throws Exception {
        int databaseSizeBeforeUpdate = peliculaRepository.findAll().collectList().block().size();
        pelicula.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(pelicula))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Pelicula in the database
        List<Pelicula> peliculaList = peliculaRepository.findAll().collectList().block();
        assertThat(peliculaList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void deletePelicula() {
        // Initialize the database
        peliculaRepository.save(pelicula).block();

        int databaseSizeBeforeDelete = peliculaRepository.findAll().collectList().block().size();

        // Delete the pelicula
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, pelicula.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        List<Pelicula> peliculaList = peliculaRepository.findAll().collectList().block();
        assertThat(peliculaList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
