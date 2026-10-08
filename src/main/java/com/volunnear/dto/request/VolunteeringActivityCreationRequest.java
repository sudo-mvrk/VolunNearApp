package com.volunnear.dto.request;

import com.volunnear.entity.enums.ActivityStatus;
import com.volunnear.entity.enums.Priority;
import jakarta.validation.constraints.NotBlank;

public record VolunteeringActivityCreationRequest(
    @NotBlank
    String title,
    @NotBlank
    String shortDescription,
    String fullDescription,
    Priority priority,
    Integer capacity,
    Long lat,
    Long lon,
    ActivityStatus activityStatus
) {
}
