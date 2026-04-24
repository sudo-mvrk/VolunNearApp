package com.volunnear.repository;

import com.volunnear.entity.profile.Volunteer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface VolunteerRepository extends JpaRepository<Volunteer, Long> {
    Optional<Volunteer> findByAppUser_Username(String username);
}
