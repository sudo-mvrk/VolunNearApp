package com.volunnear.service.activity;

import com.volunnear.ActivityRequestStatus;
import com.volunnear.dto.response.PagedResponseDTO;
import com.volunnear.entity.activity.Activity;
import com.volunnear.entity.activity.VolunteerActivity;
import com.volunnear.entity.profile.VolunteerProfile;
import com.volunnear.exception.BadDataInRequestException;
import com.volunnear.exception.DataNotFoundException;
import com.volunnear.repository.activity.VolunteerActivityRepository;
import com.volunnear.service.profile.VolunteerService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class VolunteerParticipationService {
    private final ActivityService activityService;
    private final VolunteerService volunteerService;
    private final VolunteerActivityRepository volunteerActivityRepository;

    public void createVolunteerActivityRequest(Long activityId, Principal principal) {
        if (!activityService.existsActivityById(activityId)) {
            throw new DataNotFoundException("Activity with id " + activityId + " not found");
        }
        VolunteerProfile volunteerProfile = volunteerService.getVolunteerProfileEntity(principal);
        if (volunteerActivityRepository.existsVolunteerActivityByActivity_IdAndVolunteer_AppUser_Username(activityId,
                principal.getName())) {
            throw new BadDataInRequestException("User with username" + principal.getName() +
                    " already have request for activity with id: " + activityId);
        }
        VolunteerActivity volunteerActivity = new VolunteerActivity();
        Activity activity = new Activity();
        activity.setId(activityId);

        volunteerActivity.setVolunteer(volunteerProfile);
        volunteerActivity.setActivity(activity);
        volunteerActivity.setStatus(ActivityRequestStatus.PENDING);

        volunteerActivityRepository.save(volunteerActivity);
    }

    public PagedResponseDTO<?> getAllRequestsByPrincipal(Pageable pageable, Principal principal) {
        List<VolunteerActivity> allByVolunteerAppUserUsername = volunteerActivityRepository.findAllByVolunteer_AppUser_Username(principal.getName());
        if (allByVolunteerAppUserUsername.isEmpty()) {
            throw new DataNotFoundException("Requests for user  " + principal.getName() + " not found");
        }
        List<Long> idList = allByVolunteerAppUserUsername.stream().map(v -> v.getActivity().getId()).toList();

        // TODO: Write method to parse result from activity service to use only activity id, activity title and status of request
        return null;
    }


    // TODO: Get my activities (find all volunteer activities by principal)
    // TODO: Cancel activity request
    // TODO: Leave activity
}
