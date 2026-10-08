package com.volunnear.mapper;

import com.volunnear.dto.request.OrganizationUpdateProfileRequestDto;
import com.volunnear.dto.request.VolunteeringActivityCreationRequest;
import com.volunnear.entity.activity.VolunteeringActivity;
import com.volunnear.entity.profile.Organization;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.PrecisionModel;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.time.LocalDateTime;

@Mapper(componentModel = "spring", imports = {LocalDateTime.class, GeometryFactory.class})
public interface VolunteeringActivityMapper {

    @Mapping(target = "location", source = "request", qualifiedByName = "toPoint")
    VolunteeringActivity toEntity(VolunteeringActivityCreationRequest request, Organization organization);

    @Named("toPoint")
    default Point mapLocation (VolunteeringActivityCreationRequest dto) {
        if (dto.lat() == null || dto.lat() == 0) {
            return null;
        }
        GeometryFactory geometryFactory = new GeometryFactory(new PrecisionModel(), 4326);
        return geometryFactory.createPoint(new Coordinate(dto.lon(), dto.lat()));
    }
}
