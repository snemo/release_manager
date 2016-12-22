package com.nuxplanet.releasemanager.web.rest;

import com.nuxplanet.releasemanager.ReleasemanagerApp;

import com.nuxplanet.releasemanager.domain.Release;
import com.nuxplanet.releasemanager.repository.ReleaseRepository;
import com.nuxplanet.releasemanager.repository.search.ReleaseSearchRepository;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import static org.hamcrest.Matchers.hasItem;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Base64Utils;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Test class for the ReleaseResource REST controller.
 *
 * @see ReleaseResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = ReleasemanagerApp.class)
public class ReleaseResourceIntTest {

    private static final String DEFAULT_NAME = "AAAAA";
    private static final String UPDATED_NAME = "BBBBB";

    private static final String DEFAULT_DESCRIPTION = "AAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBB";

    private static final LocalDate DEFAULT_DATE = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_DATE = LocalDate.now(ZoneId.systemDefault());

    private static final String DEFAULT_COMMIT_ID = "AAAAA";
    private static final String UPDATED_COMMIT_ID = "BBBBB";

    private static final String DEFAULT_CONFIG = "AAAAA";
    private static final String UPDATED_CONFIG = "BBBBB";

    private static final String DEFAULT_ADAPTERS = "AAAAA";
    private static final String UPDATED_ADAPTERS = "BBBBB";

    private static final String DEFAULT_OTHER_STEPS = "AAAAA";
    private static final String UPDATED_OTHER_STEPS = "BBBBB";

    private static final byte[] DEFAULT_FILE = TestUtil.createByteArray(1, "0");
    private static final byte[] UPDATED_FILE = TestUtil.createByteArray(2, "1");
    private static final String DEFAULT_FILE_CONTENT_TYPE = "image/jpg";
    private static final String UPDATED_FILE_CONTENT_TYPE = "image/png";

    @Inject
    private ReleaseRepository releaseRepository;

    @Inject
    private ReleaseSearchRepository releaseSearchRepository;

    @Inject
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Inject
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Inject
    private EntityManager em;

    private MockMvc restReleaseMockMvc;

    private Release release;

    @PostConstruct
    public void setup() {
        MockitoAnnotations.initMocks(this);
        ReleaseResource releaseResource = new ReleaseResource();
        ReflectionTestUtils.setField(releaseResource, "releaseSearchRepository", releaseSearchRepository);
        ReflectionTestUtils.setField(releaseResource, "releaseRepository", releaseRepository);
        this.restReleaseMockMvc = MockMvcBuilders.standaloneSetup(releaseResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setMessageConverters(jacksonMessageConverter).build();
    }

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Release createEntity(EntityManager em) {
        Release release = new Release()
                .name(DEFAULT_NAME)
                .description(DEFAULT_DESCRIPTION)
                .date(DEFAULT_DATE)
                .commitId(DEFAULT_COMMIT_ID)
                .config(DEFAULT_CONFIG)
                .adapters(DEFAULT_ADAPTERS)
                .otherSteps(DEFAULT_OTHER_STEPS)
                .file(DEFAULT_FILE)
                .fileContentType(DEFAULT_FILE_CONTENT_TYPE);
        return release;
    }

    @Before
    public void initTest() {
        releaseSearchRepository.deleteAll();
        release = createEntity(em);
    }

    @Test
    @Transactional
    public void createRelease() throws Exception {
        int databaseSizeBeforeCreate = releaseRepository.findAll().size();

        // Create the Release

        restReleaseMockMvc.perform(post("/api/releases")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(release)))
                .andExpect(status().isCreated());

        // Validate the Release in the database
        List<Release> releases = releaseRepository.findAll();
        assertThat(releases).hasSize(databaseSizeBeforeCreate + 1);
        Release testRelease = releases.get(releases.size() - 1);
        assertThat(testRelease.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testRelease.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
        assertThat(testRelease.getDate()).isEqualTo(DEFAULT_DATE);
        assertThat(testRelease.getCommitId()).isEqualTo(DEFAULT_COMMIT_ID);
        assertThat(testRelease.getConfig()).isEqualTo(DEFAULT_CONFIG);
        assertThat(testRelease.getAdapters()).isEqualTo(DEFAULT_ADAPTERS);
        assertThat(testRelease.getOtherSteps()).isEqualTo(DEFAULT_OTHER_STEPS);
        assertThat(testRelease.getFile()).isEqualTo(DEFAULT_FILE);
        assertThat(testRelease.getFileContentType()).isEqualTo(DEFAULT_FILE_CONTENT_TYPE);

        // Validate the Release in ElasticSearch
        Release releaseEs = releaseSearchRepository.findOne(testRelease.getId());
        assertThat(releaseEs).isEqualToComparingFieldByField(testRelease);
    }

    @Test
    @Transactional
    public void checkNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = releaseRepository.findAll().size();
        // set the field null
        release.setName(null);

        // Create the Release, which fails.

        restReleaseMockMvc.perform(post("/api/releases")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(release)))
                .andExpect(status().isBadRequest());

        List<Release> releases = releaseRepository.findAll();
        assertThat(releases).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkDateIsRequired() throws Exception {
        int databaseSizeBeforeTest = releaseRepository.findAll().size();
        // set the field null
        release.setDate(null);

        // Create the Release, which fails.

        restReleaseMockMvc.perform(post("/api/releases")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(release)))
                .andExpect(status().isBadRequest());

        List<Release> releases = releaseRepository.findAll();
        assertThat(releases).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkCommitIdIsRequired() throws Exception {
        int databaseSizeBeforeTest = releaseRepository.findAll().size();
        // set the field null
        release.setCommitId(null);

        // Create the Release, which fails.

        restReleaseMockMvc.perform(post("/api/releases")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(release)))
                .andExpect(status().isBadRequest());

        List<Release> releases = releaseRepository.findAll();
        assertThat(releases).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllReleases() throws Exception {
        // Initialize the database
        releaseRepository.saveAndFlush(release);

        // Get all the releases
        restReleaseMockMvc.perform(get("/api/releases?sort=id,desc"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$.[*].id").value(hasItem(release.getId().intValue())))
                .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME.toString())))
                .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION.toString())))
                .andExpect(jsonPath("$.[*].date").value(hasItem(DEFAULT_DATE.toString())))
                .andExpect(jsonPath("$.[*].commitId").value(hasItem(DEFAULT_COMMIT_ID.toString())))
                .andExpect(jsonPath("$.[*].config").value(hasItem(DEFAULT_CONFIG.toString())))
                .andExpect(jsonPath("$.[*].adapters").value(hasItem(DEFAULT_ADAPTERS.toString())))
                .andExpect(jsonPath("$.[*].otherSteps").value(hasItem(DEFAULT_OTHER_STEPS.toString())))
                .andExpect(jsonPath("$.[*].fileContentType").value(hasItem(DEFAULT_FILE_CONTENT_TYPE)))
                .andExpect(jsonPath("$.[*].file").value(hasItem(Base64Utils.encodeToString(DEFAULT_FILE))));
    }

    @Test
    @Transactional
    public void getRelease() throws Exception {
        // Initialize the database
        releaseRepository.saveAndFlush(release);

        // Get the release
        restReleaseMockMvc.perform(get("/api/releases/{id}", release.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(release.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME.toString()))
            .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION.toString()))
            .andExpect(jsonPath("$.date").value(DEFAULT_DATE.toString()))
            .andExpect(jsonPath("$.commitId").value(DEFAULT_COMMIT_ID.toString()))
            .andExpect(jsonPath("$.config").value(DEFAULT_CONFIG.toString()))
            .andExpect(jsonPath("$.adapters").value(DEFAULT_ADAPTERS.toString()))
            .andExpect(jsonPath("$.otherSteps").value(DEFAULT_OTHER_STEPS.toString()))
            .andExpect(jsonPath("$.fileContentType").value(DEFAULT_FILE_CONTENT_TYPE))
            .andExpect(jsonPath("$.file").value(Base64Utils.encodeToString(DEFAULT_FILE)));
    }

    @Test
    @Transactional
    public void getNonExistingRelease() throws Exception {
        // Get the release
        restReleaseMockMvc.perform(get("/api/releases/{id}", Long.MAX_VALUE))
                .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateRelease() throws Exception {
        // Initialize the database
        releaseRepository.saveAndFlush(release);
        releaseSearchRepository.save(release);
        int databaseSizeBeforeUpdate = releaseRepository.findAll().size();

        // Update the release
        Release updatedRelease = releaseRepository.findOne(release.getId());
        updatedRelease
                .name(UPDATED_NAME)
                .description(UPDATED_DESCRIPTION)
                .date(UPDATED_DATE)
                .commitId(UPDATED_COMMIT_ID)
                .config(UPDATED_CONFIG)
                .adapters(UPDATED_ADAPTERS)
                .otherSteps(UPDATED_OTHER_STEPS)
                .file(UPDATED_FILE)
                .fileContentType(UPDATED_FILE_CONTENT_TYPE);

        restReleaseMockMvc.perform(put("/api/releases")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(updatedRelease)))
                .andExpect(status().isOk());

        // Validate the Release in the database
        List<Release> releases = releaseRepository.findAll();
        assertThat(releases).hasSize(databaseSizeBeforeUpdate);
        Release testRelease = releases.get(releases.size() - 1);
        assertThat(testRelease.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testRelease.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
        assertThat(testRelease.getDate()).isEqualTo(UPDATED_DATE);
        assertThat(testRelease.getCommitId()).isEqualTo(UPDATED_COMMIT_ID);
        assertThat(testRelease.getConfig()).isEqualTo(UPDATED_CONFIG);
        assertThat(testRelease.getAdapters()).isEqualTo(UPDATED_ADAPTERS);
        assertThat(testRelease.getOtherSteps()).isEqualTo(UPDATED_OTHER_STEPS);
        assertThat(testRelease.getFile()).isEqualTo(UPDATED_FILE);
        assertThat(testRelease.getFileContentType()).isEqualTo(UPDATED_FILE_CONTENT_TYPE);

        // Validate the Release in ElasticSearch
        Release releaseEs = releaseSearchRepository.findOne(testRelease.getId());
        assertThat(releaseEs).isEqualToComparingFieldByField(testRelease);
    }

    @Test
    @Transactional
    public void deleteRelease() throws Exception {
        // Initialize the database
        releaseRepository.saveAndFlush(release);
        releaseSearchRepository.save(release);
        int databaseSizeBeforeDelete = releaseRepository.findAll().size();

        // Get the release
        restReleaseMockMvc.perform(delete("/api/releases/{id}", release.getId())
                .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());

        // Validate ElasticSearch is empty
        boolean releaseExistsInEs = releaseSearchRepository.exists(release.getId());
        assertThat(releaseExistsInEs).isFalse();

        // Validate the database is empty
        List<Release> releases = releaseRepository.findAll();
        assertThat(releases).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void searchRelease() throws Exception {
        // Initialize the database
        releaseRepository.saveAndFlush(release);
        releaseSearchRepository.save(release);

        // Search the release
        restReleaseMockMvc.perform(get("/api/_search/releases?query=id:" + release.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(release.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME.toString())))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION.toString())))
            .andExpect(jsonPath("$.[*].date").value(hasItem(DEFAULT_DATE.toString())))
            .andExpect(jsonPath("$.[*].commitId").value(hasItem(DEFAULT_COMMIT_ID.toString())))
            .andExpect(jsonPath("$.[*].config").value(hasItem(DEFAULT_CONFIG.toString())))
            .andExpect(jsonPath("$.[*].adapters").value(hasItem(DEFAULT_ADAPTERS.toString())))
            .andExpect(jsonPath("$.[*].otherSteps").value(hasItem(DEFAULT_OTHER_STEPS.toString())))
            .andExpect(jsonPath("$.[*].fileContentType").value(hasItem(DEFAULT_FILE_CONTENT_TYPE)))
            .andExpect(jsonPath("$.[*].file").value(hasItem(Base64Utils.encodeToString(DEFAULT_FILE))));
    }
}
