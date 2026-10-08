package com.volunnear.volunteer;

import com.volunnear.volunteer.dto.VolunteerRegistrationRequestDto;
import com.volunnear.volunteer.dto.VolunteerUpdateProfileRequestDto;
import com.volunnear.volunteer.dto.VolunteerProfileResponseDto;
import com.volunnear.auth.AppUser;
import com.volunnear.auth.CustomUserDetails;
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
        volunteerMapper.updateEntity(request, volunteer);
        return volunteerMapper.toDto(volunteer);
    }
}
