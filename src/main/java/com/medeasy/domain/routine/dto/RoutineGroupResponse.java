package com.medeasy.domain.routine.dto;

import lombok.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
/*
* 사용자 루틴 일별 조회 응답 dto
* */
public class RoutineGroupResponse {
    private LocalTime takeTime;
    private LocalDate takeDate;
    private String type;
    private List<RoutineMedicineResponse> medicines;

    @Builder
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RoutineMedicineResponse{
        private Long routineId;
        private String medicineName;
        private boolean isTaken;
    }
}
