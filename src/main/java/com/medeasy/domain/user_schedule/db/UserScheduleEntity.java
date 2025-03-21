package com.medeasy.domain.user_schedule.db;

import com.medeasy.domain.routine.db.RoutineEntity;
import com.medeasy.domain.user.db.UserEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "user_schedule")
public class UserScheduleEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JoinColumn(name = "user_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private UserEntity user;

    @Column(name = "name", length = 150)
    private String name;

    @Column(name = "take_time")
    @Temporal(TemporalType.TIME)
    private LocalTime takeTime;

    @Builder.Default
    @OneToMany(mappedBy = "userSchedule", orphanRemoval = false)
    private List<RoutineEntity> routine=new ArrayList<>();
}
