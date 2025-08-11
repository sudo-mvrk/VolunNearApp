package com.volunnear.mapper.activity;

import com.volunnear.dto.response.activity.ActivityRequestInfoDTO;
import com.volunnear.entity.activity.VolunteerActivity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.time.LocalDateTime;

@Mapper(componentModel = "spring", imports = {ActivityMapper.class, LocalDateTime.class})
public interface VolunteerActivityMapper {
    @Mapping(target = "status", source = "status")
    @Mapping(target = "activityId", source = "activity.id")
    @Mapping(target = "activityTitle", source = "activity.title")
    ActivityRequestInfoDTO toDto (VolunteerActivity volunteerActivity);
}
