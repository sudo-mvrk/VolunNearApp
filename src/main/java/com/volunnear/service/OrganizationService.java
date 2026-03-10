package com.volunnear.service;

import com.volunnear.dto.request.OrganizationRegistrationRequestDto;
import com.volunnear.entity.profile.Organization;
import com.volunnear.entity.user.AppUser;
import com.volunnear.mapper.OrganizationMapper;
import com.volunnear.repository.OrganizationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class OrganizationService {
    private final OrganizationMapper organizationMapper;
    private final OrganizationRepository organizationRepository;

    @Transactional
    public void createOrganizationProfile(OrganizationRegistrationRequestDto request, AppUser appUser) {
        Organization organization = organizationMapper.toEntity(request, appUser);
        organizationRepository.save(organization);
    }
}
