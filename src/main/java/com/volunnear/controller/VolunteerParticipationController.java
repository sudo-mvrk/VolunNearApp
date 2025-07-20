package com.volunnear.controller;

import com.volunnear.Routes;
import com.volunnear.dto.response.PagedResponseDTO;
import com.volunnear.dto.response.activity.ActivityCardDTO;
import com.volunnear.service.activity.VolunteerParticipationService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequiredArgsConstructor
public class VolunteerParticipationController {
    private final VolunteerParticipationService volunteerParticipationService;

    @PreAuthorize("hasRole('VOLUNTEER')")
    @PostMapping(Routes.VOLUNTEER_ACTIVITY_REQUEST)
    public void createVolunteerActivityRequest(@PathVariable Long id, Principal principal) {
        volunteerParticipationService.createVolunteerActivityRequest(id, principal);
    }

    @PreAuthorize("hasRole('VOLUNTEER')")
    @GetMapping(Routes.VOLUNTEER_REQUESTS)
    public PagedResponseDTO<ActivityCardDTO> getVolunteerRequests(@RequestParam(defaultValue = "0") int page,
                                                                  @RequestParam(defaultValue = "10") int size,
                                                                  Principal principal) {
        return volunteerParticipationService.getAllRequestsByPrincipal(PageRequest.of(page, size), principal);
    }
}
