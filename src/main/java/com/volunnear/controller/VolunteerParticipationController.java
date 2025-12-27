package com.volunnear.controller;

import com.volunnear.annotation.Idempotent;
import com.volunnear.dto.response.PagedResponseDTO;
import com.volunnear.dto.response.activity.ActivityRequestInfoDTO;
import com.volunnear.service.activity.VolunteerParticipationService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class VolunteerParticipationController {
    private final VolunteerParticipationService volunteerParticipationService;

    // TODO: "Create activity request" logically belongs to activity controller
    @Idempotent
    @PreAuthorize("hasRole('VOLUNTEER')")
    @PostMapping("/activities/{activityId}/participants")
    public void createVolunteerActivityRequest(@PathVariable("activityId") Long id, Principal principal) {
        volunteerParticipationService.createVolunteerActivityRequest(id, principal);
    }

    @PreAuthorize("hasRole('VOLUNTEER')")
    @DeleteMapping("/activities/{activityId}/participants")
    public ResponseEntity<Void> deleteVolunteerActivityRequest(@PathVariable("activityId") Long id, Principal principal) {
        volunteerParticipationService.cancelMyActivityRequest(id, principal);
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasRole('VOLUNTEER')")
    @DeleteMapping("/activities/{activityId}/participants/me")
    public ResponseEntity<Void> leaveFromActivityByPrincipal(@PathVariable("activityId") Long id, Principal principal) {
        volunteerParticipationService.leaveActivity(id, principal);
        return ResponseEntity.noContent().build();
    }

    // TODO: "My requests" logically belongs to volunteer profile
    @PreAuthorize("hasRole('VOLUNTEER')")
    @GetMapping("/volunteers/me/requests")
    public PagedResponseDTO<ActivityRequestInfoDTO> getVolunteerRequestsByPrincipal(@RequestParam(defaultValue = "0") int page,
                                                                                    @RequestParam(defaultValue = "10") int size,
                                                                                    @RequestParam(defaultValue = "approved") String status,
                                                                                    Principal principal) {
        return volunteerParticipationService.getAllRequestsByPrincipalAndStatus(PageRequest.of(page, size), status, principal);
    }
}