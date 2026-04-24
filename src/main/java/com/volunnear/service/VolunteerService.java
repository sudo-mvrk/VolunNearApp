package com.volunnear.service;

import com.volunnear.dto.request.VolunteerRegistrationRequestDto;
import com.volunnear.dto.request.VolunteerUpdateProfileRequestDto;
import com.volunnear.dto.response.profile.VolunteerProfileResponseDto;
import com.volunnear.entity.profile.Volunteer;
import com.volunnear.entity.user.AppUser;
import com.volunnear.mapper.VolunteerMapper;
import com.volunnear.repository.VolunteerRepository;
import com.volunnear.security.detail.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class VolunteerService {
    private final VolunteerMapper volunteerMapper;
    private final VolunteerRepository volunteerRepository;

    @Transactional
    public void createVolunteerProfile(VolunteerRegistrationRequestDto request, AppUser appUser) {
        Volunteer volunteer = volunteerMapper.toEntity(request, appUser);
        volunteerRepository.save(volunteer);
    }

    @Transactional
    public VolunteerProfileResponseDto updateVolunteerProfile (VolunteerUpdateProfileRequestDto request, CustomUserDetails userDetails) {
        Volunteer volunteer = volunteerRepository.findByAppUser_Username(userDetails.getUsername())
                .orElseThrow(() -> new UsernameNotFoundException("User with username " + userDetails.getUsername() + " not found. Try re-login"));
        Volunteer updatedEntity = volunteerMapper.updateEntity(request, volunteer);
        return volunteerMapper.toDto(updatedEntity);
    }
}
