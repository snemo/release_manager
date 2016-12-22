package com.nuxplanet.releasemanager.web.rest;

import com.codahale.metrics.annotation.Timed;
import com.nuxplanet.releasemanager.domain.ProjectUser;

import com.nuxplanet.releasemanager.repository.ProjectUserRepository;
import com.nuxplanet.releasemanager.repository.search.ProjectUserSearchRepository;
import com.nuxplanet.releasemanager.service.UserService;
import com.nuxplanet.releasemanager.web.rest.util.HeaderUtil;
import com.nuxplanet.releasemanager.web.rest.util.PaginationUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import javax.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static org.elasticsearch.index.query.QueryBuilders.*;

/**
 * REST controller for managing ProjectUser.
 */
@RestController
@RequestMapping("/api")
public class ProjectUserResource {

    private final Logger log = LoggerFactory.getLogger(ProjectUserResource.class);

    @Inject
    private ProjectUserRepository projectUserRepository;

    @Inject
    private ProjectUserSearchRepository projectUserSearchRepository;

    @Inject
    private UserService userService;

    /**
     * POST  /project-users : Create a new projectUser.
     *
     * @param projectUser the projectUser to create
     * @return the ResponseEntity with status 201 (Created) and with body the new projectUser, or with status 400 (Bad Request) if the projectUser has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PostMapping("/project-users")
    @Timed
    public ResponseEntity<ProjectUser> createProjectUser(@Valid @RequestBody ProjectUser projectUser) throws URISyntaxException {
        log.debug("REST request to save ProjectUser : {}", projectUser);
        if (projectUser.getId() != null) {
            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("projectUser", "idexists", "A new projectUser cannot already have an ID")).body(null);
        }
        projectUser.setCreatedBy(userService.getUserWithAuthorities());
        ProjectUser result = projectUserRepository.save(projectUser);
        projectUserSearchRepository.save(result);
        return ResponseEntity.created(new URI("/api/project-users/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert("projectUser", result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /project-users : Updates an existing projectUser.
     *
     * @param projectUser the projectUser to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated projectUser,
     * or with status 400 (Bad Request) if the projectUser is not valid,
     * or with status 500 (Internal Server Error) if the projectUser couldnt be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PutMapping("/project-users")
    @Timed
    public ResponseEntity<ProjectUser> updateProjectUser(@Valid @RequestBody ProjectUser projectUser) throws URISyntaxException {
        log.debug("REST request to update ProjectUser : {}", projectUser);
        if (projectUser.getId() == null) {
            return createProjectUser(projectUser);
        }
        ProjectUser result = projectUserRepository.save(projectUser);
        projectUserSearchRepository.save(result);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert("projectUser", projectUser.getId().toString()))
            .body(result);
    }

    /**
     * GET  /project-users : get all the projectUsers.
     *
     * @param pageable the pagination information
     * @return the ResponseEntity with status 200 (OK) and the list of projectUsers in body
     * @throws URISyntaxException if there is an error to generate the pagination HTTP headers
     */
    @GetMapping("/project-users")
    @Timed
    public ResponseEntity<List<ProjectUser>> getAllProjectUsers(Pageable pageable)
        throws URISyntaxException {
        log.debug("REST request to get a page of ProjectUsers");
        Page<ProjectUser> page = projectUserRepository.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/project-users");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    /**
     * GET  /project-users/:id : get the "id" projectUser.
     *
     * @param id the id of the projectUser to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the projectUser, or with status 404 (Not Found)
     */
    @GetMapping("/project-users/{id}")
    @Timed
    public ResponseEntity<ProjectUser> getProjectUser(@PathVariable Long id) {
        log.debug("REST request to get ProjectUser : {}", id);
        ProjectUser projectUser = projectUserRepository.findOne(id);
        return Optional.ofNullable(projectUser)
            .map(result -> new ResponseEntity<>(
                result,
                HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * DELETE  /project-users/:id : delete the "id" projectUser.
     *
     * @param id the id of the projectUser to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @DeleteMapping("/project-users/{id}")
    @Timed
    public ResponseEntity<Void> deleteProjectUser(@PathVariable Long id) {
        log.debug("REST request to delete ProjectUser : {}", id);
        projectUserRepository.delete(id);
        projectUserSearchRepository.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert("projectUser", id.toString())).build();
    }

    /**
     * SEARCH  /_search/project-users?query=:query : search for the projectUser corresponding
     * to the query.
     *
     * @param query the query of the projectUser search
     * @param pageable the pagination information
     * @return the result of the search
     * @throws URISyntaxException if there is an error to generate the pagination HTTP headers
     */
    @GetMapping("/_search/project-users")
    @Timed
    public ResponseEntity<List<ProjectUser>> searchProjectUsers(@RequestParam String query, Pageable pageable)
        throws URISyntaxException {
        log.debug("REST request to search for a page of ProjectUsers for query {}", query);
        Page<ProjectUser> page = projectUserSearchRepository.search(queryStringQuery(query), pageable);
        HttpHeaders headers = PaginationUtil.generateSearchPaginationHttpHeaders(query, page, "/api/_search/project-users");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }


}
