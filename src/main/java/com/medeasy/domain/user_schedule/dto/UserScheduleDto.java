package com.medeasy.domain.user_schedule.dto;

import com.medeasy.domain.routine_medicine.dto.RoutineMedicineDto;
import lombok.*;

import java.time.LocalTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserScheduleDto {

    private Long id;

    private String name;

    private LocalTime takeTime;

    private List<RoutineMedicineDto> routineMedicineDtos;
}
