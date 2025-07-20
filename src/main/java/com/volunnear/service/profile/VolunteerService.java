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
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.security.Principal;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class VolunteerService {
    private final CurrentUserFacade currentUserFacade;
    private final VolunteerProfileMapper volunteerProfileMapper;
    private final VolunteerProfileRepository volunteerProfileRepository;

    public VolunteerProfileResponseDTO createVolunteerProfile(VolunteerProfileSaveRequestDTO createRequest, Principal principal) {
        AppUser appUser = currentUserFacade.getUserFromPrincipal(principal);
        if (currentUserFacade.hasVolunteerProfile(appUser)) {
            throw new UserAlreadyExistsException("Volunteer profile with username " + appUser.getUsername() + " already exists, try update profile");
        }
        VolunteerProfile volunteerProfile = volunteerProfileMapper.toEntity(createRequest, appUser);
        volunteerProfileRepository.save(volunteerProfile);
        return volunteerProfileMapper.toDto(volunteerProfile);
    }

    public VolunteerProfileResponseDTO updateVolunteerProfile(VolunteerProfileSaveRequestDTO editRequest, Principal principal) {
        AppUser appUser = currentUserFacade.getUserFromPrincipal(principal);
        VolunteerProfile profile = volunteerProfileRepository.findByAppUser_Username(appUser.getUsername())
                .orElseThrow(() -> new DataNotFoundException("Volunteer profile with username " + appUser.getUsername() + " not found"));
        volunteerProfileMapper.updateEntity(editRequest, profile);
        volunteerProfileRepository.save(profile);
        return volunteerProfileMapper.toDto(profile);
    }

    public VolunteerProfileResponseDTO getVolunteerProfile(Principal principal) {
        VolunteerProfile profile = volunteerProfileRepository.findByAppUser_Username(principal.getName())
                .orElseThrow(() -> new BadUserCredentialsException("User with username" + principal.getName() + " not found"));
        return volunteerProfileMapper.toDto(profile);
    }

    public VolunteerProfile getVolunteerProfileEntity(Principal principal) {
        return volunteerProfileRepository.findByAppUser_Username(principal.getName())
                .orElseThrow(() -> new BadUserCredentialsException("User with username" + principal.getName() + " not found"));
    }

    public void deleteVolunteerProfile(Principal principal) {
        if (!volunteerProfileRepository.existsByAppUser_Username(principal.getName())) {
            throw new DataNotFoundException("Volunteer profile with username " + principal.getName() + " not found");
        }
        currentUserFacade.deleteAppUserByPrincipal(principal);
    }
}
