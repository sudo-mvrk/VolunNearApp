package com.volunnear.service.user;

import com.volunnear.dto.request.OrganizationRegistrationRequestDto;
import com.volunnear.dto.request.OrganizationUpdateProfileRequestDto;
import com.volunnear.dto.response.profile.OrganizationProfileResponseDto;
import com.volunnear.entity.profile.Organization;
import com.volunnear.entity.user.AppUser;
import com.volunnear.exception.DataNotFoundException;
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
