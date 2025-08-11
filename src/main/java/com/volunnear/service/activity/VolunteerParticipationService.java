package com.volunnear.service.activity;

import com.volunnear.ActivityRequestStatus;
import com.volunnear.dto.response.PagedResponseDTO;
import com.volunnear.dto.response.activity.ActivityRequestInfoDTO;
import com.volunnear.entity.activity.Activity;
import com.volunnear.entity.activity.VolunteerActivity;
import com.volunnear.entity.profile.VolunteerProfile;
import com.volunnear.exception.BadDataInRequestException;
import com.volunnear.exception.DataNotFoundException;
import com.volunnear.mapper.PaginationMapper;
import com.volunnear.mapper.activity.VolunteerActivityMapper;
import com.volunnear.repository.activity.VolunteerActivityRepository;
import com.volunnear.service.profile.VolunteerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.Principal;

@Slf4j
@Service
@RequiredArgsConstructor
public class VolunteerParticipationService {
    private final ActivityService activityService;
    private final VolunteerService volunteerService;
    private final VolunteerActivityMapper volunteerActivityMapper;
    private final VolunteerActivityRepository volunteerActivityRepository;
    private final PaginationMapper paginationMapper;

    @Transactional
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

    @Transactional(readOnly = true)
    public PagedResponseDTO<ActivityRequestInfoDTO> getAllRequestsByPrincipalAndStatus(Pageable pageable, String status, Principal principal) {
        ActivityRequestStatus parsedStatus;
        try {
            parsedStatus = ActivityRequestStatus.valueOf(status.trim().toUpperCase());
        } catch (IllegalArgumentException | NullPointerException e) {
            throw new BadDataInRequestException("Invalid status value: " + status);
        }
        Page<VolunteerActivity> page = volunteerActivityRepository.findAllByVolunteer_AppUser_Username_AndStatus(principal.getName(), parsedStatus, pageable);
        if (page.isEmpty()) {
            throw new DataNotFoundException("Requests for user  " + principal.getName() + " with status: " + status.trim() + " not found");
        }
        return paginationMapper.mapPage(page, volunteerActivityMapper::toDto);
    }

    @Transactional
    public void cancelMyActivityRequest(Long activityId, Principal principal) {
        validateVolunteerActivityRequest(activityId, principal);
        volunteerActivityRepository.deleteByVolunteer_AppUser_Username_AndActivity_Id_AndStatus(principal.getName(), activityId, ActivityRequestStatus.PENDING);
    }


    @Transactional
    public void leaveActivity(Long activityId, Principal principal) {
        validateVolunteerActivityRequest(activityId, principal);
        volunteerActivityRepository.deleteByVolunteer_AppUser_Username_AndActivity_Id_AndStatus(principal.getName(), activityId, ActivityRequestStatus.APPROVED);
    }

    private void validateVolunteerActivityRequest(Long activityId, Principal principal) {
        if (!activityService.existsActivityById(activityId)) {
            throw new DataNotFoundException("Activity with id " + activityId + " not found");
        }
        if (!volunteerActivityRepository.existsVolunteerActivityByActivity_IdAndVolunteer_AppUser_Username_AndStatus(activityId,
                principal.getName(), ActivityRequestStatus.PENDING)) {
            throw new DataNotFoundException("Request by username " + principal.getName() + " not found by activity with id: " + activityId);
        }
    }
}
