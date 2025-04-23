package com.medeasy.domain.routine.dto;

import lombok.*;

import java.time.LocalTime;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RoutineGroupInfoResponse {

    private Long routineGroupId;

    private String medicineId;

    private String nickname;

    private Integer dose;

    private Integer intervalDays;

    private Integer remainingQuantity;

    private List<ScheduleResponse> scheduleResponses;

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ScheduleResponse{

        private Long userScheduleId;

        private String name;

        private LocalTime takeTime;

        private boolean isSelected;

    }
}
