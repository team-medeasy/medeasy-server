package com.medeasy.domain.routine.db;

import com.medeasy.domain.medicine.db.MedicineEntity;
import com.medeasy.domain.user.db.UserEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalTime;


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

    @Column(nullable = false, length = 150)
    private String nickname;

    @Temporal(TemporalType.DATE)
    @Column(nullable = false)
    private LocalDate takeDate;

    @Temporal(TemporalType.TIME)
    @Column(nullable = false)
    private LocalTime takeTime;

    @Column(nullable = false, columnDefinition = "bool")
    private boolean isTaken;

    @Column(nullable = false)
    private String type;

    @Column(nullable = false)
    private int dose;

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
}
