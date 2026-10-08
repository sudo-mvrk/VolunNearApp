package com.volunnear.service.activity;

import com.volunnear.dto.request.VolunteeringActivityCreationRequest;
import com.volunnear.dto.response.activtiy.VolunteeringActivityResponseDto;
import com.volunnear.entity.activity.VolunteeringActivity;
import com.volunnear.entity.profile.Organization;
import com.volunnear.mapper.VolunteeringActivityMapper;
import com.volunnear.repository.VolunteeringActivityRepository;
import com.volunnear.security.detail.CustomUserDetails;
import com.volunnear.service.user.OrganizationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class VolunteeringActivityService {
    private OrganizationService organizationService;
    private VolunteeringActivityMapper volunteeringActivityMapper;
    private VolunteeringActivityRepository volunteeringActivityRepository;

    @Transactional
    public VolunteeringActivityResponseDto createVolunteeringActivity(VolunteeringActivityCreationRequest request,
                                                                      CustomUserDetails userDetails) {
        Organization organizationByUsername = organizationService.getOrganizationByUsername(userDetails);
        VolunteeringActivity entity = volunteeringActivityMapper.toEntity(request, organizationByUsername);
        System.out.println(entity);
        return null;
    }

}
