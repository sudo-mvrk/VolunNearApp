package com.volunnear.util;

import com.volunnear.ActivityRequestStatus;
import com.volunnear.exception.BadDataInRequestException;

public class ActivityStatusParser {
    public ActivityStatusParser() {
        throw new UnsupportedOperationException("Utility class");
    }

    public static ActivityRequestStatus parse(String status) {
        try {
            return ActivityRequestStatus.valueOf(status.trim().toUpperCase());
        } catch (IllegalArgumentException | NullPointerException e) {
            throw new BadDataInRequestException("Invalid status value: " + status);
        }
    }
}
