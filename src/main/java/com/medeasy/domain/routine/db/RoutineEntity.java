package com.medeasy.domain.routine.db;

import com.medeasy.domain.medicine.db.MedicineEntity;
import com.medeasy.domain.routine_schedule.db.RoutineScheduleEntity;
import com.medeasy.domain.user.db.UserEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;


@Entity
@Table(name = "routine")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RoutineEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 150)
    private String nickname;

    @Column(nullable = false)
    private int dose;

    @Column(nullable = false)
    private int totalQuantity;

    @JoinColumn(name = "user_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private UserEntity user;

    @JoinColumn(name = "medicine_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private MedicineEntity medicine;

    /*
    * CascadeType.ALL 부모 엔티티의 업데이트를 자식에서도 따라감.
    * orphanRemoval 부모 엔티티가 삭제되면 그 삭제 여부를 database에도 반영
    * */
    @OneToMany(mappedBy = "routine", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<RoutineScheduleEntity> schedules= new ArrayList<>();
}
