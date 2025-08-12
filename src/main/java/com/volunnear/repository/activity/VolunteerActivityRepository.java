package com.volunnear.repository.activity;

import com.volunnear.ActivityRequestStatus;
import com.volunnear.entity.activity.VolunteerActivity;
import com.volunnear.entity.activity.VolunteerActivityId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface VolunteerActivityRepository extends JpaRepository<VolunteerActivity, VolunteerActivityId> {
    boolean existsVolunteerActivityByActivity_IdAndVolunteer_AppUser_Username(Long id, String username);

    boolean existsVolunteerActivityByActivity_IdAndVolunteer_AppUser_Username_AndStatus(Long id, String username, ActivityRequestStatus status);

    void deleteByVolunteer_AppUser_Username_AndActivity_Id_AndStatus(String volunteerUsername, Long activityId, ActivityRequestStatus status);

    Page<VolunteerActivity> findAllByVolunteer_AppUser_Username_AndStatus(String username, ActivityRequestStatus status, Pageable pageable);

    Page<VolunteerActivity> findAllByActivity_IdAndStatus(Long activityId, ActivityRequestStatus status, Pageable pageable);

    Optional<VolunteerActivity> findByActivity_IdAndVolunteer_IdAndStatus(Long activityId, Long volunteerId, ActivityRequestStatus status);

    Optional<VolunteerActivity> findByActivity_IdAndVolunteer_Id(Long activityId, Long volunteerId);
}
