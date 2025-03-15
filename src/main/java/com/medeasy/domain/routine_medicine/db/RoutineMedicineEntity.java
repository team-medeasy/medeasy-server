package com.medeasy.domain.routine_medicine.db;

import com.medeasy.domain.routine.db.RoutineEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "routine_medicine")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RoutineMedicineEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 150)
    private String nickname;

    @Column(nullable = false, columnDefinition = "boolean")
    private Boolean isTaken;

    @Column(nullable = false)
    private int dose;

    @JoinColumn(name = "routine_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private RoutineEntity routine;

    // 엘라스틱 서치와의 매칭 정보
    @Column(nullable = false, length = 100)
    private String medicineId;
}
