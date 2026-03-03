package com.volunnear.mapper;

import com.volunnear.dto.request.VolunteerRegistrationRequestDto;
import com.volunnear.dto.response.AppUserResponseDto;
import com.volunnear.entity.enums.Role;
import com.volunnear.entity.user.AppUser;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.HashSet;
import java.util.Set;

@Mapper(componentModel = "spring", imports = {HashSet.class, Set.class})
public interface AppUserMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "roles", source = "role")
    AppUser toEntity(VolunteerRegistrationRequestDto volunteerRegistrationRequestDto, Role role);

    @Mapping(target = "roles", source = "roles")
    AppUserResponseDto toDto(AppUser appUser);

    default Set<Role> mapRoleToSet(Role role) {
        if (role == null) {
            return new HashSet<>();
        }
        return new HashSet<>(Set.of(role));
    }
}
