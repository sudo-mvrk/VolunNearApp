package com.volunnear.mapper;

import com.volunnear.dto.request.OrganizationRegistrationRequestDto;
import com.volunnear.dto.request.OrganizationUpdateProfileRequestDto;
import com.volunnear.dto.response.profile.OrganizationProfileResponseDto;
import com.volunnear.entity.profile.Organization;
import com.volunnear.entity.user.AppUser;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.PrecisionModel;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;

@Mapper(componentModel = "spring")
public interface OrganizationMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "appUser", source = "appUser")
    Organization toEntity(OrganizationRegistrationRequestDto requestDto, AppUser appUser);

    @Mapping(target = "username", source = "appUser.username")
    OrganizationProfileResponseDto toDto(Organization organization);

    @Mapping(target = "location", source = "requestDto", qualifiedByName = "toPoint")
    Organization updateEntity(OrganizationUpdateProfileRequestDto requestDto, @MappingTarget Organization organization);

    @Named("toPoint")
    default Point mapLocation (OrganizationUpdateProfileRequestDto dto) {
        if (dto.lat() == null || dto.lat() == 0) {
            return null;
        }
        GeometryFactory geometryFactory = new GeometryFactory(new PrecisionModel(), 4326);

        return geometryFactory.createPoint(new Coordinate(dto.lon(), dto.lat()));
    }
}
