package com.volunnear.dto.request.user;

import com.volunnear.annotation.ValidPhoneNumber;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class RegisterOrganizationUserProfileRequest extends RegisterAppUserDTO{
    @NotBlank
    private String organizationName;
    @NotBlank
    private String country;
    @NotBlank
    private String city;
    @NotBlank
    private String address;
    @NotBlank
    @ValidPhoneNumber
    private String phone;
}
