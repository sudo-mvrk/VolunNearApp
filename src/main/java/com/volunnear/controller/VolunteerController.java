package com.volunnear.controller;

import com.volunnear.dto.request.VolunteerUpdateProfileRequestDto;
import com.volunnear.dto.response.profile.VolunteerProfileResponseDto;
import com.volunnear.security.detail.CustomUserDetails;
import com.volunnear.service.VolunteerService;
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
@RequestMapping("/api/v1/volunteers")
public class VolunteerController {
    private final VolunteerService volunteerService;

    @PutMapping("/me")
    public ResponseEntity<VolunteerProfileResponseDto> updateVolunteerProfile(
            @RequestBody @Valid VolunteerUpdateProfileRequestDto requestDto,
            @AuthenticationPrincipal CustomUserDetails userDetails
            ) {
        VolunteerProfileResponseDto response = volunteerService.updateVolunteerProfile(requestDto, userDetails);
        return ResponseEntity.ok(response);
    }
}
