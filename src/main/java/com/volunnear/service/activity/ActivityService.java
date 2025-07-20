package com.volunnear.service.activity;

import com.volunnear.dto.request.activity.ActivitySaveRequestDTO;
import com.volunnear.dto.response.PagedResponseDTO;
import com.volunnear.dto.response.activity.ActivityCardDTO;
import com.volunnear.dto.response.activity.ActivityResponseDTO;
import com.volunnear.entity.activity.Activity;
import com.volunnear.entity.profile.OrganizationProfile;
import com.volunnear.exception.DataNotFoundException;
import com.volunnear.mapper.PaginationMapper;
import com.volunnear.mapper.activity.ActivityMapper;
import com.volunnear.repository.activity.ActivityRepository;
import com.volunnear.service.GeoService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.List;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class ActivityService {
    private final GeoService geoService;
    private final ActivityMapper activityMapper;
    private final GeometryFactory geometryFactory;
    private final ActivityRepository activityRepository;
    private final PaginationMapper paginationMapper;

    public ActivityResponseDTO createActivity(ActivitySaveRequestDTO requestDTO, OrganizationProfile organizationProfile) {
        Point point = resolvePointFromRequest(requestDTO);
        Activity activity = activityMapper.toEntity(requestDTO, organizationProfile, point, geometryFactory);
        activity.setOrganizationProfile(organizationProfile);
        activityRepository.save(activity);
        return activityMapper.toDto(activity);
    }

    public ActivityResponseDTO updateActivity(ActivitySaveRequestDTO requestDTO, Long id, Principal principal) {
        Point point = resolvePointFromRequest(requestDTO);
        Activity activity = activityRepository.findByIdAndOrganizationProfile_AppUser_Username(id, principal.getName())
                .orElseThrow(() -> new DataNotFoundException("Activity with id: " + id + " not found"));
        activityMapper.updateEntity(requestDTO, point, activity, geometryFactory);
        activityRepository.save(activity);
        return activityMapper.toDto(activity);
    }

    public ActivityResponseDTO getActivityById(Long id) {
        Activity activity = activityRepository.findActivityById(id)
                .orElseThrow(() -> new DataNotFoundException("Activity with id: " + id + " not found"));
        return activityMapper.toDto(activity);
    }

    public PagedResponseDTO<ActivityCardDTO> getActivitiesByOrganizationId(Pageable pageable, Long organizationId) {
        Page<Activity> page = activityRepository.findAllByOrganizationProfile_Id(organizationId, pageable);
        return paginationMapper.mapPage(page, activityMapper::toCardDto);
    }

    public PagedResponseDTO<ActivityCardDTO> getActivitiesByPrincipal(Pageable pageable, Principal principal) {
        Page<Activity> page = activityRepository.findAllByOrganizationProfile_AppUser_Username(principal.getName(), pageable);
        return paginationMapper.mapPage(page, activityMapper::toCardDto);
    }

    public PagedResponseDTO<ActivityCardDTO> getAllActivities(Pageable pageable) {
        Page<Activity> page = activityRepository.findAll(pageable);
        return paginationMapper.mapPage(page, activityMapper::toCardDto);
    }

    public PagedResponseDTO<ActivityCardDTO> getAllActivitiesByIdIn(Pageable pageable, List<Long> activitiesIds) {
        Page<Activity> page = activityRepository.findAllByIdIn(activitiesIds, pageable);
        return paginationMapper.mapPage(page, activityMapper::toCardDto);
    }

    public void deleteActivityById(Long id, Principal principal) {
        if (activityRepository.deleteByIdAndOrganizationProfile_AppUser_Username(id, principal.getName()) == 0) {
            throw new DataNotFoundException("Activity not found or not owned by user");
        }
    }

    public boolean existsActivityById(Long id) {
        return activityRepository.existsById(id);
    }

    private Point resolvePointFromRequest(ActivitySaveRequestDTO dto) {
        return geoService.geocode(dto.getLocation());
    }
}
