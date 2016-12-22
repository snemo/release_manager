package com.nuxplanet.releasemanager.service;

import com.nuxplanet.releasemanager.domain.ProjectUser;
import com.nuxplanet.releasemanager.repository.InstanceRepository;
import com.nuxplanet.releasemanager.repository.ProjectRepository;
import com.nuxplanet.releasemanager.repository.ProjectUserRepository;
import com.nuxplanet.releasemanager.repository.ReleaseRepository;
import com.nuxplanet.releasemanager.repository.search.InstanceSearchRepository;
import com.nuxplanet.releasemanager.repository.search.ProjectSearchRepository;
import com.nuxplanet.releasemanager.repository.search.ProjectUserSearchRepository;
import com.nuxplanet.releasemanager.repository.search.ReleaseSearchRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author Wojciech Olech
 */
@Service
public class ElasticSearchService {

    @Autowired
    private ProjectRepository projectRepository;
    @Autowired
    private ProjectSearchRepository projectSearchRepository;

    @Autowired
    private ReleaseRepository releaseRepository;
    @Autowired
    private ReleaseSearchRepository releaseSearchRepository;

    @Autowired
    private InstanceRepository instanceRepository;
    @Autowired
    private InstanceSearchRepository instanceSearchRepository;

    @Autowired
    private ProjectUserRepository projectUserRepository;
    @Autowired
    private ProjectUserSearchRepository projectUserSearchRepository;

    public void reindex() {
        // Reindex projects
        projectRepository.findAll().forEach(
            project -> projectSearchRepository.save(project));

        // Reindex releases
        releaseRepository.findAll().forEach(
            release -> releaseSearchRepository.save(release));

        // Reindex instances
        instanceRepository.findAll().forEach(
            instance -> instanceSearchRepository.save(instance));

        // Reindex project-user
        projectUserRepository.findAll().forEach(
            projectUser -> projectUserSearchRepository.save(projectUser));
    }
}
