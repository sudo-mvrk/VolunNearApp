package com.volunnear.entity.activity;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Embeddable
@NoArgsConstructor
@AllArgsConstructor
public class VolunteerActivityId {
    private Long volunteer;
    private Long activity;
}
