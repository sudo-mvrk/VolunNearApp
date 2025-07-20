package com.volunnear.repository.activity;

import com.volunnear.entity.activity.VolunteerActivity;
import com.volunnear.entity.activity.VolunteerActivityId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface VolunteerActivityRepository extends JpaRepository<VolunteerActivity, VolunteerActivityId> {
    boolean existsVolunteerActivityByActivity_IdAndVolunteer_AppUser_Username(Long id, String username);
    List<VolunteerActivity> findAllByVolunteer_AppUser_Username(String username);
}
