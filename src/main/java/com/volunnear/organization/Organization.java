package com.volunnear.organization;

import com.volunnear.auth.AppUser;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.locationtech.jts.geom.Point;

@Getter
@Setter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "organizations")
public class Organization {
    @Id
    private Long id;
    @MapsId
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id", unique = true)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private AppUser appUser;
    @Column(nullable = false)
    private String organizationName;
    @Lob
    private String description;
    private String websiteUrl;
    private Point location;
    private String city;
    private String fullAddress;
    private String countryCode;
}
