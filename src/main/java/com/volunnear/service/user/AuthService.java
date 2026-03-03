package com.volunnear.service.user;

import com.volunnear.dto.request.VolunteerRegistrationRequestDto;
import com.volunnear.dto.response.AppUserResponseDto;
import com.volunnear.entity.enums.Role;
import com.volunnear.entity.user.AppUser;
import com.volunnear.exception.UnauthorizedException;
import com.volunnear.exception.UserAlreadyExistsException;
import com.volunnear.mapper.AppUserMapper;
import com.volunnear.repository.AppUserRepository;
import com.volunnear.security.detail.CustomUserDetails;
import com.volunnear.service.VolunteerService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final AppUserMapper appUserMapper;
    private final PasswordEncoder passwordEncoder;
    private final VolunteerService volunteerService;
    private final AppUserRepository appUserRepository;

    @Transactional
    public AppUserResponseDto registerVolunteer(VolunteerRegistrationRequestDto request) {
        // TODO: Separate logic of Volunteer and Organization registration in two methods but make registerAppUser to avoid duplication
        checkIfUserExistsByEmailAndUsername(request);

        AppUser appUser = appUserMapper.toEntity(request, Role.ROLE_VOLUNTEER);

        appUser.setPassword(passwordEncoder.encode(request.password()));

        AppUser savedUser = appUserRepository.save(appUser);

        volunteerService.createVolunteerProfile(request, savedUser);
        return appUserMapper.toDto(savedUser);
    }

    public AppUserResponseDto getMyProfile(CustomUserDetails userDetails) {
        Long id = userDetails.getUser().getId();
        AppUser appUser = appUserRepository.findAppUserById(id)
                .orElseThrow(() -> new UnauthorizedException("User not found"));
        return appUserMapper.toDto(appUser);
    }

    private void checkIfUserExistsByEmailAndUsername(VolunteerRegistrationRequestDto request) {
        if (appUserRepository.existsByUsername(request.username())) {
            throw new UserAlreadyExistsException("User with that username already exists");
        }
        if (appUserRepository.existsByEmail(request.email())) {
            throw new UserAlreadyExistsException("User with that email already exists");
        }
    }
}
