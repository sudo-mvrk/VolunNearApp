package com.volunnear.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;


public record VolunteerRegistrationRequestDto(
        @Size(min = 3, max = 25)
        @NotBlank
        String username,
        @Size(min = 4, max = 35)
        @NotBlank
        String password,
        @Email
        @NotBlank
        String email,
        @NotBlank
        String firstName,
        @NotBlank
        String lastName,
        LocalDate dateOfBirth
) {
}
