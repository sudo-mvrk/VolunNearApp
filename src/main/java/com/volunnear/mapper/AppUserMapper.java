package com.volunnear.mapper;

import com.volunnear.dto.response.AppUserResponseDto;
import com.volunnear.entity.user.AppUser;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.HashSet;
import java.util.Set;

@Mapper(componentModel = "spring", imports = {HashSet.class, Set.class})
public interface AppUserMapper {

    @Mapping(target = "roles", source = "roles")
    AppUserResponseDto toDto(AppUser appUser);
}
