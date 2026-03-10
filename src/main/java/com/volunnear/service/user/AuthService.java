package com.volunnear.service.user;

import com.volunnear.dto.RegistrationCredentials;
import com.volunnear.dto.request.OrganizationRegistrationRequestDto;
import com.volunnear.dto.request.VolunteerRegistrationRequestDto;
import com.volunnear.dto.response.AppUserResponseDto;
import com.volunnear.entity.enums.Role;
import com.volunnear.entity.user.AppUser;
import com.volunnear.exception.UnauthorizedException;
import com.volunnear.exception.UserAlreadyExistsException;
import com.volunnear.mapper.AppUserMapper;
import com.volunnear.repository.AppUserRepository;
import com.volunnear.security.detail.CustomUserDetails;
import com.volunnear.service.OrganizationService;
import com.volunnear.service.VolunteerService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final AppUserMapper appUserMapper;
    private final PasswordEncoder passwordEncoder;
    private final VolunteerService volunteerService;
    private final OrganizationService organizationService;
    private final AppUserRepository appUserRepository;

    @Transactional
    public AppUserResponseDto registerVolunteer(VolunteerRegistrationRequestDto request) {
        checkIfUserExistsByEmailAndUsername(request.username(), request.email());
        AppUser savedUser = createAndSaveAppUser(
                request,
                Role.ROLE_VOLUNTEER);
        volunteerService.createVolunteerProfile(request, savedUser);
        return appUserMapper.toDto(savedUser);
    }

    @Transactional
    public AppUserResponseDto registerOrganization(OrganizationRegistrationRequestDto request) {
        checkIfUserExistsByEmailAndUsername(request.username(), request.email());
        AppUser savedUser = createAndSaveAppUser(
                request,
                Role.ROLE_ORGANIZATION
        );
        organizationService.createOrganizationProfile(request, savedUser);
        return appUserMapper.toDto(savedUser);
    }

    public AppUserResponseDto getMyProfile(CustomUserDetails userDetails) {
        Long id = userDetails.getUser().getId();
        AppUser appUser = appUserRepository.findAppUserById(id)
                .orElseThrow(() -> new UnauthorizedException("User not found"));
        return appUserMapper.toDto(appUser);
    }

    private AppUser createAndSaveAppUser(RegistrationCredentials credentials, Role role) {
        AppUser appUser = AppUser.builder()
                .username(credentials.username())
                .email(credentials.email())
                .password(passwordEncoder.encode(credentials.password()))
                .roles(Set.of(role))
                .build();
        return appUserRepository.save(appUser);
    }

    private void checkIfUserExistsByEmailAndUsername(String username, String email) {
        if (appUserRepository.existsByUsername(username)) {
            throw new UserAlreadyExistsException("username", "User with that username already exists");
        }
        if (appUserRepository.existsByEmail(email)) {
            throw new UserAlreadyExistsException("email", "User with that email already exists");
        }
    }
}
