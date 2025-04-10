package com.medeasy.domain.user_schedule.dto;

import lombok.*;

import java.time.LocalTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserScheduleGroupDto {

    private Long userScheduleId;

    private String name;

    private LocalTime takeTime;

    private List<RoutineMedicineDto> routineMedicineDtos;
}
