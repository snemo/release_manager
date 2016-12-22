package com.nuxplanet.releasemanager.repository;

import com.nuxplanet.releasemanager.domain.Installation;

import org.springframework.data.jpa.repository.*;

import java.util.List;

/**
 * Spring Data JPA repository for the Installation entity.
 */
@SuppressWarnings("unused")
public interface InstallationRepository extends JpaRepository<Installation,Long> {

}
