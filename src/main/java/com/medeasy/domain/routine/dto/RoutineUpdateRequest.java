package com.medeasy.domain.routine.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.List;

/**
 * 루틴 변경 요청 request dto
 * */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RoutineUpdateRequest {

    @Schema(description = "수정하려는 루틴 약 Id", example = "12", nullable = false)
    private Long routineId;

    @Schema(description = "약 별칭", example = "감기약", nullable = true)
    private String nickname;

    @Schema(description = "복용 주기", example = "1, 2, 3", nullable = true)
    private List<Long> dayOfWeeks;

    @Schema(description = "복용 스케줄", example = "1, 2, 3", nullable = true)
    private List<Long> userScheduleIds;

    @Schema(description = "약 1회 투여량", example = "1", nullable = true)
    private int dose;

    @Schema(description = "약 총 투여일수", example = "3", nullable = true)
    private int totalDays;
}
