package com.volunnear.entity.activity;

import com.volunnear.ActivityRequestStatus;
import com.volunnear.entity.profile.VolunteerProfile;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.proxy.HibernateProxy;

import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Getter
@Setter
@ToString
@Table(name = "volunteer_activity")
@IdClass(VolunteerActivityId.class)
public class VolunteerActivity {
    @Id
    @ManyToOne
    @JoinColumn(name = "volunteer_id", nullable = false)
    private VolunteerProfile volunteer;
    @Id
    @ManyToOne
    @JoinColumn(name = "activity_id", nullable = false)
    private Activity activity;

    @Column(name = "joined_at")
    private LocalDateTime joinedAt;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private ActivityRequestStatus status;

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        Class<?> oEffectiveClass = o instanceof HibernateProxy ? ((HibernateProxy) o).getHibernateLazyInitializer().getPersistentClass() : o.getClass();
        Class<?> thisEffectiveClass = this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass() : this.getClass();
        if (thisEffectiveClass != oEffectiveClass) return false;
        VolunteerActivity that = (VolunteerActivity) o;
        return getVolunteer() != null && Objects.equals(getVolunteer(), that.getVolunteer())
                && getActivity() != null && Objects.equals(getActivity(), that.getActivity());
    }

    @Override
    public final int hashCode() {
        return Objects.hash(volunteer, activity);
    }
}
