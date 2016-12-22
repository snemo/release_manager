package com.nuxplanet.releasemanager.web.rest;

import com.codahale.metrics.annotation.Timed;
import com.nuxplanet.releasemanager.domain.Release;

import com.nuxplanet.releasemanager.repository.ReleaseRepository;
import com.nuxplanet.releasemanager.repository.search.ReleaseSearchRepository;
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
 * REST controller for managing Release.
 */
@RestController
@RequestMapping("/api")
public class ReleaseResource {

    private final Logger log = LoggerFactory.getLogger(ReleaseResource.class);
        
    @Inject
    private ReleaseRepository releaseRepository;

    @Inject
    private ReleaseSearchRepository releaseSearchRepository;

    /**
     * POST  /releases : Create a new release.
     *
     * @param release the release to create
     * @return the ResponseEntity with status 201 (Created) and with body the new release, or with status 400 (Bad Request) if the release has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PostMapping("/releases")
    @Timed
    public ResponseEntity<Release> createRelease(@Valid @RequestBody Release release) throws URISyntaxException {
        log.debug("REST request to save Release : {}", release);
        if (release.getId() != null) {
            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("release", "idexists", "A new release cannot already have an ID")).body(null);
        }
        Release result = releaseRepository.save(release);
        releaseSearchRepository.save(result);
        return ResponseEntity.created(new URI("/api/releases/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert("release", result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /releases : Updates an existing release.
     *
     * @param release the release to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated release,
     * or with status 400 (Bad Request) if the release is not valid,
     * or with status 500 (Internal Server Error) if the release couldnt be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PutMapping("/releases")
    @Timed
    public ResponseEntity<Release> updateRelease(@Valid @RequestBody Release release) throws URISyntaxException {
        log.debug("REST request to update Release : {}", release);
        if (release.getId() == null) {
            return createRelease(release);
        }
        Release result = releaseRepository.save(release);
        releaseSearchRepository.save(result);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert("release", release.getId().toString()))
            .body(result);
    }

    /**
     * GET  /releases : get all the releases.
     *
     * @param pageable the pagination information
     * @return the ResponseEntity with status 200 (OK) and the list of releases in body
     * @throws URISyntaxException if there is an error to generate the pagination HTTP headers
     */
    @GetMapping("/releases")
    @Timed
    public ResponseEntity<List<Release>> getAllReleases(Pageable pageable)
        throws URISyntaxException {
        log.debug("REST request to get a page of Releases");
        Page<Release> page = releaseRepository.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/releases");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    /**
     * GET  /releases/:id : get the "id" release.
     *
     * @param id the id of the release to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the release, or with status 404 (Not Found)
     */
    @GetMapping("/releases/{id}")
    @Timed
    public ResponseEntity<Release> getRelease(@PathVariable Long id) {
        log.debug("REST request to get Release : {}", id);
        Release release = releaseRepository.findOne(id);
        return Optional.ofNullable(release)
            .map(result -> new ResponseEntity<>(
                result,
                HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * DELETE  /releases/:id : delete the "id" release.
     *
     * @param id the id of the release to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @DeleteMapping("/releases/{id}")
    @Timed
    public ResponseEntity<Void> deleteRelease(@PathVariable Long id) {
        log.debug("REST request to delete Release : {}", id);
        releaseRepository.delete(id);
        releaseSearchRepository.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert("release", id.toString())).build();
    }

    /**
     * SEARCH  /_search/releases?query=:query : search for the release corresponding
     * to the query.
     *
     * @param query the query of the release search 
     * @param pageable the pagination information
     * @return the result of the search
     * @throws URISyntaxException if there is an error to generate the pagination HTTP headers
     */
    @GetMapping("/_search/releases")
    @Timed
    public ResponseEntity<List<Release>> searchReleases(@RequestParam String query, Pageable pageable)
        throws URISyntaxException {
        log.debug("REST request to search for a page of Releases for query {}", query);
        Page<Release> page = releaseSearchRepository.search(queryStringQuery(query), pageable);
        HttpHeaders headers = PaginationUtil.generateSearchPaginationHttpHeaders(query, page, "/api/_search/releases");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }


}
