package com.volunnear.controller;

import com.volunnear.dto.request.profile.VolunteerProfileSaveRequestDTO;
import com.volunnear.dto.response.profile.VolunteerProfileResponseDTO;
import com.volunnear.service.profile.VolunteerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/api/v1/volunteers")
@RequiredArgsConstructor
public class VolunteerController {
    private final VolunteerService volunteerService;

    @PostMapping("/me")
    public VolunteerProfileResponseDTO createVolunteerProfile(@RequestBody @Valid VolunteerProfileSaveRequestDTO createRequest, Principal principal) {
        return volunteerService.createVolunteerProfile(createRequest, principal);
    }

    @PutMapping("/me")
    public VolunteerProfileResponseDTO updateVolunteerProfile(@RequestBody @Valid VolunteerProfileSaveRequestDTO updateRequest, Principal principal) {
        return volunteerService.updateVolunteerProfile(updateRequest, principal);
    }

    @GetMapping("/me")
    public VolunteerProfileResponseDTO getVolunteerProfile(Principal principal) {
        return volunteerService.getVolunteerProfile(principal);
    }

    @ResponseStatus(HttpStatus.OK)
    @DeleteMapping("/me")
    public void deleteVolunteerProfile(Principal principal) {
        volunteerService.deleteVolunteerProfile(principal);
    }
}