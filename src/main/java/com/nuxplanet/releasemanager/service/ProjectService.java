package com.nuxplanet.releasemanager.service;

import com.nuxplanet.releasemanager.domain.Project;
import com.nuxplanet.releasemanager.domain.User;
import com.nuxplanet.releasemanager.repository.ProjectUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Wojciech Olech
 */
@Service
@Transactional
public class ProjectService {

    @Autowired
    private ProjectUserRepository projectUserRepository;

    public List<Project> getUserProjects(User user) {
        return projectUserRepository.findByUser(user).stream()
            .map(projectUser -> projectUser.getProject())
            .collect(Collectors.toList());
    }
}
