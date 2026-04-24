package com.volunnear.entity.profile;

import com.volunnear.entity.user.AppUser;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.locationtech.jts.geom.Point;

import java.time.LocalDate;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "volunteers")
public class Volunteer {
    @Id
    private Long id;
    @MapsId
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private AppUser appUser;
    private String firstName;
    private String lastName;
    @Column(columnDefinition = "DATE")
    private LocalDate dateOfBirth;
    @Lob
    private String bio;
    private Point location;
    private String locationName;
    private Integer radius;
}
