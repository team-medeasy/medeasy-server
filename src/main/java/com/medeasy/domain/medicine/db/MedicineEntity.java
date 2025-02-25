package com.medeasy.domain.medicine.db;

import com.medeasy.domain.routine.db.RoutineEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "medicine")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@SequenceGenerator(
        name = "medicine_seq_generator",
        sequenceName = "public.medicine_id_seq",
        allocationSize = 50,
        initialValue = 2350
)
public class MedicineEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "medicine_seq_generator")
    @Column(name = "id", nullable = false, updatable = false)
    private Long id;

    @Column(name = "item_code", nullable = true, length = 50, columnDefinition = "varchar")
    private String itemCode;

    @Column(name = "entp_name", nullable = false, length = 100)
    private String entpName;

    @Column(name = "shape", nullable = false, length = 50)
    private String shape;

    @Column(name = "color", nullable = false, length = 50)
    private String color;

    @Column(name = "item_name", nullable = false, length = 100)
    private String itemName;

    @Column(name = "efficacy", columnDefinition = "TEXT")
    private String efficacy;

    @Column(name = "use_method", columnDefinition = "TEXT")
    private String useMethod;

    @Column(name = "attention", columnDefinition = "TEXT")
    private String attention;

    @Column(name = "interaction", columnDefinition = "TEXT")
    private String interaction; // 상호작용

    @Column(name = "side_effect", columnDefinition = "TEXT")
    private String sideEffect; // 부작용

    @Column(name = "deposit_method", columnDefinition = "TEXT")
    private String depositMethod; // 보관법

    @Column(name = "open_at")
    private LocalDate openAt; // 공개일자

    @Column(name = "update_at")
    private LocalDate updateAt; // 수정일자

    @Column(name = "image_url", columnDefinition = "TEXT")
    private String imageUrl; // 이미지 URL

    @Column(name = "bizrno", nullable = true, length = 50)
    private String bizrno;

    // 양방향 매핑이 필요없다고 판단. 주석처리
//    @OneToMany(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH, CascadeType.DETACH})
//    private List<RoutineEntity> routines=new ArrayList<>();
}
