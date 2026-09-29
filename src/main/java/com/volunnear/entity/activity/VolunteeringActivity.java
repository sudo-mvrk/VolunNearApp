package com.volunnear.entity.activity;

import com.volunnear.entity.enums.ActivityStatus;
import com.volunnear.entity.enums.Priority;
import com.volunnear.entity.profile.Organization;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "volunteering_activity")
public class VolunteeringActivity {
    //- organization_id (FK -> Organization)
    //- schedule (Datetime range / Schedule details)
    @Id
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "organization_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Organization organizationId;
    @Column(name = "activity_title", nullable = false)
    private String title;
    @Column(name = "short_description", nullable = false)
    private String shortDescription;
    @Lob
    @Column(name = "full_description")
    private String fullDescription;
    @Enumerated(EnumType.STRING)
    private Priority priority;
    // TODO: Implement Schedule
    @Column(nullable = false)
    private Integer capacity;
    @Column(name = "updated_at", nullable = false)
    @UpdateTimestamp
    private LocalDateTime updatedAt;
    @Column(name = "activity_status", nullable = false)
    private ActivityStatus activityStatus;
}
