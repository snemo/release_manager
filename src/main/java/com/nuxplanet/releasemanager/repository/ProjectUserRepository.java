package com.nuxplanet.releasemanager.repository;

import com.nuxplanet.releasemanager.domain.ProjectUser;

import com.nuxplanet.releasemanager.domain.User;
import org.springframework.data.jpa.repository.*;

import java.util.List;

/**
 * Spring Data JPA repository for the ProjectUser entity.
 */
@SuppressWarnings("unused")
public interface ProjectUserRepository extends JpaRepository<ProjectUser,Long> {

    @Query("select projectUser from ProjectUser projectUser where projectUser.user.login = ?#{principal.username}")
    List<ProjectUser> findByUserIsCurrentUser();

    @Query("select projectUser from ProjectUser projectUser where projectUser.createdBy.login = ?#{principal.username}")
    List<ProjectUser> findByCreatedByIsCurrentUser();

    List<ProjectUser> findByUser(User user);
}
