package com.volunnear.dto.response.activtiy;

import com.volunnear.entity.enums.ActivityStatus;
import com.volunnear.entity.enums.Priority;

import java.time.LocalDateTime;

public record VolunteeringActivityResponseDto(
        Long id,
        String title,
        String shortDescription,
        String fullDescription,
        Priority priority,
        Integer capacity,
        LocalDateTime updatedAt,
        ActivityStatus activityStatus
) {
}
