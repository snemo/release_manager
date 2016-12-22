package com.nuxplanet.releasemanager.web.rest;

import com.nuxplanet.releasemanager.ReleasemanagerApp;

import com.nuxplanet.releasemanager.domain.ProjectUser;
import com.nuxplanet.releasemanager.repository.ProjectUserRepository;
import com.nuxplanet.releasemanager.repository.search.ProjectUserSearchRepository;

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
 * Test class for the ProjectUserResource REST controller.
 *
 * @see ProjectUserResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = ReleasemanagerApp.class)
public class ProjectUserResourceIntTest {

    private static final LocalDate DEFAULT_CREATED = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_CREATED = LocalDate.now(ZoneId.systemDefault());

    @Inject
    private ProjectUserRepository projectUserRepository;

    @Inject
    private ProjectUserSearchRepository projectUserSearchRepository;

    @Inject
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Inject
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Inject
    private EntityManager em;

    private MockMvc restProjectUserMockMvc;

    private ProjectUser projectUser;

    @PostConstruct
    public void setup() {
        MockitoAnnotations.initMocks(this);
        ProjectUserResource projectUserResource = new ProjectUserResource();
        ReflectionTestUtils.setField(projectUserResource, "projectUserSearchRepository", projectUserSearchRepository);
        ReflectionTestUtils.setField(projectUserResource, "projectUserRepository", projectUserRepository);
        this.restProjectUserMockMvc = MockMvcBuilders.standaloneSetup(projectUserResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setMessageConverters(jacksonMessageConverter).build();
    }

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static ProjectUser createEntity(EntityManager em) {
        ProjectUser projectUser = new ProjectUser()
                .created(DEFAULT_CREATED);
        return projectUser;
    }

    @Before
    public void initTest() {
        projectUserSearchRepository.deleteAll();
        projectUser = createEntity(em);
    }

    @Test
    @Transactional
    public void createProjectUser() throws Exception {
        int databaseSizeBeforeCreate = projectUserRepository.findAll().size();

        // Create the ProjectUser

        restProjectUserMockMvc.perform(post("/api/project-users")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(projectUser)))
                .andExpect(status().isCreated());

        // Validate the ProjectUser in the database
        List<ProjectUser> projectUsers = projectUserRepository.findAll();
        assertThat(projectUsers).hasSize(databaseSizeBeforeCreate + 1);
        ProjectUser testProjectUser = projectUsers.get(projectUsers.size() - 1);
        assertThat(testProjectUser.getCreated()).isEqualTo(DEFAULT_CREATED);

        // Validate the ProjectUser in ElasticSearch
        ProjectUser projectUserEs = projectUserSearchRepository.findOne(testProjectUser.getId());
        assertThat(projectUserEs).isEqualToComparingFieldByField(testProjectUser);
    }

    @Test
    @Transactional
    public void checkCreatedIsRequired() throws Exception {
        int databaseSizeBeforeTest = projectUserRepository.findAll().size();
        // set the field null
        projectUser.setCreated(null);

        // Create the ProjectUser, which fails.

        restProjectUserMockMvc.perform(post("/api/project-users")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(projectUser)))
                .andExpect(status().isBadRequest());

        List<ProjectUser> projectUsers = projectUserRepository.findAll();
        assertThat(projectUsers).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllProjectUsers() throws Exception {
        // Initialize the database
        projectUserRepository.saveAndFlush(projectUser);

        // Get all the projectUsers
        restProjectUserMockMvc.perform(get("/api/project-users?sort=id,desc"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$.[*].id").value(hasItem(projectUser.getId().intValue())))
                .andExpect(jsonPath("$.[*].created").value(hasItem(DEFAULT_CREATED.toString())));
    }

    @Test
    @Transactional
    public void getProjectUser() throws Exception {
        // Initialize the database
        projectUserRepository.saveAndFlush(projectUser);

        // Get the projectUser
        restProjectUserMockMvc.perform(get("/api/project-users/{id}", projectUser.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(projectUser.getId().intValue()))
            .andExpect(jsonPath("$.created").value(DEFAULT_CREATED.toString()));
    }

    @Test
    @Transactional
    public void getNonExistingProjectUser() throws Exception {
        // Get the projectUser
        restProjectUserMockMvc.perform(get("/api/project-users/{id}", Long.MAX_VALUE))
                .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateProjectUser() throws Exception {
        // Initialize the database
        projectUserRepository.saveAndFlush(projectUser);
        projectUserSearchRepository.save(projectUser);
        int databaseSizeBeforeUpdate = projectUserRepository.findAll().size();

        // Update the projectUser
        ProjectUser updatedProjectUser = projectUserRepository.findOne(projectUser.getId());
        updatedProjectUser
                .created(UPDATED_CREATED);

        restProjectUserMockMvc.perform(put("/api/project-users")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(updatedProjectUser)))
                .andExpect(status().isOk());

        // Validate the ProjectUser in the database
        List<ProjectUser> projectUsers = projectUserRepository.findAll();
        assertThat(projectUsers).hasSize(databaseSizeBeforeUpdate);
        ProjectUser testProjectUser = projectUsers.get(projectUsers.size() - 1);
        assertThat(testProjectUser.getCreated()).isEqualTo(UPDATED_CREATED);

        // Validate the ProjectUser in ElasticSearch
        ProjectUser projectUserEs = projectUserSearchRepository.findOne(testProjectUser.getId());
        assertThat(projectUserEs).isEqualToComparingFieldByField(testProjectUser);
    }

    @Test
    @Transactional
    public void deleteProjectUser() throws Exception {
        // Initialize the database
        projectUserRepository.saveAndFlush(projectUser);
        projectUserSearchRepository.save(projectUser);
        int databaseSizeBeforeDelete = projectUserRepository.findAll().size();

        // Get the projectUser
        restProjectUserMockMvc.perform(delete("/api/project-users/{id}", projectUser.getId())
                .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());

        // Validate ElasticSearch is empty
        boolean projectUserExistsInEs = projectUserSearchRepository.exists(projectUser.getId());
        assertThat(projectUserExistsInEs).isFalse();

        // Validate the database is empty
        List<ProjectUser> projectUsers = projectUserRepository.findAll();
        assertThat(projectUsers).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void searchProjectUser() throws Exception {
        // Initialize the database
        projectUserRepository.saveAndFlush(projectUser);
        projectUserSearchRepository.save(projectUser);

        // Search the projectUser
        restProjectUserMockMvc.perform(get("/api/_search/project-users?query=id:" + projectUser.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(projectUser.getId().intValue())))
            .andExpect(jsonPath("$.[*].created").value(hasItem(DEFAULT_CREATED.toString())));
    }
}
