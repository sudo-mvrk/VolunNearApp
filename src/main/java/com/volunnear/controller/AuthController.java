package com.volunnear.controller;

import com.volunnear.dto.request.LoginRequestDto;
import com.volunnear.dto.request.OrganizationRegistrationRequestDto;
import com.volunnear.dto.request.VolunteerRegistrationRequestDto;
import com.volunnear.dto.response.AppUserResponseDto;
import com.volunnear.security.SecurityFacade;
import com.volunnear.security.detail.CustomUserDetails;
import com.volunnear.service.user.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
public class AuthController {
    private final AuthService authService;
    private final SecurityFacade securityFacade;

    @GetMapping("/csrf")
    public ResponseEntity<CsrfToken> getCsrfToken(CsrfToken token) {
        return ResponseEntity.ok(token);
    }

    @PostMapping("/register/volunteer")
    @ResponseStatus(code = HttpStatus.CREATED)
    public ResponseEntity<AppUserResponseDto> registerVolunteer(@RequestBody @Valid VolunteerRegistrationRequestDto request) {
        AppUserResponseDto response = authService.registerVolunteer(request);
        return ResponseEntity.ok(response);
    }


    @PostMapping("/register/organization")
    @ResponseStatus(code = HttpStatus.CREATED)
    public ResponseEntity<AppUserResponseDto> registerOrganization(@RequestBody @Valid OrganizationRegistrationRequestDto request) {
        AppUserResponseDto response = authService.registerOrganization(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/login")
    public ResponseEntity<Void> login(@RequestBody @Valid LoginRequestDto requestDto,
                                      HttpServletRequest httpServletRequest,
                                      HttpServletResponse httpServletResponse) {
        securityFacade.authenticateAndCreateSession(requestDto, httpServletRequest, httpServletResponse);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/me")
    public ResponseEntity<AppUserResponseDto> getMyProfile(@AuthenticationPrincipal CustomUserDetails userDetails) {
        AppUserResponseDto response = authService.getMyProfile(userDetails);
        return ResponseEntity.ok(response);
    }
}
