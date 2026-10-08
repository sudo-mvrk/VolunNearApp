package com.volunnear.organization;

import com.volunnear.organization.dto.OrganizationRegistrationRequestDto;
import com.volunnear.organization.dto.OrganizationUpdateProfileRequestDto;
import com.volunnear.organization.dto.OrganizationProfileResponseDto;
import com.volunnear.auth.AppUser;
import com.volunnear.common.exception.DataNotFoundException;
import com.volunnear.auth.CustomUserDetails;
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
                .orElseThrow(() -> throwUsernameNotFoundException(userDetails.getUsername()));
        organizationMapper.updateEntity(request, organization);
        return organizationMapper.toDto(organization);
    }

    public Organization getOrganizationByUsername(CustomUserDetails userDetails) {
        return organizationRepository.findOrganizationByAppUser_Username(userDetails.getUsername())
                .orElseThrow(() -> throwUsernameNotFoundException(userDetails.getUsername()));
    }

    private UsernameNotFoundException throwUsernameNotFoundException(String username) {
        return new UsernameNotFoundException("User with username " + username + " not found");
    }
}
