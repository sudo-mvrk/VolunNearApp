package com.volunnear.repository.activity;

import com.volunnear.entity.activity.Activity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ActivityRepository extends JpaRepository<Activity, Long> {
    Optional<Activity> findByIdAndOrganizationProfile_AppUser_Username(Long id, String username);

    Optional<Activity> findActivityById(Long id);

    Page<Activity> findAllByOrganizationProfile_Id(Long id, Pageable pageable);

    Page<Activity> findAllByOrganizationProfile_AppUser_Username(String username, Pageable pageable);

    int deleteByIdAndOrganizationProfile_AppUser_Username(Long id, String username);

    boolean existsByIdAndOrganizationProfile_AppUser_Username(Long id, String username);
}