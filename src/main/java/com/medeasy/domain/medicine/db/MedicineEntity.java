package com.medeasy.domain.medicine.db;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "medicine")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MedicineEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, updatable = false)
    private Long id;

    @Column(name = "item_code", nullable = true)
    private Long itemCode;

    @Column(name = "entp_name", nullable = false, length = 100)
    private String entpName;

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
}
