package com.volunnear.repository;

import com.volunnear.entity.profile.Organization;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OrganizationRepository extends JpaRepository<Organization, Long> {
    Optional<Organization> findOrganizationByAppUser_Username(String username);
}
