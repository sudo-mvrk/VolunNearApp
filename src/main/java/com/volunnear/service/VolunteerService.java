package com.volunnear.service;

import com.volunnear.dto.request.VolunteerRegistrationRequestDto;
import com.volunnear.entity.profile.Volunteer;
import com.volunnear.entity.user.AppUser;
import com.volunnear.mapper.VolunteerMapper;
import com.volunnear.repository.VolunteerRepository;
import lombok.RequiredArgsConstructor;
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
}
