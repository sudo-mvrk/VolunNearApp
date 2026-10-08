package com.volunnear.organization.dto;

public record OrganizationProfileResponseDto (
        String username,
        String organizationName,
        String description,
        String websiteUrl,
        String city,
        String fullAddress,
        String countryCode
) {
}
