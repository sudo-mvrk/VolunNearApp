package com.volunnear.dto.response.profile;

import lombok.Builder;

import java.time.LocalDate;

@Builder
public record VolunteerProfileResponseDto (
   Long id,
   String username,
   String firstName,
   String lastName,
   LocalDate datoOfBirth,
   String bio,
   String locationName,
   String countryCode,
   String city,
   String region,
   Integer radius
) {
}
