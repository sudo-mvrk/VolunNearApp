package com.volunnear.organization;

import com.volunnear.organization.dto.OrganizationRegistrationRequestDto;
import com.volunnear.organization.dto.OrganizationUpdateProfileRequestDto;
import com.volunnear.organization.dto.OrganizationProfileResponseDto;
import com.volunnear.auth.AppUser;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.PrecisionModel;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface OrganizationMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "appUser", source = "appUser")
    Organization toEntity(OrganizationRegistrationRequestDto requestDto, AppUser appUser);

    @Mapping(target = "username", source = "appUser.username")
    OrganizationProfileResponseDto toDto(Organization organization);

    @Mapping(target = "location", source = "requestDto", qualifiedByName = "toPoint")
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
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
