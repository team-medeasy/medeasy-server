package com.medeasy.domain.routine.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
/*
* 그룹형 쿼리 튜플 변환을 위한 dto
* */
public class RoutineGroupDto {
    private LocalDate takeDate;

    private List<UserScheduleGroupDto> userScheduleDtos;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    static class UserScheduleGroupDto {

        private Long userScheduleId;

        private String name; // 스케줄 이름

        private LocalTime takeTime;

        private List<RoutineDto> routineMedicineDtos;

        @Data
        @NoArgsConstructor
        @AllArgsConstructor
        @Builder
        static class RoutineDto{
            private Long routineId;
            private String nickname;
            private Boolean isTaken;
        }
    }
}
