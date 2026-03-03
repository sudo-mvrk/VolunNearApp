package com.volunnear.entity.profile;

import com.volunnear.entity.user.AppUser;
import jakarta.persistence.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Entity
@Table(name = "organizations")
// TODO: findo out fields needed for organization
public class Organization {
    @Id
    private Long id;
    @MapsId
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id", unique = true)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private AppUser appUser;
    private String organizationName;
}
