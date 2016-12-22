package com.nuxplanet.releasemanager.repository.search;

import com.nuxplanet.releasemanager.domain.Installation;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data ElasticSearch repository for the Installation entity.
 */
public interface InstallationSearchRepository extends ElasticsearchRepository<Installation, Long> {
}
