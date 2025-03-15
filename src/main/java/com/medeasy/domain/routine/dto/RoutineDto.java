package com.medeasy.domain.routine.dto;

import com.medeasy.domain.routine_medicine.db.RoutineMedicineEntity;
import com.medeasy.domain.user.db.UserEntity;
import com.medeasy.domain.user_schedule.db.UserScheduleEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
/**
 * 하나의 날짜에 대한 루틴
 * */
public class RoutineDto {
    private Long id;

    private LocalDate takeDate;

    private UserEntity user;

    private UserScheduleEntity userSchedule;

    private List<RoutineMedicineEntity> routineMedicines = new ArrayList<>();
}
