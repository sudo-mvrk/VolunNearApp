package com.volunnear.dto.geoInfo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

public record GeoCodingResponse(@JsonProperty("lat") String lat,
                                @JsonProperty("lon") String lon,
                                @JsonProperty("display_name") String displayName,
                                AddressDetails address) {
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record AddressDetails(
            String road,
            String city,
            String town,
            String village,
            String country,
            String house_number,
            String postcode
    ) {}
}
