package com.cursojhipster.puebajhipster.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;

import com.cursojhipster.puebajhipster.IntegrationTest;
import com.cursojhipster.puebajhipster.domain.Categoria;
import com.cursojhipster.puebajhipster.repository.CategoriaRepository;
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
import org.springframework.util.Base64Utils;

/**
 * Integration tests for the {@link CategoriaResource} REST controller.
 */
@IntegrationTest
@AutoConfigureWebTestClient
@WithMockUser
class CategoriaResourceIT {

    private static final String DEFAULT_NOMBRE = "AAAAAAAAAA";
    private static final String UPDATED_NOMBRE = "BBBBBBBBBB";

    private static final byte[] DEFAULT_IMAGEN = TestUtil.createByteArray(1, "0");
    private static final byte[] UPDATED_IMAGEN = TestUtil.createByteArray(1, "1");
    private static final String DEFAULT_IMAGEN_CONTENT_TYPE = "image/jpg";
    private static final String UPDATED_IMAGEN_CONTENT_TYPE = "image/png";

    private static final String ENTITY_API_URL = "/api/categorias";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private CategoriaRepository categoriaRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private WebTestClient webTestClient;

    private Categoria categoria;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Categoria createEntity(EntityManager em) {
        Categoria categoria = new Categoria().nombre(DEFAULT_NOMBRE).imagen(DEFAULT_IMAGEN).imagenContentType(DEFAULT_IMAGEN_CONTENT_TYPE);
        return categoria;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Categoria createUpdatedEntity(EntityManager em) {
        Categoria categoria = new Categoria().nombre(UPDATED_NOMBRE).imagen(UPDATED_IMAGEN).imagenContentType(UPDATED_IMAGEN_CONTENT_TYPE);
        return categoria;
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll(Categoria.class).block();
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
        categoria = createEntity(em);
    }

    @Test
    void createCategoria() throws Exception {
        int databaseSizeBeforeCreate = categoriaRepository.findAll().collectList().block().size();
        // Create the Categoria
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(categoria))
            .exchange()
            .expectStatus()
            .isCreated();

        // Validate the Categoria in the database
        List<Categoria> categoriaList = categoriaRepository.findAll().collectList().block();
        assertThat(categoriaList).hasSize(databaseSizeBeforeCreate + 1);
        Categoria testCategoria = categoriaList.get(categoriaList.size() - 1);
        assertThat(testCategoria.getNombre()).isEqualTo(DEFAULT_NOMBRE);
        assertThat(testCategoria.getImagen()).isEqualTo(DEFAULT_IMAGEN);
        assertThat(testCategoria.getImagenContentType()).isEqualTo(DEFAULT_IMAGEN_CONTENT_TYPE);
    }

    @Test
    void createCategoriaWithExistingId() throws Exception {
        // Create the Categoria with an existing ID
        categoria.setId(1L);

        int databaseSizeBeforeCreate = categoriaRepository.findAll().collectList().block().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(categoria))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Categoria in the database
        List<Categoria> categoriaList = categoriaRepository.findAll().collectList().block();
        assertThat(categoriaList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    void checkNombreIsRequired() throws Exception {
        int databaseSizeBeforeTest = categoriaRepository.findAll().collectList().block().size();
        // set the field null
        categoria.setNombre(null);

        // Create the Categoria, which fails.

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(categoria))
            .exchange()
            .expectStatus()
            .isBadRequest();

        List<Categoria> categoriaList = categoriaRepository.findAll().collectList().block();
        assertThat(categoriaList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    void getAllCategoriasAsStream() {
        // Initialize the database
        categoriaRepository.save(categoria).block();

        List<Categoria> categoriaList = webTestClient
            .get()
            .uri(ENTITY_API_URL)
            .accept(MediaType.APPLICATION_NDJSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentTypeCompatibleWith(MediaType.APPLICATION_NDJSON)
            .returnResult(Categoria.class)
            .getResponseBody()
            .filter(categoria::equals)
            .collectList()
            .block(Duration.ofSeconds(5));

        assertThat(categoriaList).isNotNull();
        assertThat(categoriaList).hasSize(1);
        Categoria testCategoria = categoriaList.get(0);
        assertThat(testCategoria.getNombre()).isEqualTo(DEFAULT_NOMBRE);
        assertThat(testCategoria.getImagen()).isEqualTo(DEFAULT_IMAGEN);
        assertThat(testCategoria.getImagenContentType()).isEqualTo(DEFAULT_IMAGEN_CONTENT_TYPE);
    }

    @Test
    void getAllCategorias() {
        // Initialize the database
        categoriaRepository.save(categoria).block();

        // Get all the categoriaList
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
            .value(hasItem(categoria.getId().intValue()))
            .jsonPath("$.[*].nombre")
            .value(hasItem(DEFAULT_NOMBRE))
            .jsonPath("$.[*].imagenContentType")
            .value(hasItem(DEFAULT_IMAGEN_CONTENT_TYPE))
            .jsonPath("$.[*].imagen")
            .value(hasItem(Base64Utils.encodeToString(DEFAULT_IMAGEN)));
    }

    @Test
    void getCategoria() {
        // Initialize the database
        categoriaRepository.save(categoria).block();

        // Get the categoria
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, categoria.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(categoria.getId().intValue()))
            .jsonPath("$.nombre")
            .value(is(DEFAULT_NOMBRE))
            .jsonPath("$.imagenContentType")
            .value(is(DEFAULT_IMAGEN_CONTENT_TYPE))
            .jsonPath("$.imagen")
            .value(is(Base64Utils.encodeToString(DEFAULT_IMAGEN)));
    }

    @Test
    void getNonExistingCategoria() {
        // Get the categoria
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putNewCategoria() throws Exception {
        // Initialize the database
        categoriaRepository.save(categoria).block();

        int databaseSizeBeforeUpdate = categoriaRepository.findAll().collectList().block().size();

        // Update the categoria
        Categoria updatedCategoria = categoriaRepository.findById(categoria.getId()).block();
        updatedCategoria.nombre(UPDATED_NOMBRE).imagen(UPDATED_IMAGEN).imagenContentType(UPDATED_IMAGEN_CONTENT_TYPE);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, updatedCategoria.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(updatedCategoria))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Categoria in the database
        List<Categoria> categoriaList = categoriaRepository.findAll().collectList().block();
        assertThat(categoriaList).hasSize(databaseSizeBeforeUpdate);
        Categoria testCategoria = categoriaList.get(categoriaList.size() - 1);
        assertThat(testCategoria.getNombre()).isEqualTo(UPDATED_NOMBRE);
        assertThat(testCategoria.getImagen()).isEqualTo(UPDATED_IMAGEN);
        assertThat(testCategoria.getImagenContentType()).isEqualTo(UPDATED_IMAGEN_CONTENT_TYPE);
    }

    @Test
    void putNonExistingCategoria() throws Exception {
        int databaseSizeBeforeUpdate = categoriaRepository.findAll().collectList().block().size();
        categoria.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, categoria.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(categoria))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Categoria in the database
        List<Categoria> categoriaList = categoriaRepository.findAll().collectList().block();
        assertThat(categoriaList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchCategoria() throws Exception {
        int databaseSizeBeforeUpdate = categoriaRepository.findAll().collectList().block().size();
        categoria.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(categoria))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Categoria in the database
        List<Categoria> categoriaList = categoriaRepository.findAll().collectList().block();
        assertThat(categoriaList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamCategoria() throws Exception {
        int databaseSizeBeforeUpdate = categoriaRepository.findAll().collectList().block().size();
        categoria.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(categoria))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Categoria in the database
        List<Categoria> categoriaList = categoriaRepository.findAll().collectList().block();
        assertThat(categoriaList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdateCategoriaWithPatch() throws Exception {
        // Initialize the database
        categoriaRepository.save(categoria).block();

        int databaseSizeBeforeUpdate = categoriaRepository.findAll().collectList().block().size();

        // Update the categoria using partial update
        Categoria partialUpdatedCategoria = new Categoria();
        partialUpdatedCategoria.setId(categoria.getId());

        partialUpdatedCategoria.nombre(UPDATED_NOMBRE).imagen(UPDATED_IMAGEN).imagenContentType(UPDATED_IMAGEN_CONTENT_TYPE);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedCategoria.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedCategoria))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Categoria in the database
        List<Categoria> categoriaList = categoriaRepository.findAll().collectList().block();
        assertThat(categoriaList).hasSize(databaseSizeBeforeUpdate);
        Categoria testCategoria = categoriaList.get(categoriaList.size() - 1);
        assertThat(testCategoria.getNombre()).isEqualTo(UPDATED_NOMBRE);
        assertThat(testCategoria.getImagen()).isEqualTo(UPDATED_IMAGEN);
        assertThat(testCategoria.getImagenContentType()).isEqualTo(UPDATED_IMAGEN_CONTENT_TYPE);
    }

    @Test
    void fullUpdateCategoriaWithPatch() throws Exception {
        // Initialize the database
        categoriaRepository.save(categoria).block();

        int databaseSizeBeforeUpdate = categoriaRepository.findAll().collectList().block().size();

        // Update the categoria using partial update
        Categoria partialUpdatedCategoria = new Categoria();
        partialUpdatedCategoria.setId(categoria.getId());

        partialUpdatedCategoria.nombre(UPDATED_NOMBRE).imagen(UPDATED_IMAGEN).imagenContentType(UPDATED_IMAGEN_CONTENT_TYPE);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedCategoria.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedCategoria))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Categoria in the database
        List<Categoria> categoriaList = categoriaRepository.findAll().collectList().block();
        assertThat(categoriaList).hasSize(databaseSizeBeforeUpdate);
        Categoria testCategoria = categoriaList.get(categoriaList.size() - 1);
        assertThat(testCategoria.getNombre()).isEqualTo(UPDATED_NOMBRE);
        assertThat(testCategoria.getImagen()).isEqualTo(UPDATED_IMAGEN);
        assertThat(testCategoria.getImagenContentType()).isEqualTo(UPDATED_IMAGEN_CONTENT_TYPE);
    }

    @Test
    void patchNonExistingCategoria() throws Exception {
        int databaseSizeBeforeUpdate = categoriaRepository.findAll().collectList().block().size();
        categoria.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, categoria.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(categoria))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Categoria in the database
        List<Categoria> categoriaList = categoriaRepository.findAll().collectList().block();
        assertThat(categoriaList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchCategoria() throws Exception {
        int databaseSizeBeforeUpdate = categoriaRepository.findAll().collectList().block().size();
        categoria.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(categoria))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Categoria in the database
        List<Categoria> categoriaList = categoriaRepository.findAll().collectList().block();
        assertThat(categoriaList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamCategoria() throws Exception {
        int databaseSizeBeforeUpdate = categoriaRepository.findAll().collectList().block().size();
        categoria.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(categoria))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Categoria in the database
        List<Categoria> categoriaList = categoriaRepository.findAll().collectList().block();
        assertThat(categoriaList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void deleteCategoria() {
        // Initialize the database
        categoriaRepository.save(categoria).block();

        int databaseSizeBeforeDelete = categoriaRepository.findAll().collectList().block().size();

        // Delete the categoria
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, categoria.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        List<Categoria> categoriaList = categoriaRepository.findAll().collectList().block();
        assertThat(categoriaList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
