package com.volunnear.service;

import com.volunnear.client.NominatimClient;
import com.volunnear.dto.geoInfo.AddressDTO;
import com.volunnear.dto.geoInfo.GeoCodingResponse;
import com.volunnear.exception.GeoCodingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;


@Slf4j
@Service
@RequiredArgsConstructor
public class GeoService {
    private final NominatimClient nominatimClient;
    private final GeometryFactory geometryFactory;

    public Point geocode(AddressDTO addressDTO) {
        List<GeoCodingResponse> responses = nominatimClient.geocode(
                addressDTO.toQuery(),
                "json",
                1
        );

        if (responses == null || responses.isEmpty()) {
            throw new GeoCodingException("No location found for: " + addressDTO.toQuery());
        }

        GeoCodingResponse response = responses.getFirst();
        double lat = Double.parseDouble(response.lat());
        double lon = Double.parseDouble(response.lon());

        return geometryFactory.createPoint(new Coordinate(lon, lat));
    }
    public AddressDTO reverseGeocode(double lat, double lon) {
        log.info("Reverse geocoding for lat: {}, lon: {}", lat, lon);

        try {
            GeoCodingResponse response = nominatimClient.reverse(lat, lon, "json");

            if (response == null || response.address() == null) {
                throw new GeoCodingException("Address not found for coordinates: " + lat + ", " + lon);
            }

            GeoCodingResponse.AddressDetails details = response.address();

            String city =  details.city();
            if (!StringUtils.hasText(city)) city = details.town();
            if (!StringUtils.hasText(city)) city = details.village();

            String streetAddress = details.road();
            if (StringUtils.hasText(details.house_number())) {
                streetAddress += ", " + details.house_number();
            }

            return AddressDTO.builder()
                    .country(details.country())
                    .city(city)
                    .address(streetAddress != null ? streetAddress : response.displayName())
                    .latitude(lat)
                    .longitude(lon)
                    .build();

        } catch (Exception e) {
            log.error("Error during reverse geocoding", e);
            return AddressDTO.builder()
                    .latitude(lat)
                    .longitude(lon)
                    .address("Unknown location")
                    .build();
        }
    }
}
