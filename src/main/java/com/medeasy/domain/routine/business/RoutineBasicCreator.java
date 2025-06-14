package com.medeasy.domain.routine.business;

import com.medeasy.domain.medicine.db.MedicineDocument;
import com.medeasy.domain.medicine.service.MedicineDocumentService;
import com.medeasy.domain.routine.converter.RoutineConverter;
import com.medeasy.domain.routine.db.RoutineEntity;
import com.medeasy.domain.routine.dto.RoutineRegisterRequest;
import com.medeasy.domain.user.db.UserEntity;
import com.medeasy.domain.user_schedule.db.UserScheduleEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class RoutineBasicCreator implements RoutineCreator{

    private final RoutineConverter routineConverter;
    private final MedicineDocumentService medicineDocumentService;

    @Override
    public List<RoutineEntity> createRoutines(RoutineCalculator routineCalculator, RoutineRegisterRequest request, UserEntity userEntity, List<UserScheduleEntity> userScheduleEntities) {
        LocalDate startDate = LocalDate.now();
        LocalTime startTime = LocalTime.now();
        List<RoutineEntity> routineEntities = new ArrayList<>();
        List<LocalDate> routineDates= routineCalculator.calculateRoutineDates(startDate, userScheduleEntities.size(), request);
        int dose = request.getDose();
        int quantity = 0;
        MedicineDocument medicineDocument = medicineDocumentService.findMedicineDocumentById(request.getMedicineId());
        if (request.getNickname() == null) {
            request.setNickname(medicineDocument.getItemName());
        }

        // 사용할 루틴 미리 조회 및 생성
        if(routineDates.contains(startDate)) {
            for (UserScheduleEntity userScheduleEntity : userScheduleEntities) {
                if (LocalTime.now().isAfter(userScheduleEntity.getTakeTime())) {
                    continue;
                }
                quantity += dose;
                if (quantity > request.getTotalQuantity()) break;

                RoutineEntity routineEntity = routineConverter.toEntityFromRequest(startDate, userEntity, userScheduleEntity);
                routineEntities.add(routineEntity);
            }

            // 오늘 날짜 등록 후 리스트 제외
            routineDates.remove(startDate);
        }

        // 오늘 날짜를 제외한 루틴 생성
        for (LocalDate localDate : routineDates) {
            for (UserScheduleEntity userScheduleEntity : userScheduleEntities) {
                quantity += dose;
                if (quantity > request.getTotalQuantity()) break;

                RoutineEntity routineEntity = routineConverter.toEntityFromRequest(localDate, userEntity, userScheduleEntity);
                routineEntities.add(routineEntity);
            }
        }

        return routineEntities;
    }
}
