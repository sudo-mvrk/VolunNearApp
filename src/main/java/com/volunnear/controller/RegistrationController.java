package com.volunnear.controller;

import com.volunnear.UserRole;
import com.volunnear.dto.request.user.RegisterAppUserDTO;
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
    public Long registerAppUserVolunteer(@RequestBody @Valid RegisterAppUserDTO appUser) {
        return userService.registerAppUser(appUser, UserRole.VOLUNTEER);
    }

    @PostMapping("/organization")
    public Long registerAppUserOrganization(@RequestBody @Valid RegisterAppUserDTO appUser) {
        return userService.registerAppUser(appUser, UserRole.ORGANIZATION);
    }
}