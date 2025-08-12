package com.volunnear.mapper.activity;

import com.volunnear.dto.response.ParticipantCardDTO;
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
    @Mapping(target = "volunteerId", source = "volunteer.id")
    @Mapping(target = "firstName", source = "volunteer.firstName")
    @Mapping(target = "lastName", source = "volunteer.lastName")
    @Mapping(target = "middleName", source = "volunteer.middleName")
    @Mapping(target = "email", source = "volunteer.appUser.email")
    @Mapping(target = "username", source = "volunteer.appUser.username")
    ParticipantCardDTO toVolunteerCardDto(VolunteerActivity volunteerActivity);
}
