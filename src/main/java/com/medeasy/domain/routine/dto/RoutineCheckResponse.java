package com.medeasy.domain.routine.dto;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RoutineCheckResponse {

    private Long routineId;

    private Boolean beforeIsTaken;

    private Boolean afterIsTaken;
}
