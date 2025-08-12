package com.volunnear.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ParticipantCardDTO {
    private Long volunteerId;
    private String firstName;
    private String middleName;
    private String lastName;
    private String email;
    private String username;
    private String status;
    private String joinedAt;
}
