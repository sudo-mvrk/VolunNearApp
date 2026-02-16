package com.volunnear.service.profile;

import com.volunnear.dto.request.profile.OrganizationProfileSaveRequestDTO;
import com.volunnear.dto.response.profile.OrganizationProfileResponseDTO;
import com.volunnear.entity.profile.OrganizationProfile;
import com.volunnear.entity.users.AppUser;
import com.volunnear.exception.DataNotFoundException;
import com.volunnear.mapper.profile.OrganizationProfileMapper;
import com.volunnear.repository.profile.OrganizationProfileRepository;
import com.volunnear.service.user.CurrentUserFacade;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.Principal;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrganizationService {
    private final CurrentUserFacade currentUserFacade;
    private final OrganizationProfileMapper organizationProfileMapper;
    private final OrganizationProfileRepository organizationProfileRepository;

    @Transactional
    public OrganizationProfileResponseDTO updateOrganizationProfile(OrganizationProfileSaveRequestDTO editRequest, Principal principal) {
        OrganizationProfile profile = getOrganizationProfileByPrincipal(principal);
        organizationProfileMapper.updateEntity(editRequest, profile);
        organizationProfileRepository.save(profile);
        return organizationProfileMapper.toDto(profile);
    }

    @Transactional(readOnly = true)
    public OrganizationProfileResponseDTO getOrganizationProfile(Principal principal) {
        return organizationProfileMapper.toDto(getOrganizationProfileByPrincipal(principal));
    }

    @Transactional(readOnly = true)
    public OrganizationProfileResponseDTO getOrganizationProfileById(Long id) {
        OrganizationProfile organizationProfile = organizationProfileRepository.findById(id)
                .orElseThrow(() -> new DataNotFoundException("User with id " + id + " not found"));
        return organizationProfileMapper.toDto(organizationProfile);
    }

    @Transactional
    public void deleteOrganizationProfile(Principal principal) {
        if (!organizationProfileRepository.existsByAppUser_Username(principal.getName())) {
            throw new DataNotFoundException("Organization profile with username " + principal.getName() + " not found");
        }
        currentUserFacade.deleteAppUserByPrincipal(principal);
    }

    @Transactional(readOnly = true)
    public OrganizationProfile getOrganizationProfileByPrincipal(Principal principal) {
        AppUser appUser = currentUserFacade.getUserFromPrincipal(principal);
        return organizationProfileRepository.findByAppUser_Username(appUser.getUsername())
                .orElseThrow(() -> new DataNotFoundException("Organization profile with username " + appUser.getUsername() + " not found"));
    }
}
