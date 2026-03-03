package com.volunnear.mapper;

import com.volunnear.dto.request.VolunteerRegistrationRequestDto;
import com.volunnear.entity.profile.Volunteer;
import com.volunnear.entity.user.AppUser;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface VolunteerMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "appUser", source = "appUser")
    @Mapping(target = "dateOfBirth", source = "requestDto.dateOfBirth")
    Volunteer toEntity(VolunteerRegistrationRequestDto requestDto, AppUser appUser);
}
