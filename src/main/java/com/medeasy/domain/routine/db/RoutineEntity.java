package com.medeasy.domain.routine.db;

import com.medeasy.domain.routine_group_mapping.db.RoutineGroupMappingEntity;
import com.medeasy.domain.user.db.UserEntity;
import com.medeasy.domain.user_schedule.db.UserScheduleEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;


@Entity
@Table(name = "routine")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@SequenceGenerator(
        name = "routine_seq_generator",
        sequenceName = "routine_id_seq",
        allocationSize = 30
)
public class RoutineEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "routine_seq_generator")
    private Long id;

    @Temporal(TemporalType.DATE)
    @Column(nullable = false)
    private LocalDate takeDate;

    @JoinColumn(name = "user_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private UserEntity user;

    @JoinColumn(name = "user_schedule_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private UserScheduleEntity userSchedule;

    @Column(nullable = false)
    private String nickname;

    @Column(nullable = false)
    private String medicineId;

    @Column(nullable = false)
    private int dose;

    @Column(nullable = false)
    private boolean isTaken;

    @OneToMany(mappedBy = "routine", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    @Builder.Default
    private List<RoutineGroupMappingEntity> routineGroupMappings=new ArrayList<>();

    /*
    * CascadeType.ALL 부모 엔티티의 업데이트를 자식에서도 따라감.
    * orphanRemoval 부모 엔티티가 삭제되면 그 삭제 여부를 database에도 반영
    * */
}
