package com.volunnear.dto.response.profile;

import com.volunnear.SkillType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.Set;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class VolunteerProfileResponseDTO {
    private String email;
    private String username;
    private String firstName;
    private String lastName;
    private String middleName;
    private String about;
    private LocalDate birthday;
    private LocalDate created;
    private String phone;
    private Set<SkillType> skills;
    private Double rating;
    private Double latitude;
    private Double longitude;
    private String address;
    private String city;
    private String country;
}
