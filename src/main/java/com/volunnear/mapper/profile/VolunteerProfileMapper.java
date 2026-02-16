package com.volunnear.mapper.profile;

import com.volunnear.dto.geoInfo.AddressDTO;
import com.volunnear.dto.request.profile.VolunteerProfileSaveRequestDTO;
import com.volunnear.dto.request.user.RegisterVolunteerUserProfileRequest;
import com.volunnear.dto.response.profile.VolunteerProfileResponseDTO;
import com.volunnear.entity.profile.VolunteerProfile;
import com.volunnear.entity.users.AppUser;
import com.volunnear.mapper.user.AppUserMapper;
import com.volunnear.service.GeoService;
import org.mapstruct.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;


@Mapper(componentModel = "spring", imports = {LocalDate.class, AppUserMapper.class, GeoService.class})
public abstract class VolunteerProfileMapper {
    @Autowired
    protected GeoService geoService;

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "appUser", source = "appUser")
    public abstract VolunteerProfile toEntity(RegisterVolunteerUserProfileRequest dto, AppUser appUser);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    public abstract void updateEntity(VolunteerProfileSaveRequestDTO dto, @MappingTarget VolunteerProfile entity);
    @AfterMapping
    protected void enrichWithGeoData(VolunteerProfileSaveRequestDTO dto, @MappingTarget VolunteerProfile entity) {
        if (dto.getLatitude() != null && dto.getLongitude() != null) {
            try {
                AddressDTO addressInfo = geoService.reverseGeocode(dto.getLatitude(), dto.getLongitude());

                entity.setAddress(addressInfo.getAddress());
                entity.setCity(addressInfo.getCity());
                entity.setCountry(addressInfo.getCountry());

                entity.setLatitude(dto.getLatitude());
                entity.setLongitude(dto.getLongitude());

            } catch (Exception e) {
                System.err.println("Geocoding failed inside mapper: " + e.getMessage());
            }
        }
    }

    @Mapping(target = "email", source = "volunteerProfile.appUser.email")
    @Mapping(target = "username", source = "volunteerProfile.appUser.username")
    @Mapping(target = "created", source = "volunteerProfile.appUser.created")
    public abstract VolunteerProfileResponseDTO toDto(VolunteerProfile volunteerProfile);
}