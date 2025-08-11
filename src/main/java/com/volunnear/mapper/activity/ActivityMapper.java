package com.volunnear.mapper.activity;

import com.volunnear.dto.request.activity.ActivitySaveRequestDTO;
import com.volunnear.dto.response.activity.ActivityCardDTO;
import com.volunnear.dto.response.activity.ActivityResponseDTO;
import com.volunnear.entity.activity.Activity;
import com.volunnear.entity.profile.OrganizationProfile;
import com.volunnear.mapper.profile.OrganizationProfileMapper;
import com.volunnear.mapper.user.AppUserMapper;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.mapstruct.*;

import java.time.LocalDateTime;

@Mapper(componentModel = "spring", imports = {OrganizationProfileMapper.class, AppUserMapper.class, LocalDateTime.class, GeometryFactory.class})
public interface ActivityMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "startedAt", expression = "java(LocalDateTime.now())")
    @Mapping(target = "location", source = "point")
    @Mapping(target = "city", source = "dto.location.city")
    @Mapping(target = "country", source = "dto.location.country")
    @Mapping(target = "address", source = "dto.location.address")
    @Mapping(target = "description", source = "dto.description")
    Activity toEntity(ActivitySaveRequestDTO dto, OrganizationProfile organizationProfile, Point point, @Context GeometryFactory geometryFactory);

    @Mapping(target = "location", source = "point")
    @Mapping(target = "city", source = "dto.location.city")
    @Mapping(target = "country", source = "dto.location.country")
    @Mapping(target = "address", source = "dto.location.address")
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntity(ActivitySaveRequestDTO dto, Point point, @MappingTarget Activity activity, @Context GeometryFactory geometryFactory);

    @Mapping(target = "organizationId", source = "organizationProfile.id")
    ActivityResponseDTO toDto(Activity activity);

    @Mapping(target = "organizationId", source = "organizationProfile.id")
    ActivityCardDTO toCardDto(Activity activity);
}
