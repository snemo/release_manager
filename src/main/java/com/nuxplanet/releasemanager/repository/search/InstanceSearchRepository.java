package com.nuxplanet.releasemanager.repository.search;

import com.nuxplanet.releasemanager.domain.Instance;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data ElasticSearch repository for the Instance entity.
 */
public interface InstanceSearchRepository extends ElasticsearchRepository<Instance, Long> {
}
