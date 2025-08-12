package com.volunnear.service.activity;

import com.volunnear.ActivityRequestStatus;
import com.volunnear.dto.response.PagedResponseDTO;
import com.volunnear.dto.response.ParticipantCardDTO;
import com.volunnear.entity.activity.VolunteerActivity;
import com.volunnear.exception.DataNotFoundException;
import com.volunnear.mapper.PaginationMapper;
import com.volunnear.mapper.activity.VolunteerActivityMapper;
import com.volunnear.repository.activity.ActivityRepository;
import com.volunnear.repository.activity.VolunteerActivityRepository;
import com.volunnear.util.ActivityStatusParser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.Principal;
import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrganizationVolunteerActivityManagementService {
    private final PaginationMapper paginationMapper;
    private final ActivityRepository activityRepository;
    private final VolunteerActivityMapper volunteerActivityMapper;
    private final VolunteerActivityRepository volunteerActivityRepository;

    @Transactional(readOnly = true)
    public PagedResponseDTO<ParticipantCardDTO> getParticipants(Long activityId, String status, Pageable pageable, Principal principal) {
        validateActivityOwnership(activityId, principal);
        Page<VolunteerActivity> allByActivityIdAndStatus =
                volunteerActivityRepository.findAllByActivity_IdAndStatus(activityId, ActivityStatusParser.parse(status), pageable);
        return paginationMapper.mapPage(allByActivityIdAndStatus, volunteerActivityMapper::toVolunteerCardDto);
    }

    @Transactional
    public void approveActivity(Long activityId, Long volunteerId, Principal principal) {
        validateActivityOwnership(activityId, principal);
        VolunteerActivity activityRequest = volunteerActivityRepository
                .findByActivity_IdAndVolunteer_IdAndStatus(activityId, volunteerId, ActivityRequestStatus.PENDING)
                .orElseThrow(() -> new DataNotFoundException(
                        "Activity request for volunteer with id: " + volunteerId + " not found"
                ));

        activityRequest.setStatus(ActivityRequestStatus.APPROVED);
        activityRequest.setJoinedAt(LocalDateTime.now());
        volunteerActivityRepository.save(activityRequest);
    }

    @Transactional
    public void rejectOrKickParticipant(Long activityId, Long volunteerId, Principal principal) {
        validateActivityOwnership(activityId, principal);
        if (!activityRepository.existsById(activityId)) {
            throw new DataNotFoundException("Activity with id " + activityId + " not found");
        }
        VolunteerActivity volunteerActivity = volunteerActivityRepository.findByActivity_IdAndVolunteer_Id(activityId, volunteerId)
                .orElseThrow(() -> new DataNotFoundException("Request from volunteer with id: " + volunteerId + " to activity with id: " + activityId + " not found"));
        if (volunteerActivity.getStatus().equals(ActivityRequestStatus.APPROVED)) {
            volunteerActivity.setStatus(ActivityRequestStatus.KICKED);
        } else if (volunteerActivity.getStatus().equals(ActivityRequestStatus.PENDING)) {
            volunteerActivity.setStatus(ActivityRequestStatus.REJECTED);
        }
        volunteerActivityRepository.save(volunteerActivity);
    }

    private void validateActivityOwnership(Long activityId, Principal principal) {
        if (!activityRepository.existsByIdAndOrganizationProfile_AppUser_Username(activityId, principal.getName())) {
            throw new AccessDeniedException("You do not own this activity");
        }
    }
}
