package com.volunnear.mapper.profile;

import com.volunnear.dto.request.profile.OrganizationProfileSaveRequestDTO;
import com.volunnear.dto.request.user.RegisterOrganizationUserProfileRequest;
import com.volunnear.dto.response.profile.OrganizationProfileResponseDTO;
import com.volunnear.entity.profile.OrganizationProfile;
import com.volunnear.entity.users.AppUser;
import com.volunnear.mapper.user.AppUserMapper;
import org.mapstruct.*;

import java.time.LocalDate;
import java.util.List;

@Mapper(componentModel = "spring", imports = {LocalDate.class, AppUserMapper.class, List.class})
public interface OrganizationProfileMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "appUser", source = "appUser")
    OrganizationProfile toEntity(RegisterOrganizationUserProfileRequest dto, AppUser appUser);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntity(OrganizationProfileSaveRequestDTO dto, @MappingTarget OrganizationProfile profile);


    @Mapping(target = "email", source = "organizationProfile.appUser.email")
    @Mapping(target = "username", source = "organizationProfile.appUser.username")
    @Mapping(target = "created", source = "organizationProfile.appUser.created")
    OrganizationProfileResponseDTO toDto(OrganizationProfile organizationProfile);
}
