package com.medeasy.domain.routine.dto;

import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CurrentRoutineMedicineResponse {

    private String medicineId;

    private String medicineImage;

    private String medicineName;

    private String nickname;

    private String entpName;

    private String className; // 분류명

    private String etcOtcName; // 전문의약품 여부

    private LocalDate routineStartDate;

    private LocalDate routineEndDate;

    private int dose;

    private int scheduleSize;

    private Integer intervalDays;
}
