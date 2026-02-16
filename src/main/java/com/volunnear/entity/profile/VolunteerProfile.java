package com.volunnear.entity.profile;

import com.volunnear.SkillType;
import com.volunnear.entity.users.AppUser;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@Entity
@ToString
@Table(name = "volunteer_profile")
public class VolunteerProfile {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @ToString.Exclude
    @JoinColumn(name = "app_user_id", nullable = false, unique = true)
    private AppUser appUser;

    @Column(name = "first_name", nullable = false)
    private String firstName;

    @Column(name = "last_name", nullable = false)
    private String lastName;

    @Column(name = "middle_name", nullable = false)
    private String middleName;

    @Column(name = "about")
    private String about;

    @Column(name = "birthday", nullable = false)
    private LocalDate birthday;

    @Column(name = "phone_number", nullable = false)
    private String phone;

    @Column(name = "rating")
    private Double rating = 0.0;

    @Column(name = "is_busy")
    private Boolean isBusy = false;
    private Double latitude;
    private Double longitude;
    private String address;
    private String country;
    private String city;

    @ElementCollection(targetClass = SkillType.class, fetch = FetchType.EAGER)
    @CollectionTable(name = "volunteer_skills", joinColumns = @JoinColumn(name = "volunteer_id"))
    @Enumerated(EnumType.STRING)
    @Column(name = "skill")
    private Set<SkillType> skills = new HashSet<>();
}