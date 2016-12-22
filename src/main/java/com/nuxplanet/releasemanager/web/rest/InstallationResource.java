package com.nuxplanet.releasemanager.web.rest;

import com.codahale.metrics.annotation.Timed;
import com.nuxplanet.releasemanager.domain.Installation;

import com.nuxplanet.releasemanager.repository.InstallationRepository;
import com.nuxplanet.releasemanager.repository.search.InstallationSearchRepository;
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
 * REST controller for managing Installation.
 */
@RestController
@RequestMapping("/api")
public class InstallationResource {

    private final Logger log = LoggerFactory.getLogger(InstallationResource.class);
        
    @Inject
    private InstallationRepository installationRepository;

    @Inject
    private InstallationSearchRepository installationSearchRepository;

    /**
     * POST  /installations : Create a new installation.
     *
     * @param installation the installation to create
     * @return the ResponseEntity with status 201 (Created) and with body the new installation, or with status 400 (Bad Request) if the installation has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PostMapping("/installations")
    @Timed
    public ResponseEntity<Installation> createInstallation(@Valid @RequestBody Installation installation) throws URISyntaxException {
        log.debug("REST request to save Installation : {}", installation);
        if (installation.getId() != null) {
            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("installation", "idexists", "A new installation cannot already have an ID")).body(null);
        }
        Installation result = installationRepository.save(installation);
        installationSearchRepository.save(result);
        return ResponseEntity.created(new URI("/api/installations/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert("installation", result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /installations : Updates an existing installation.
     *
     * @param installation the installation to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated installation,
     * or with status 400 (Bad Request) if the installation is not valid,
     * or with status 500 (Internal Server Error) if the installation couldnt be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PutMapping("/installations")
    @Timed
    public ResponseEntity<Installation> updateInstallation(@Valid @RequestBody Installation installation) throws URISyntaxException {
        log.debug("REST request to update Installation : {}", installation);
        if (installation.getId() == null) {
            return createInstallation(installation);
        }
        Installation result = installationRepository.save(installation);
        installationSearchRepository.save(result);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert("installation", installation.getId().toString()))
            .body(result);
    }

    /**
     * GET  /installations : get all the installations.
     *
     * @param pageable the pagination information
     * @return the ResponseEntity with status 200 (OK) and the list of installations in body
     * @throws URISyntaxException if there is an error to generate the pagination HTTP headers
     */
    @GetMapping("/installations")
    @Timed
    public ResponseEntity<List<Installation>> getAllInstallations(Pageable pageable)
        throws URISyntaxException {
        log.debug("REST request to get a page of Installations");
        Page<Installation> page = installationRepository.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/installations");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    /**
     * GET  /installations/:id : get the "id" installation.
     *
     * @param id the id of the installation to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the installation, or with status 404 (Not Found)
     */
    @GetMapping("/installations/{id}")
    @Timed
    public ResponseEntity<Installation> getInstallation(@PathVariable Long id) {
        log.debug("REST request to get Installation : {}", id);
        Installation installation = installationRepository.findOne(id);
        return Optional.ofNullable(installation)
            .map(result -> new ResponseEntity<>(
                result,
                HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * DELETE  /installations/:id : delete the "id" installation.
     *
     * @param id the id of the installation to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @DeleteMapping("/installations/{id}")
    @Timed
    public ResponseEntity<Void> deleteInstallation(@PathVariable Long id) {
        log.debug("REST request to delete Installation : {}", id);
        installationRepository.delete(id);
        installationSearchRepository.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert("installation", id.toString())).build();
    }

    /**
     * SEARCH  /_search/installations?query=:query : search for the installation corresponding
     * to the query.
     *
     * @param query the query of the installation search 
     * @param pageable the pagination information
     * @return the result of the search
     * @throws URISyntaxException if there is an error to generate the pagination HTTP headers
     */
    @GetMapping("/_search/installations")
    @Timed
    public ResponseEntity<List<Installation>> searchInstallations(@RequestParam String query, Pageable pageable)
        throws URISyntaxException {
        log.debug("REST request to search for a page of Installations for query {}", query);
        Page<Installation> page = installationSearchRepository.search(queryStringQuery(query), pageable);
        HttpHeaders headers = PaginationUtil.generateSearchPaginationHttpHeaders(query, page, "/api/_search/installations");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }


}
