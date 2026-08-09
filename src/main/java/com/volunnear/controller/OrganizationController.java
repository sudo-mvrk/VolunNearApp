package com.volunnear.controller;

import com.volunnear.dto.request.OrganizationUpdateProfileRequestDto;
import com.volunnear.dto.response.profile.OrganizationProfileResponseDto;
import com.volunnear.security.detail.CustomUserDetails;
import com.volunnear.service.OrganizationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/organizations")
public class OrganizationController {
    private final OrganizationService organizationService;

    @PutMapping("/me")
    public ResponseEntity<OrganizationProfileResponseDto> updateOrganizationProfile(@RequestBody @Valid OrganizationUpdateProfileRequestDto requestDto,
                                                                                    @AuthenticationPrincipal CustomUserDetails userDetails) {
        OrganizationProfileResponseDto response = organizationService.updateOrganizationProfile(requestDto, userDetails);
        return ResponseEntity.ok(response);
    }
}
