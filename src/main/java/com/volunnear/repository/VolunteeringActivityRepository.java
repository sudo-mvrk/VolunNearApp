package com.volunnear.repository;

import com.volunnear.entity.profile.Organization;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VolunteeringActivityRepository extends JpaRepository<Organization, Long> {
}
