package com.nuxplanet.releasemanager.repository;

import com.nuxplanet.releasemanager.domain.Instance;

import org.springframework.data.jpa.repository.*;

import java.util.List;

/**
 * Spring Data JPA repository for the Instance entity.
 */
@SuppressWarnings("unused")
public interface InstanceRepository extends JpaRepository<Instance,Long> {

}
