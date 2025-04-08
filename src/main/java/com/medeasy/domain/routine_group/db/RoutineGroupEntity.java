package com.medeasy.domain.routine_group.db;

import com.medeasy.domain.routine_group_mapping.db.RoutineGroupMappingEntity;
import jakarta.persistence.*;
import lombok.*;

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

    @Column(nullable = false, length = 50)
    private String medicineId;

    @OneToMany(mappedBy = "routineGroup", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    @Builder.Default
    private List<RoutineGroupMappingEntity> routineGroupMappings=new ArrayList<>();
}
