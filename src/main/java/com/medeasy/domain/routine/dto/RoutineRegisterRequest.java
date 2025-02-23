package com.medeasy.domain.routine.dto;

import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RoutineRegisterRequest {
    private Long medicineId;

    private String nickname;

    private int dose;

    private int totalQuantity;

    private List<LocalDate> dates;

    private List<String> types;

    private String typeDescription;
}
