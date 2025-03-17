package com.medeasy.domain.user_schedule.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalTime;

/**
 * 사용자 개인 루틴 업데이트 요청 dto
 * */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserScheduleUpdateRequest {
    @Schema(description = "스케줄 식별 ID", example = "1", nullable = true)
    private Long userScheduleId;

    @Schema(description = "스케줄 별명", example = "아침 식사 후", nullable = true)
    private String name;

    @JsonFormat(pattern = "HH:mm:ss")
    @Schema(description = "아침 기상 시간 (형식: HH:mm:ss)", example = "08:30:00", nullable = true, type = "string")
    private LocalTime take_time;
}
