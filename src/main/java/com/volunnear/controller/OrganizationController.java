package com.volunnear.controller;

import com.volunnear.dto.request.profile.OrganizationProfileSaveRequestDTO;
import com.volunnear.dto.response.PagedResponseDTO;
import com.volunnear.dto.response.activity.ActivityCardDTO;
import com.volunnear.dto.response.profile.OrganizationProfileResponseDTO;
import com.volunnear.service.activity.ActivityService;
import com.volunnear.service.profile.OrganizationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@Slf4j
@RestController
@RequestMapping("/api/v1/organizations")
@RequiredArgsConstructor
public class OrganizationController {
    private final ActivityService activityService;
    private final OrganizationService organizationService;

    // --- My Profile (Organization) ---
    @PreAuthorize("hasRole('ORGANIZATION')")
    @PostMapping("/me")
    public OrganizationProfileResponseDTO createOrganizationProfile(@RequestBody @Valid OrganizationProfileSaveRequestDTO requestDTO, Principal principal) {
        return organizationService.createOrganizationProfile(requestDTO, principal);
    }

    @PreAuthorize("hasRole('ORGANIZATION')")
    @PutMapping("/me")
    public OrganizationProfileResponseDTO updateOrganizationProfile(@RequestBody @Valid OrganizationProfileSaveRequestDTO requestDTO, Principal principal) {
        return organizationService.updateOrganizationProfile(requestDTO, principal);
    }

    @PreAuthorize("hasRole('ORGANIZATION')")
    @GetMapping("/me")
    public OrganizationProfileResponseDTO getOrganizationProfile(Principal principal) {
        return organizationService.getOrganizationProfile(principal);
    }

    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasRole('ORGANIZATION')")
    @DeleteMapping("/me")
    public void deleteOrganizationProfile(Principal principal) {
        organizationService.deleteOrganizationProfile(principal);
    }

    @PreAuthorize("hasRole('ORGANIZATION')")
    @GetMapping("/me/activities")
    public PagedResponseDTO<ActivityCardDTO> getOrganizationActivitiesByPrincipal(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Principal principal) {
        return activityService.getActivitiesByPrincipal(PageRequest.of(page, size), principal);
    }

    // --- Public Info ---
    @GetMapping("/{id}")
    public OrganizationProfileResponseDTO getOrganizationProfileById(@PathVariable("id") Long id) {
        return organizationService.getOrganizationProfileById(id);
    }

    @GetMapping("/{id}/activities")
    public PagedResponseDTO<ActivityCardDTO> getActivitiesByOrganizationId(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @PathVariable Long id) {
        return activityService.getActivitiesByOrganizationId(PageRequest.of(page, size), id);
    }
}