package com.nuxplanet.releasemanager.repository.search;

import com.nuxplanet.releasemanager.domain.Release;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data ElasticSearch repository for the Release entity.
 */
public interface ReleaseSearchRepository extends ElasticsearchRepository<Release, Long> {
}
