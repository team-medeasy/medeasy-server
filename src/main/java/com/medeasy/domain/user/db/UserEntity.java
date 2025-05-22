package com.medeasy.domain.user.db;

import com.medeasy.domain.routine.db.RoutineEntity;
import com.medeasy.domain.routine_group.db.RoutineGroupEntity;
import com.medeasy.domain.user_care_mapping.db.UserCareMappingEntity;
import com.medeasy.domain.user_schedule.db.UserScheduleEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "\"user\"")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 150)
    private String email;

    @Column(nullable = false, length = 50)
    private String name;

    @Column(nullable = false, length = 150)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private Gender gender;

    @Column(nullable = true, length = 20)
    private String kakaoUid;

    @Column(nullable = true, length = 20)
    private String appleUid;

    @Column(nullable = true, length = 200)
    private String appleRefreshToken;

    @CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "registered_at")
    private Date registeredAt;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "logined_at")
    private LocalDateTime loginedAt;

    @Temporal(TemporalType.DATE)
    private Date birthday;

    @Builder.Default
    @OneToMany(mappedBy = "careProvider")
    private List<UserCareMappingEntity> careReceivers=new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "careReceiver")
    private List<UserCareMappingEntity> careProviders = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY, orphanRemoval = true, cascade = CascadeType.ALL)
    private List<RoutineGroupEntity> routineGroups= new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY, orphanRemoval = true, cascade = CascadeType.ALL)
    @OrderBy("takeTime ASC")
    private List<UserScheduleEntity> userSchedules = new ArrayList<>();

    @Column(name = "is_notification_agreed", nullable = false)
    @Builder.Default
    private Boolean isNotificationAgreed = true;
}
