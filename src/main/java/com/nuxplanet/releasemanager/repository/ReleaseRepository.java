package com.nuxplanet.releasemanager.repository;

import com.nuxplanet.releasemanager.domain.Release;

import org.springframework.data.jpa.repository.*;

import java.util.List;

/**
 * Spring Data JPA repository for the Release entity.
 */
@SuppressWarnings("unused")
public interface ReleaseRepository extends JpaRepository<Release,Long> {

}
