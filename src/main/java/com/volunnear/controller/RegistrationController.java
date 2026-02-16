package com.volunnear.controller;

import com.volunnear.UserRole;
import com.volunnear.dto.request.user.RegisterOrganizationUserProfileRequest;
import com.volunnear.dto.request.user.RegisterVolunteerUserProfileRequest;
import com.volunnear.service.user.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth/register")
@RequiredArgsConstructor
public class RegistrationController {
    private final UserService userService;

    @PostMapping("/volunteer")
    public void registerAppUserVolunteer(@RequestBody @Valid RegisterVolunteerUserProfileRequest profileRequest) {
        userService.registerVolunteer(profileRequest, UserRole.VOLUNTEER);
    }

    @PostMapping("/organization")
    public void registerAppUserOrganization(@RequestBody @Valid RegisterOrganizationUserProfileRequest profileRequest) {
        userService.registerOrganization(profileRequest, UserRole.ORGANIZATION);
    }
}