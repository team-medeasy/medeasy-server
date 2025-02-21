package com.medeasy.domain.routine.dto;

import lombok.*;

import java.sql.Time;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RoutineRegisterRequest {
    private Long medicineId;

    private String nickname;

    private Long dose;

    private Long totalQuantity;

    private List<Date> dates;

    private List<Time> times;
}
