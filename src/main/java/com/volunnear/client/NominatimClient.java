package com.volunnear.client;

import com.volunnear.config.NominatimFeignConfig;
import com.volunnear.dto.geoInfo.GeoCodingResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(
        name = "nominatimClient",
        url = "https://nominatim.openstreetmap.org",
        configuration = NominatimFeignConfig.class
)
public interface NominatimClient {
    @GetMapping(value = "/search", produces = "application/json")
    List<GeoCodingResponse> geocode(
            @RequestParam("q") String query,
            @RequestParam("format") String format,
            @RequestParam("limit") int limit
    );
    @GetMapping(value = "/reverse", produces = "application/json")
    GeoCodingResponse reverse(
            @RequestParam("lat") double lat,
            @RequestParam("lon") double lon,
            @RequestParam("format") String format
    );
}
