package com.volunnear.controller;

import com.volunnear.dto.request.VolunteeringActivityCreationRequest;
import com.volunnear.dto.response.activtiy.VolunteeringActivityResponseDto;
import com.volunnear.security.detail.CustomUserDetails;
import com.volunnear.service.activity.VolunteeringActivityService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/activities")
public class VolunteeringActivityController {
    private final VolunteeringActivityService activityService;

    public ResponseEntity<VolunteeringActivityResponseDto> createActivity(
            @RequestBody VolunteeringActivityCreationRequest  request,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        return ResponseEntity.ok(activityService.createVolunteeringActivity(request, userDetails));
    }
}
