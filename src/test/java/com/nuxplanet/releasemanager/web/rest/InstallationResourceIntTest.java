package com.nuxplanet.releasemanager.web.rest;

import com.nuxplanet.releasemanager.ReleasemanagerApp;

import com.nuxplanet.releasemanager.domain.Installation;
import com.nuxplanet.releasemanager.repository.InstallationRepository;
import com.nuxplanet.releasemanager.repository.search.InstallationSearchRepository;

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
 * Test class for the InstallationResource REST controller.
 *
 * @see InstallationResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = ReleasemanagerApp.class)
public class InstallationResourceIntTest {

    private static final LocalDate DEFAULT_DATE = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_DATE = LocalDate.now(ZoneId.systemDefault());

    private static final String DEFAULT_NOTES = "AAAAA";
    private static final String UPDATED_NOTES = "BBBBB";

    private static final Boolean DEFAULT_SUCCESS = false;
    private static final Boolean UPDATED_SUCCESS = true;

    private static final String DEFAULT_ADAPTER_ERRORS = "AAAAA";
    private static final String UPDATED_ADAPTER_ERRORS = "BBBBB";

    private static final String DEFAULT_CONFIG_ERRORS = "AAAAA";
    private static final String UPDATED_CONFIG_ERRORS = "BBBBB";

    private static final String DEFAULT_OTHER_ERRORS = "AAAAA";
    private static final String UPDATED_OTHER_ERRORS = "BBBBB";

    @Inject
    private InstallationRepository installationRepository;

    @Inject
    private InstallationSearchRepository installationSearchRepository;

    @Inject
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Inject
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Inject
    private EntityManager em;

    private MockMvc restInstallationMockMvc;

    private Installation installation;

    @PostConstruct
    public void setup() {
        MockitoAnnotations.initMocks(this);
        InstallationResource installationResource = new InstallationResource();
        ReflectionTestUtils.setField(installationResource, "installationSearchRepository", installationSearchRepository);
        ReflectionTestUtils.setField(installationResource, "installationRepository", installationRepository);
        this.restInstallationMockMvc = MockMvcBuilders.standaloneSetup(installationResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setMessageConverters(jacksonMessageConverter).build();
    }

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Installation createEntity(EntityManager em) {
        Installation installation = new Installation()
                .date(DEFAULT_DATE)
                .notes(DEFAULT_NOTES)
                .success(DEFAULT_SUCCESS)
                .adapterErrors(DEFAULT_ADAPTER_ERRORS)
                .configErrors(DEFAULT_CONFIG_ERRORS)
                .otherErrors(DEFAULT_OTHER_ERRORS);
        return installation;
    }

    @Before
    public void initTest() {
        installationSearchRepository.deleteAll();
        installation = createEntity(em);
    }

    @Test
    @Transactional
    public void createInstallation() throws Exception {
        int databaseSizeBeforeCreate = installationRepository.findAll().size();

        // Create the Installation

        restInstallationMockMvc.perform(post("/api/installations")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(installation)))
                .andExpect(status().isCreated());

        // Validate the Installation in the database
        List<Installation> installations = installationRepository.findAll();
        assertThat(installations).hasSize(databaseSizeBeforeCreate + 1);
        Installation testInstallation = installations.get(installations.size() - 1);
        assertThat(testInstallation.getDate()).isEqualTo(DEFAULT_DATE);
        assertThat(testInstallation.getNotes()).isEqualTo(DEFAULT_NOTES);
        assertThat(testInstallation.isSuccess()).isEqualTo(DEFAULT_SUCCESS);
        assertThat(testInstallation.getAdapterErrors()).isEqualTo(DEFAULT_ADAPTER_ERRORS);
        assertThat(testInstallation.getConfigErrors()).isEqualTo(DEFAULT_CONFIG_ERRORS);
        assertThat(testInstallation.getOtherErrors()).isEqualTo(DEFAULT_OTHER_ERRORS);

        // Validate the Installation in ElasticSearch
        Installation installationEs = installationSearchRepository.findOne(testInstallation.getId());
        assertThat(installationEs).isEqualToComparingFieldByField(testInstallation);
    }

    @Test
    @Transactional
    public void checkDateIsRequired() throws Exception {
        int databaseSizeBeforeTest = installationRepository.findAll().size();
        // set the field null
        installation.setDate(null);

        // Create the Installation, which fails.

        restInstallationMockMvc.perform(post("/api/installations")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(installation)))
                .andExpect(status().isBadRequest());

        List<Installation> installations = installationRepository.findAll();
        assertThat(installations).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllInstallations() throws Exception {
        // Initialize the database
        installationRepository.saveAndFlush(installation);

        // Get all the installations
        restInstallationMockMvc.perform(get("/api/installations?sort=id,desc"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$.[*].id").value(hasItem(installation.getId().intValue())))
                .andExpect(jsonPath("$.[*].date").value(hasItem(DEFAULT_DATE.toString())))
                .andExpect(jsonPath("$.[*].notes").value(hasItem(DEFAULT_NOTES.toString())))
                .andExpect(jsonPath("$.[*].success").value(hasItem(DEFAULT_SUCCESS.booleanValue())))
                .andExpect(jsonPath("$.[*].adapterErrors").value(hasItem(DEFAULT_ADAPTER_ERRORS.toString())))
                .andExpect(jsonPath("$.[*].configErrors").value(hasItem(DEFAULT_CONFIG_ERRORS.toString())))
                .andExpect(jsonPath("$.[*].otherErrors").value(hasItem(DEFAULT_OTHER_ERRORS.toString())));
    }

    @Test
    @Transactional
    public void getInstallation() throws Exception {
        // Initialize the database
        installationRepository.saveAndFlush(installation);

        // Get the installation
        restInstallationMockMvc.perform(get("/api/installations/{id}", installation.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(installation.getId().intValue()))
            .andExpect(jsonPath("$.date").value(DEFAULT_DATE.toString()))
            .andExpect(jsonPath("$.notes").value(DEFAULT_NOTES.toString()))
            .andExpect(jsonPath("$.success").value(DEFAULT_SUCCESS.booleanValue()))
            .andExpect(jsonPath("$.adapterErrors").value(DEFAULT_ADAPTER_ERRORS.toString()))
            .andExpect(jsonPath("$.configErrors").value(DEFAULT_CONFIG_ERRORS.toString()))
            .andExpect(jsonPath("$.otherErrors").value(DEFAULT_OTHER_ERRORS.toString()));
    }

    @Test
    @Transactional
    public void getNonExistingInstallation() throws Exception {
        // Get the installation
        restInstallationMockMvc.perform(get("/api/installations/{id}", Long.MAX_VALUE))
                .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateInstallation() throws Exception {
        // Initialize the database
        installationRepository.saveAndFlush(installation);
        installationSearchRepository.save(installation);
        int databaseSizeBeforeUpdate = installationRepository.findAll().size();

        // Update the installation
        Installation updatedInstallation = installationRepository.findOne(installation.getId());
        updatedInstallation
                .date(UPDATED_DATE)
                .notes(UPDATED_NOTES)
                .success(UPDATED_SUCCESS)
                .adapterErrors(UPDATED_ADAPTER_ERRORS)
                .configErrors(UPDATED_CONFIG_ERRORS)
                .otherErrors(UPDATED_OTHER_ERRORS);

        restInstallationMockMvc.perform(put("/api/installations")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(updatedInstallation)))
                .andExpect(status().isOk());

        // Validate the Installation in the database
        List<Installation> installations = installationRepository.findAll();
        assertThat(installations).hasSize(databaseSizeBeforeUpdate);
        Installation testInstallation = installations.get(installations.size() - 1);
        assertThat(testInstallation.getDate()).isEqualTo(UPDATED_DATE);
        assertThat(testInstallation.getNotes()).isEqualTo(UPDATED_NOTES);
        assertThat(testInstallation.isSuccess()).isEqualTo(UPDATED_SUCCESS);
        assertThat(testInstallation.getAdapterErrors()).isEqualTo(UPDATED_ADAPTER_ERRORS);
        assertThat(testInstallation.getConfigErrors()).isEqualTo(UPDATED_CONFIG_ERRORS);
        assertThat(testInstallation.getOtherErrors()).isEqualTo(UPDATED_OTHER_ERRORS);

        // Validate the Installation in ElasticSearch
        Installation installationEs = installationSearchRepository.findOne(testInstallation.getId());
        assertThat(installationEs).isEqualToComparingFieldByField(testInstallation);
    }

    @Test
    @Transactional
    public void deleteInstallation() throws Exception {
        // Initialize the database
        installationRepository.saveAndFlush(installation);
        installationSearchRepository.save(installation);
        int databaseSizeBeforeDelete = installationRepository.findAll().size();

        // Get the installation
        restInstallationMockMvc.perform(delete("/api/installations/{id}", installation.getId())
                .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());

        // Validate ElasticSearch is empty
        boolean installationExistsInEs = installationSearchRepository.exists(installation.getId());
        assertThat(installationExistsInEs).isFalse();

        // Validate the database is empty
        List<Installation> installations = installationRepository.findAll();
        assertThat(installations).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void searchInstallation() throws Exception {
        // Initialize the database
        installationRepository.saveAndFlush(installation);
        installationSearchRepository.save(installation);

        // Search the installation
        restInstallationMockMvc.perform(get("/api/_search/installations?query=id:" + installation.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(installation.getId().intValue())))
            .andExpect(jsonPath("$.[*].date").value(hasItem(DEFAULT_DATE.toString())))
            .andExpect(jsonPath("$.[*].notes").value(hasItem(DEFAULT_NOTES.toString())))
            .andExpect(jsonPath("$.[*].success").value(hasItem(DEFAULT_SUCCESS.booleanValue())))
            .andExpect(jsonPath("$.[*].adapterErrors").value(hasItem(DEFAULT_ADAPTER_ERRORS.toString())))
            .andExpect(jsonPath("$.[*].configErrors").value(hasItem(DEFAULT_CONFIG_ERRORS.toString())))
            .andExpect(jsonPath("$.[*].otherErrors").value(hasItem(DEFAULT_OTHER_ERRORS.toString())));
    }
}
