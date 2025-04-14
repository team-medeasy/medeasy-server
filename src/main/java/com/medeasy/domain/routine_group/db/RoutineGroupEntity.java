package com.medeasy.domain.routine_group.db;

import com.medeasy.domain.routine.db.RoutineEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

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

    @OneToMany(mappedBy = "routineGroup", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    @Builder.Default
    private List<RoutineEntity> routines=new ArrayList<>();
}
