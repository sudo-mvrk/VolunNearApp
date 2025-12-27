package com.volunnear.controller;

import com.volunnear.dto.response.PagedResponseDTO;
import com.volunnear.dto.response.ParticipantCardDTO;
import com.volunnear.service.activity.OrganizationVolunteerActivityManagementService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/api/v1/activities/{activityId}/participants")
@RequiredArgsConstructor
public class OrganizationVolunteerActivityManagementController {
    private final OrganizationVolunteerActivityManagementService service;

    @PreAuthorize("hasRole('ORGANIZATION')")
    @GetMapping
    public PagedResponseDTO<ParticipantCardDTO> getParticipants(@PathVariable Long activityId,
                                                                @RequestParam(defaultValue = "approved") String status,
                                                                @RequestParam(defaultValue = "10") int size,
                                                                @RequestParam(defaultValue = "0") int page,
                                                                Principal principal) {
        return service.getParticipants(activityId, status, PageRequest.of(page, size), principal);
    }

    @PreAuthorize("hasRole('ORGANIZATION')")
    @PostMapping("/{volunteerId}/approve")
    public ResponseEntity<Void> approveParticipant(@PathVariable Long activityId,
                                                   @PathVariable Long volunteerId,
                                                   Principal principal) {
        service.approveActivity(activityId, volunteerId, principal);
        return ResponseEntity.ok().build();
    }

    @PreAuthorize("hasRole('ORGANIZATION')")
    @DeleteMapping("/{volunteerId}")
    public ResponseEntity<Void> removeParticipant(@PathVariable Long activityId,
                                                  @PathVariable Long volunteerId,
                                                  Principal principal) {
        service.rejectOrKickParticipant(activityId, volunteerId, principal);
        return ResponseEntity.noContent().build();
    }
}