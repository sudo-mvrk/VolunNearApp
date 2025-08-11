package com.volunnear.service.profile;

import com.volunnear.dto.request.profile.VolunteerProfileSaveRequestDTO;
import com.volunnear.dto.response.profile.VolunteerProfileResponseDTO;
import com.volunnear.entity.profile.VolunteerProfile;
import com.volunnear.entity.users.AppUser;
import com.volunnear.exception.BadUserCredentialsException;
import com.volunnear.exception.DataNotFoundException;
import com.volunnear.exception.UserAlreadyExistsException;
import com.volunnear.mapper.profile.VolunteerProfileMapper;
import com.volunnear.repository.profile.VolunteerProfileRepository;
import com.volunnear.service.user.CurrentUserFacade;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.Principal;

@Slf4j
@Service
@RequiredArgsConstructor
public class VolunteerService {
    private final CurrentUserFacade currentUserFacade;
    private final VolunteerProfileMapper volunteerProfileMapper;
    private final VolunteerProfileRepository volunteerProfileRepository;

    @Transactional
    public VolunteerProfileResponseDTO createVolunteerProfile(VolunteerProfileSaveRequestDTO createRequest, Principal principal) {
        AppUser appUser = currentUserFacade.getUserFromPrincipal(principal);
        if (currentUserFacade.hasVolunteerProfile(appUser)) {
            throw new UserAlreadyExistsException("Volunteer profile with username " + appUser.getUsername() + " already exists, try update profile");
        }
        VolunteerProfile volunteerProfile = volunteerProfileMapper.toEntity(createRequest, appUser);
        volunteerProfileRepository.save(volunteerProfile);
        return volunteerProfileMapper.toDto(volunteerProfile);
    }

    @Transactional
    public VolunteerProfileResponseDTO updateVolunteerProfile(VolunteerProfileSaveRequestDTO editRequest, Principal principal) {
        AppUser appUser = currentUserFacade.getUserFromPrincipal(principal);
        VolunteerProfile profile = volunteerProfileRepository.findByAppUser_Username(appUser.getUsername())
                .orElseThrow(() -> new DataNotFoundException("Volunteer profile with username " + appUser.getUsername() + " not found"));
        volunteerProfileMapper.updateEntity(editRequest, profile);
        volunteerProfileRepository.save(profile);
        return volunteerProfileMapper.toDto(profile);
    }

    @Transactional(readOnly = true)
    public VolunteerProfileResponseDTO getVolunteerProfile(Principal principal) {
        VolunteerProfile profile = volunteerProfileRepository.findByAppUser_Username(principal.getName())
                .orElseThrow(() -> new BadUserCredentialsException("User with username" + principal.getName() + " not found"));
        return volunteerProfileMapper.toDto(profile);
    }

    @Transactional(readOnly = true)
    public VolunteerProfile getVolunteerProfileEntity(Principal principal) {
        return volunteerProfileRepository.findByAppUser_Username(principal.getName())
                .orElseThrow(() -> new BadUserCredentialsException("User with username" + principal.getName() + " not found"));
    }

    @Transactional
    public void deleteVolunteerProfile(Principal principal) {
        if (!volunteerProfileRepository.existsByAppUser_Username(principal.getName())) {
            throw new DataNotFoundException("Volunteer profile with username " + principal.getName() + " not found");
        }
        currentUserFacade.deleteAppUserByPrincipal(principal);
    }
}
