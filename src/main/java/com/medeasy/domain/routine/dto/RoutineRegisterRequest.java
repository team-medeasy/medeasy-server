package com.medeasy.domain.routine.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RoutineRegisterRequest {
    @Schema(description = "복용 루틴으로 등록할 약 id", example = "3594")
    private String medicineId;

    @Schema(description = "약 등록 이름", example = "아스피린")
    private String nickname;

    @Schema(description = "한번에 복용할 개수", example = "1")
    private int dose;

    @Schema(description = "총 약의 개수", example = "30")
    private int totalQuantity;

    @Schema(description = "약 복용 요일, 월요일~일요일 -> 1~7", example = "[1, 2, 3]", nullable = true)
    private List<Integer> dayOfWeeks;

    @Schema(description = "약을 복용할 사용자 스케줄 리스트", example = "[1, 2, 3]")
    private List<Long> userScheduleIds;

    @Schema(description = "약을 복용 시작 날짜 default: 오늘", example = "2025-04-11")
    private LocalDate routineStartDate;

    @Schema(description = "복용 시작 스케줄 default: 지나지 않은 스케줄 중 가장 가까운 시간", example = "37")
    private Long startUserScheduleId;

//    @Schema(description = "새 약을 복용할 시기 ", example = "[MORNING, LUNCH, DINNER, BEDTIME]")
//    private String typeDescription;

    @Schema(description = "복용 날짜 간격", example = "1", nullable = true)
    private Long intervalDays;
}
