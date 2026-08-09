package com.volunnear.service;

import com.volunnear.dto.request.OrganizationRegistrationRequestDto;
import com.volunnear.dto.request.OrganizationUpdateProfileRequestDto;
import com.volunnear.dto.response.profile.OrganizationProfileResponseDto;
import com.volunnear.entity.profile.Organization;
import com.volunnear.entity.user.AppUser;
import com.volunnear.mapper.OrganizationMapper;
import com.volunnear.repository.OrganizationRepository;
import com.volunnear.security.detail.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
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

    @Transactional
    public OrganizationProfileResponseDto updateOrganizationProfile(OrganizationUpdateProfileRequestDto request, CustomUserDetails userDetails) {
        Organization organization = organizationRepository.findOrganizationByAppUser_Username(userDetails.getUsername())
                .orElseThrow(() -> new UsernameNotFoundException("User with username " + userDetails.getUsername() + " not found"));
        organizationMapper.updateEntity(request, organization);
        return organizationMapper.toDto(organization);
    }
}
