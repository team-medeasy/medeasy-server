package com.medeasy.domain.routine_group_mapping.db;

import com.medeasy.domain.routine.db.RoutineEntity;
import com.medeasy.domain.routine_group.db.RoutineGroupEntity;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "routine_group_mapping")
@SequenceGenerator(
        name = "routine_group_mapping_seq_generator",
        sequenceName = "routine_group_mapping_id_seq",
        allocationSize = 30
)
public class RoutineGroupMappingEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "routine_group_seq_generator")
    private Long id;

    @JoinColumn(name = "routine_group_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private RoutineGroupEntity routineGroup;

    @JoinColumn(name = "routine_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private RoutineEntity routine;
}
