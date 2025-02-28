package com.medeasy.domain.user.dto;

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
public class RoutineScheduleRequest {

    @JsonFormat(pattern = "HH:mm:ss")
    @Schema(description = "아침 기상 시간 (형식: HH:mm:ss)", example = "08:30:00", nullable = true, type = "string")
    private LocalTime morningTime;

    @JsonFormat(pattern = "HH:mm:ss")
    @Schema(description = "점심 시간 (형식: HH:mm:ss)", example = "12:00:00", nullable = true, type = "string")
    private LocalTime lunchTime;

    @JsonFormat(pattern = "HH:mm:ss")
    @Schema(description = "저녁 식사 시간 (형식: HH:mm:ss)", example = "18:00:00", nullable = true, type = "string")
    private LocalTime dinnerTime;

    @JsonFormat(pattern = "HH:mm:ss")
    @Schema(description = "취침 시간 (형식: HH:mm:ss)", example = "22:00:00", nullable = true, type = "string")
    private LocalTime bedTime;
}
