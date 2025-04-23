package com.medeasy.domain.routine_group.db;

import com.medeasy.domain.routine.db.RoutineEntity;
import com.medeasy.domain.user.db.UserEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "routine_group")
@SequenceGenerator(
    name = "routine_group_seq_generator",
    sequenceName = "routine_group_id_seq",
    allocationSize = 10
)
public class RoutineGroupEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "routine_group_seq_generator")
    private Long id;

    @Column(name = "created_at", nullable = false, updatable = false)
    @CreationTimestamp
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false, updatable = false)
    @UpdateTimestamp
    private LocalDateTime updatedAt;

    @Column(nullable = false)
    private String nickname;

    @Column(nullable = false)
    private String medicineId;

    @Column(nullable = false)
    private int dose;

    @JoinColumn(name = "user_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private UserEntity user;

    @OneToMany(mappedBy = "routineGroup", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    @Builder.Default
    @OrderBy("takeDate ASC")
    private List<RoutineEntity> routines=new ArrayList<>();

    public RoutineGroupEntity mappingWithRoutines(List<RoutineEntity> routines) {
        this.routines.addAll(routines);
        routines.forEach(r->r.setRoutineGroup(this));

        return this;
    }

    public RoutineGroupEntity updateRoutine(String nickname, String medicineId, int dose) {
        this.medicineId = medicineId;
        this.dose = dose;
        this.nickname = nickname;

        return this;
    }
}
