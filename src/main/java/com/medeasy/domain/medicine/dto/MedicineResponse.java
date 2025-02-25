package com.medeasy.domain.medicine.dto;

import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MedicineResponse {

    private Long id;

    private String itemCode;

    private String entpName;

    private String itemName;

    private String shape;

    private String color;

    private String efficacy;

    private String useMethod;

    private String attention;

    private String interaction; // 상호작용

    private String sideEffect; // 부작용

    private String depositMethod; // 보관법

    private LocalDate openAt; // 공개일자

    private LocalDate updateAt; // 수정일자

    private String imageUrl; // 이미지 URL

    private String bizrno;
}
