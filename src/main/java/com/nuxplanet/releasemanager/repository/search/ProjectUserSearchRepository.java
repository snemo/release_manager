package com.nuxplanet.releasemanager.repository.search;

import com.nuxplanet.releasemanager.domain.ProjectUser;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data ElasticSearch repository for the ProjectUser entity.
 */
public interface ProjectUserSearchRepository extends ElasticsearchRepository<ProjectUser, Long> {
}
