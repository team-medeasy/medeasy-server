package com.medeasy.domain.routine.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RoutineRegisterRequestByTotalDays {
    @Schema(description = "복용 루틴으로 등록할 약 id", example = "3594")
    private String medicineId;

    @Schema(description = "약 등록 이름", example = "아스피린")
    private String nickname;

    @Schema(description = "한번에 복용할 개수", example = "1")
    private int dose;

    @Schema(description = "총 복약 날짜수", example = "5")
    private int totalDays;

    @Schema(description = "약 복용 요일, 월요일~일요일 -> 1~7", example = "[1, 2, 3]")
    private List<Integer> dayOfWeeks;

    @Schema(description = "약을 복용할 사용자 스케줄 리스트", example = "[1, 2, 3]")
    private List<Long> userScheduleIds;

//    @Schema(description = "새 약을 복용할 시기 ", example = "[MORNING, LUNCH, DINNER, BEDTIME]")
//    private String typeDescription;
}
