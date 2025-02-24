package com.medeasy.domain.routine.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RoutineDto {
    private Long routineId;
    private Long medicineId;
    private String nickname;
    private LocalTime takeTime;
    private String type;
    private Boolean isTaken;
    private LocalDate takeDate;
}
