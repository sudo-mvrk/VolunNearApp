package com.volunnear.organization.dto;

public record OrganizationUpdateProfileRequestDto (
        String organizationName,
        String description,
        String websiteUrl,
        Double lat,
        Double lon,
        String city,
        String fullAddress,
        String countryCode,
        String locationDescription
){
}
