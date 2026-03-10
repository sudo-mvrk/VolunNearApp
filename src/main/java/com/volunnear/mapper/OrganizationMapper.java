package com.volunnear.mapper;

import com.volunnear.dto.request.OrganizationRegistrationRequestDto;
import com.volunnear.entity.profile.Organization;
import com.volunnear.entity.user.AppUser;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface OrganizationMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "appUser", source = "appUser")
    Organization toEntity(OrganizationRegistrationRequestDto requestDto, AppUser appUser);
}
