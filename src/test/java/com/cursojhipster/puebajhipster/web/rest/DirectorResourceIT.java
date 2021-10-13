package com.cursojhipster.puebajhipster.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;

import com.cursojhipster.puebajhipster.IntegrationTest;
import com.cursojhipster.puebajhipster.domain.Director;
import com.cursojhipster.puebajhipster.repository.DirectorRepository;
import com.cursojhipster.puebajhipster.service.EntityManager;
import java.time.Duration;
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
 * Integration tests for the {@link DirectorResource} REST controller.
 */
@IntegrationTest
@AutoConfigureWebTestClient
@WithMockUser
class DirectorResourceIT {

    private static final String DEFAULT_NOMBRE = "AAAAAAAAAA";
    private static final String UPDATED_NOMBRE = "BBBBBBBBBB";

    private static final String DEFAULT_APELLIDOS = "AAAAAAAAAA";
    private static final String UPDATED_APELLIDOS = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/directors";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private DirectorRepository directorRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private WebTestClient webTestClient;

    private Director director;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Director createEntity(EntityManager em) {
        Director director = new Director().nombre(DEFAULT_NOMBRE).apellidos(DEFAULT_APELLIDOS);
        return director;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Director createUpdatedEntity(EntityManager em) {
        Director director = new Director().nombre(UPDATED_NOMBRE).apellidos(UPDATED_APELLIDOS);
        return director;
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll(Director.class).block();
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
        director = createEntity(em);
    }

    @Test
    void createDirector() throws Exception {
        int databaseSizeBeforeCreate = directorRepository.findAll().collectList().block().size();
        // Create the Director
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(director))
            .exchange()
            .expectStatus()
            .isCreated();

        // Validate the Director in the database
        List<Director> directorList = directorRepository.findAll().collectList().block();
        assertThat(directorList).hasSize(databaseSizeBeforeCreate + 1);
        Director testDirector = directorList.get(directorList.size() - 1);
        assertThat(testDirector.getNombre()).isEqualTo(DEFAULT_NOMBRE);
        assertThat(testDirector.getApellidos()).isEqualTo(DEFAULT_APELLIDOS);
    }

    @Test
    void createDirectorWithExistingId() throws Exception {
        // Create the Director with an existing ID
        director.setId(1L);

        int databaseSizeBeforeCreate = directorRepository.findAll().collectList().block().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(director))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Director in the database
        List<Director> directorList = directorRepository.findAll().collectList().block();
        assertThat(directorList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    void getAllDirectors() {
        // Initialize the database
        directorRepository.save(director).block();

        // Get all the directorList
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
            .value(hasItem(director.getId().intValue()))
            .jsonPath("$.[*].nombre")
            .value(hasItem(DEFAULT_NOMBRE))
            .jsonPath("$.[*].apellidos")
            .value(hasItem(DEFAULT_APELLIDOS));
    }

    @Test
    void getDirector() {
        // Initialize the database
        directorRepository.save(director).block();

        // Get the director
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, director.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(director.getId().intValue()))
            .jsonPath("$.nombre")
            .value(is(DEFAULT_NOMBRE))
            .jsonPath("$.apellidos")
            .value(is(DEFAULT_APELLIDOS));
    }

    @Test
    void getNonExistingDirector() {
        // Get the director
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putNewDirector() throws Exception {
        // Initialize the database
        directorRepository.save(director).block();

        int databaseSizeBeforeUpdate = directorRepository.findAll().collectList().block().size();

        // Update the director
        Director updatedDirector = directorRepository.findById(director.getId()).block();
        updatedDirector.nombre(UPDATED_NOMBRE).apellidos(UPDATED_APELLIDOS);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, updatedDirector.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(updatedDirector))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Director in the database
        List<Director> directorList = directorRepository.findAll().collectList().block();
        assertThat(directorList).hasSize(databaseSizeBeforeUpdate);
        Director testDirector = directorList.get(directorList.size() - 1);
        assertThat(testDirector.getNombre()).isEqualTo(UPDATED_NOMBRE);
        assertThat(testDirector.getApellidos()).isEqualTo(UPDATED_APELLIDOS);
    }

    @Test
    void putNonExistingDirector() throws Exception {
        int databaseSizeBeforeUpdate = directorRepository.findAll().collectList().block().size();
        director.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, director.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(director))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Director in the database
        List<Director> directorList = directorRepository.findAll().collectList().block();
        assertThat(directorList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchDirector() throws Exception {
        int databaseSizeBeforeUpdate = directorRepository.findAll().collectList().block().size();
        director.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(director))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Director in the database
        List<Director> directorList = directorRepository.findAll().collectList().block();
        assertThat(directorList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamDirector() throws Exception {
        int databaseSizeBeforeUpdate = directorRepository.findAll().collectList().block().size();
        director.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(director))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Director in the database
        List<Director> directorList = directorRepository.findAll().collectList().block();
        assertThat(directorList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdateDirectorWithPatch() throws Exception {
        // Initialize the database
        directorRepository.save(director).block();

        int databaseSizeBeforeUpdate = directorRepository.findAll().collectList().block().size();

        // Update the director using partial update
        Director partialUpdatedDirector = new Director();
        partialUpdatedDirector.setId(director.getId());

        partialUpdatedDirector.nombre(UPDATED_NOMBRE).apellidos(UPDATED_APELLIDOS);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedDirector.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedDirector))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Director in the database
        List<Director> directorList = directorRepository.findAll().collectList().block();
        assertThat(directorList).hasSize(databaseSizeBeforeUpdate);
        Director testDirector = directorList.get(directorList.size() - 1);
        assertThat(testDirector.getNombre()).isEqualTo(UPDATED_NOMBRE);
        assertThat(testDirector.getApellidos()).isEqualTo(UPDATED_APELLIDOS);
    }

    @Test
    void fullUpdateDirectorWithPatch() throws Exception {
        // Initialize the database
        directorRepository.save(director).block();

        int databaseSizeBeforeUpdate = directorRepository.findAll().collectList().block().size();

        // Update the director using partial update
        Director partialUpdatedDirector = new Director();
        partialUpdatedDirector.setId(director.getId());

        partialUpdatedDirector.nombre(UPDATED_NOMBRE).apellidos(UPDATED_APELLIDOS);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedDirector.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedDirector))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Director in the database
        List<Director> directorList = directorRepository.findAll().collectList().block();
        assertThat(directorList).hasSize(databaseSizeBeforeUpdate);
        Director testDirector = directorList.get(directorList.size() - 1);
        assertThat(testDirector.getNombre()).isEqualTo(UPDATED_NOMBRE);
        assertThat(testDirector.getApellidos()).isEqualTo(UPDATED_APELLIDOS);
    }

    @Test
    void patchNonExistingDirector() throws Exception {
        int databaseSizeBeforeUpdate = directorRepository.findAll().collectList().block().size();
        director.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, director.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(director))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Director in the database
        List<Director> directorList = directorRepository.findAll().collectList().block();
        assertThat(directorList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchDirector() throws Exception {
        int databaseSizeBeforeUpdate = directorRepository.findAll().collectList().block().size();
        director.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(director))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Director in the database
        List<Director> directorList = directorRepository.findAll().collectList().block();
        assertThat(directorList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamDirector() throws Exception {
        int databaseSizeBeforeUpdate = directorRepository.findAll().collectList().block().size();
        director.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(director))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Director in the database
        List<Director> directorList = directorRepository.findAll().collectList().block();
        assertThat(directorList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void deleteDirector() {
        // Initialize the database
        directorRepository.save(director).block();

        int databaseSizeBeforeDelete = directorRepository.findAll().collectList().block().size();

        // Delete the director
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, director.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        List<Director> directorList = directorRepository.findAll().collectList().block();
        assertThat(directorList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
