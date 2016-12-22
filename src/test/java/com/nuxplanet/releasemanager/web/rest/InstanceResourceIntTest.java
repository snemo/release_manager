package com.nuxplanet.releasemanager.web.rest;

import com.nuxplanet.releasemanager.ReleasemanagerApp;

import com.nuxplanet.releasemanager.domain.Instance;
import com.nuxplanet.releasemanager.repository.InstanceRepository;
import com.nuxplanet.releasemanager.repository.search.InstanceSearchRepository;

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
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Test class for the InstanceResource REST controller.
 *
 * @see InstanceResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = ReleasemanagerApp.class)
public class InstanceResourceIntTest {

    private static final String DEFAULT_NAME = "AAAAA";
    private static final String UPDATED_NAME = "BBBBB";

    private static final String DEFAULT_URL = "AAAAA";
    private static final String UPDATED_URL = "BBBBB";

    private static final String DEFAULT_DESCRIPTION = "AAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBB";

    @Inject
    private InstanceRepository instanceRepository;

    @Inject
    private InstanceSearchRepository instanceSearchRepository;

    @Inject
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Inject
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Inject
    private EntityManager em;

    private MockMvc restInstanceMockMvc;

    private Instance instance;

    @PostConstruct
    public void setup() {
        MockitoAnnotations.initMocks(this);
        InstanceResource instanceResource = new InstanceResource();
        ReflectionTestUtils.setField(instanceResource, "instanceSearchRepository", instanceSearchRepository);
        ReflectionTestUtils.setField(instanceResource, "instanceRepository", instanceRepository);
        this.restInstanceMockMvc = MockMvcBuilders.standaloneSetup(instanceResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setMessageConverters(jacksonMessageConverter).build();
    }

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Instance createEntity(EntityManager em) {
        Instance instance = new Instance()
                .name(DEFAULT_NAME)
                .url(DEFAULT_URL)
                .description(DEFAULT_DESCRIPTION);
        return instance;
    }

    @Before
    public void initTest() {
        instanceSearchRepository.deleteAll();
        instance = createEntity(em);
    }

    @Test
    @Transactional
    public void createInstance() throws Exception {
        int databaseSizeBeforeCreate = instanceRepository.findAll().size();

        // Create the Instance

        restInstanceMockMvc.perform(post("/api/instances")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(instance)))
                .andExpect(status().isCreated());

        // Validate the Instance in the database
        List<Instance> instances = instanceRepository.findAll();
        assertThat(instances).hasSize(databaseSizeBeforeCreate + 1);
        Instance testInstance = instances.get(instances.size() - 1);
        assertThat(testInstance.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testInstance.getUrl()).isEqualTo(DEFAULT_URL);
        assertThat(testInstance.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);

        // Validate the Instance in ElasticSearch
        Instance instanceEs = instanceSearchRepository.findOne(testInstance.getId());
        assertThat(instanceEs).isEqualToComparingFieldByField(testInstance);
    }

    @Test
    @Transactional
    public void checkNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = instanceRepository.findAll().size();
        // set the field null
        instance.setName(null);

        // Create the Instance, which fails.

        restInstanceMockMvc.perform(post("/api/instances")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(instance)))
                .andExpect(status().isBadRequest());

        List<Instance> instances = instanceRepository.findAll();
        assertThat(instances).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkUrlIsRequired() throws Exception {
        int databaseSizeBeforeTest = instanceRepository.findAll().size();
        // set the field null
        instance.setUrl(null);

        // Create the Instance, which fails.

        restInstanceMockMvc.perform(post("/api/instances")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(instance)))
                .andExpect(status().isBadRequest());

        List<Instance> instances = instanceRepository.findAll();
        assertThat(instances).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllInstances() throws Exception {
        // Initialize the database
        instanceRepository.saveAndFlush(instance);

        // Get all the instances
        restInstanceMockMvc.perform(get("/api/instances?sort=id,desc"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$.[*].id").value(hasItem(instance.getId().intValue())))
                .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME.toString())))
                .andExpect(jsonPath("$.[*].url").value(hasItem(DEFAULT_URL.toString())))
                .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION.toString())));
    }

    @Test
    @Transactional
    public void getInstance() throws Exception {
        // Initialize the database
        instanceRepository.saveAndFlush(instance);

        // Get the instance
        restInstanceMockMvc.perform(get("/api/instances/{id}", instance.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(instance.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME.toString()))
            .andExpect(jsonPath("$.url").value(DEFAULT_URL.toString()))
            .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION.toString()));
    }

    @Test
    @Transactional
    public void getNonExistingInstance() throws Exception {
        // Get the instance
        restInstanceMockMvc.perform(get("/api/instances/{id}", Long.MAX_VALUE))
                .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateInstance() throws Exception {
        // Initialize the database
        instanceRepository.saveAndFlush(instance);
        instanceSearchRepository.save(instance);
        int databaseSizeBeforeUpdate = instanceRepository.findAll().size();

        // Update the instance
        Instance updatedInstance = instanceRepository.findOne(instance.getId());
        updatedInstance
                .name(UPDATED_NAME)
                .url(UPDATED_URL)
                .description(UPDATED_DESCRIPTION);

        restInstanceMockMvc.perform(put("/api/instances")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(updatedInstance)))
                .andExpect(status().isOk());

        // Validate the Instance in the database
        List<Instance> instances = instanceRepository.findAll();
        assertThat(instances).hasSize(databaseSizeBeforeUpdate);
        Instance testInstance = instances.get(instances.size() - 1);
        assertThat(testInstance.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testInstance.getUrl()).isEqualTo(UPDATED_URL);
        assertThat(testInstance.getDescription()).isEqualTo(UPDATED_DESCRIPTION);

        // Validate the Instance in ElasticSearch
        Instance instanceEs = instanceSearchRepository.findOne(testInstance.getId());
        assertThat(instanceEs).isEqualToComparingFieldByField(testInstance);
    }

    @Test
    @Transactional
    public void deleteInstance() throws Exception {
        // Initialize the database
        instanceRepository.saveAndFlush(instance);
        instanceSearchRepository.save(instance);
        int databaseSizeBeforeDelete = instanceRepository.findAll().size();

        // Get the instance
        restInstanceMockMvc.perform(delete("/api/instances/{id}", instance.getId())
                .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());

        // Validate ElasticSearch is empty
        boolean instanceExistsInEs = instanceSearchRepository.exists(instance.getId());
        assertThat(instanceExistsInEs).isFalse();

        // Validate the database is empty
        List<Instance> instances = instanceRepository.findAll();
        assertThat(instances).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void searchInstance() throws Exception {
        // Initialize the database
        instanceRepository.saveAndFlush(instance);
        instanceSearchRepository.save(instance);

        // Search the instance
        restInstanceMockMvc.perform(get("/api/_search/instances?query=id:" + instance.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(instance.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME.toString())))
            .andExpect(jsonPath("$.[*].url").value(hasItem(DEFAULT_URL.toString())))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION.toString())));
    }
}
