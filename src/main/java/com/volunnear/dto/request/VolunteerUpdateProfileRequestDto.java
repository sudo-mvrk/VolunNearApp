package com.volunnear.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Past;
import lombok.Builder;

import java.time.LocalDate;
@Builder
public record VolunteerUpdateProfileRequestDto(
        String firstName,
        String lastName,
        @Past(message = "Date of birth should be in the past")
        LocalDate dateOfBirth,
        String bio,
        @Min(value = -90, message = "Latitude can't be less than -90")
        @Max(value = 90, message = "Latitude can't be more than 90")
        Double lat,
        @Min(value = -180, message = "Longitude can't be less than -180")
        @Max(value = 180, message = "Longitude can't be more than 180")
        Double lon,
        String locationName,
        Integer radius

) {
}
