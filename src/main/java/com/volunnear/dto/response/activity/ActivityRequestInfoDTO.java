package com.volunnear.dto.response.activity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ActivityRequestInfoDTO {
    private String status;
    private Long activityId;
    private String activityTitle;
}
