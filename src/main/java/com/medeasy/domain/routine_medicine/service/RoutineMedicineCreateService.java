package com.medeasy.domain.routine_medicine.service;

import com.medeasy.domain.routine_medicine.db.RoutineMedicineEntity;
import com.medeasy.domain.user_schedule.db.UserScheduleEntity;

import java.time.LocalDate;
import java.util.List;

public interface RoutineMedicineCreateService {

    void createRoutineMedicines(List<LocalDate> takeDates, List<UserScheduleEntity> userScheduleEntities, int dose, int quantity, String nickname, String medicineId, List<RoutineMedicineEntity> routineMedicineEntities);
}
