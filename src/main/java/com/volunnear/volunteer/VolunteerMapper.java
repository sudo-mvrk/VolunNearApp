package com.volunnear.volunteer;

import com.volunnear.volunteer.dto.VolunteerRegistrationRequestDto;
import com.volunnear.volunteer.dto.VolunteerUpdateProfileRequestDto;
import com.volunnear.volunteer.dto.VolunteerProfileResponseDto;
import com.volunnear.auth.AppUser;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.PrecisionModel;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;

@Mapper(componentModel = "spring")
public interface VolunteerMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "appUser", source = "appUser")
    @Mapping(target = "dateOfBirth", source = "requestDto.dateOfBirth")
    Volunteer toEntity(VolunteerRegistrationRequestDto requestDto, AppUser appUser);

    @Mapping(target = "location", source = "requestDto", qualifiedByName = "toPoint")
    Volunteer updateEntity(VolunteerUpdateProfileRequestDto requestDto, @MappingTarget Volunteer volunteer);

    @Mapping(target = "username", source = "appUser.username")
    VolunteerProfileResponseDto toDto(Volunteer volunteer);

    @Named("toPoint")
    default Point mapLocation (VolunteerUpdateProfileRequestDto dto) {
        if (dto.lat() == null || dto.lat() == 0) {
            return null;
        }
        GeometryFactory geometryFactory = new GeometryFactory(new PrecisionModel(), 4326);

        return geometryFactory.createPoint(new Coordinate(dto.lon(), dto.lat()));
    }
}
