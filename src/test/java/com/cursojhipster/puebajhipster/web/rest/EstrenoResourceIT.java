package com.cursojhipster.puebajhipster.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;

import com.cursojhipster.puebajhipster.IntegrationTest;
import com.cursojhipster.puebajhipster.domain.Estreno;
import com.cursojhipster.puebajhipster.repository.EstrenoRepository;
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
 * Integration tests for the {@link EstrenoResource} REST controller.
 */
@IntegrationTest
@AutoConfigureWebTestClient
@WithMockUser
class EstrenoResourceIT {

    private static final Instant DEFAULT_FECHA = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_FECHA = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String DEFAULT_LUGAR = "AAAAAAAAAA";
    private static final String UPDATED_LUGAR = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/estrenos";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private EstrenoRepository estrenoRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private WebTestClient webTestClient;

    private Estreno estreno;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Estreno createEntity(EntityManager em) {
        Estreno estreno = new Estreno().fecha(DEFAULT_FECHA).lugar(DEFAULT_LUGAR);
        return estreno;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Estreno createUpdatedEntity(EntityManager em) {
        Estreno estreno = new Estreno().fecha(UPDATED_FECHA).lugar(UPDATED_LUGAR);
        return estreno;
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll(Estreno.class).block();
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
        estreno = createEntity(em);
    }

    @Test
    void createEstreno() throws Exception {
        int databaseSizeBeforeCreate = estrenoRepository.findAll().collectList().block().size();
        // Create the Estreno
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(estreno))
            .exchange()
            .expectStatus()
            .isCreated();

        // Validate the Estreno in the database
        List<Estreno> estrenoList = estrenoRepository.findAll().collectList().block();
        assertThat(estrenoList).hasSize(databaseSizeBeforeCreate + 1);
        Estreno testEstreno = estrenoList.get(estrenoList.size() - 1);
        assertThat(testEstreno.getFecha()).isEqualTo(DEFAULT_FECHA);
        assertThat(testEstreno.getLugar()).isEqualTo(DEFAULT_LUGAR);
    }

    @Test
    void createEstrenoWithExistingId() throws Exception {
        // Create the Estreno with an existing ID
        estreno.setId(1L);

        int databaseSizeBeforeCreate = estrenoRepository.findAll().collectList().block().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(estreno))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Estreno in the database
        List<Estreno> estrenoList = estrenoRepository.findAll().collectList().block();
        assertThat(estrenoList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    void getAllEstrenos() {
        // Initialize the database
        estrenoRepository.save(estreno).block();

        // Get all the estrenoList
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
            .value(hasItem(estreno.getId().intValue()))
            .jsonPath("$.[*].fecha")
            .value(hasItem(DEFAULT_FECHA.toString()))
            .jsonPath("$.[*].lugar")
            .value(hasItem(DEFAULT_LUGAR));
    }

    @Test
    void getEstreno() {
        // Initialize the database
        estrenoRepository.save(estreno).block();

        // Get the estreno
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, estreno.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(estreno.getId().intValue()))
            .jsonPath("$.fecha")
            .value(is(DEFAULT_FECHA.toString()))
            .jsonPath("$.lugar")
            .value(is(DEFAULT_LUGAR));
    }

    @Test
    void getNonExistingEstreno() {
        // Get the estreno
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putNewEstreno() throws Exception {
        // Initialize the database
        estrenoRepository.save(estreno).block();

        int databaseSizeBeforeUpdate = estrenoRepository.findAll().collectList().block().size();

        // Update the estreno
        Estreno updatedEstreno = estrenoRepository.findById(estreno.getId()).block();
        updatedEstreno.fecha(UPDATED_FECHA).lugar(UPDATED_LUGAR);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, updatedEstreno.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(updatedEstreno))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Estreno in the database
        List<Estreno> estrenoList = estrenoRepository.findAll().collectList().block();
        assertThat(estrenoList).hasSize(databaseSizeBeforeUpdate);
        Estreno testEstreno = estrenoList.get(estrenoList.size() - 1);
        assertThat(testEstreno.getFecha()).isEqualTo(UPDATED_FECHA);
        assertThat(testEstreno.getLugar()).isEqualTo(UPDATED_LUGAR);
    }

    @Test
    void putNonExistingEstreno() throws Exception {
        int databaseSizeBeforeUpdate = estrenoRepository.findAll().collectList().block().size();
        estreno.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, estreno.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(estreno))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Estreno in the database
        List<Estreno> estrenoList = estrenoRepository.findAll().collectList().block();
        assertThat(estrenoList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchEstreno() throws Exception {
        int databaseSizeBeforeUpdate = estrenoRepository.findAll().collectList().block().size();
        estreno.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(estreno))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Estreno in the database
        List<Estreno> estrenoList = estrenoRepository.findAll().collectList().block();
        assertThat(estrenoList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamEstreno() throws Exception {
        int databaseSizeBeforeUpdate = estrenoRepository.findAll().collectList().block().size();
        estreno.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(estreno))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Estreno in the database
        List<Estreno> estrenoList = estrenoRepository.findAll().collectList().block();
        assertThat(estrenoList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdateEstrenoWithPatch() throws Exception {
        // Initialize the database
        estrenoRepository.save(estreno).block();

        int databaseSizeBeforeUpdate = estrenoRepository.findAll().collectList().block().size();

        // Update the estreno using partial update
        Estreno partialUpdatedEstreno = new Estreno();
        partialUpdatedEstreno.setId(estreno.getId());

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedEstreno.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedEstreno))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Estreno in the database
        List<Estreno> estrenoList = estrenoRepository.findAll().collectList().block();
        assertThat(estrenoList).hasSize(databaseSizeBeforeUpdate);
        Estreno testEstreno = estrenoList.get(estrenoList.size() - 1);
        assertThat(testEstreno.getFecha()).isEqualTo(DEFAULT_FECHA);
        assertThat(testEstreno.getLugar()).isEqualTo(DEFAULT_LUGAR);
    }

    @Test
    void fullUpdateEstrenoWithPatch() throws Exception {
        // Initialize the database
        estrenoRepository.save(estreno).block();

        int databaseSizeBeforeUpdate = estrenoRepository.findAll().collectList().block().size();

        // Update the estreno using partial update
        Estreno partialUpdatedEstreno = new Estreno();
        partialUpdatedEstreno.setId(estreno.getId());

        partialUpdatedEstreno.fecha(UPDATED_FECHA).lugar(UPDATED_LUGAR);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedEstreno.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedEstreno))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Estreno in the database
        List<Estreno> estrenoList = estrenoRepository.findAll().collectList().block();
        assertThat(estrenoList).hasSize(databaseSizeBeforeUpdate);
        Estreno testEstreno = estrenoList.get(estrenoList.size() - 1);
        assertThat(testEstreno.getFecha()).isEqualTo(UPDATED_FECHA);
        assertThat(testEstreno.getLugar()).isEqualTo(UPDATED_LUGAR);
    }

    @Test
    void patchNonExistingEstreno() throws Exception {
        int databaseSizeBeforeUpdate = estrenoRepository.findAll().collectList().block().size();
        estreno.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, estreno.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(estreno))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Estreno in the database
        List<Estreno> estrenoList = estrenoRepository.findAll().collectList().block();
        assertThat(estrenoList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchEstreno() throws Exception {
        int databaseSizeBeforeUpdate = estrenoRepository.findAll().collectList().block().size();
        estreno.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(estreno))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Estreno in the database
        List<Estreno> estrenoList = estrenoRepository.findAll().collectList().block();
        assertThat(estrenoList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamEstreno() throws Exception {
        int databaseSizeBeforeUpdate = estrenoRepository.findAll().collectList().block().size();
        estreno.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(estreno))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Estreno in the database
        List<Estreno> estrenoList = estrenoRepository.findAll().collectList().block();
        assertThat(estrenoList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void deleteEstreno() {
        // Initialize the database
        estrenoRepository.save(estreno).block();

        int databaseSizeBeforeDelete = estrenoRepository.findAll().collectList().block().size();

        // Delete the estreno
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, estreno.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        List<Estreno> estrenoList = estrenoRepository.findAll().collectList().block();
        assertThat(estrenoList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
