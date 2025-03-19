package com.medeasy.domain.schedular.dto;

import lombok.*;

import java.sql.Time;
import java.sql.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
/**
 * 스프링 스케줄러를 통해 앞으로 한시간에 해당하는 루틴, 약, 스케줄 정보 매핑 DTO
 * */
public class RoutineNativeQueryDto {
    private Long routineId;
    private String scheduleName;
    private Date takeDate;

    private String medicineId;
    private String medicineNickname;
    private int dose;

    private Long userId;
    private Time takeTime;
}
