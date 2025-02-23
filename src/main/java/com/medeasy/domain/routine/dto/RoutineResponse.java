package com.medeasy.domain.routine.dto;

import com.medeasy.domain.routine.db.RoutineEntity;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RoutineResponse {

    private Long id;

    private String nickname;

    private int dose;

    private int totalQuantity;

    // 루틴 등록된 약품 이름
    private String medicineName;

    private List<RoutineScheduleResponse> schedules;

    static class RoutineScheduleResponse{
        private Long id;

        private LocalDate takeDate;

        private LocalTime takeTime;

        private Boolean isTaken;

        private RoutineEntity routine;
    }
}
