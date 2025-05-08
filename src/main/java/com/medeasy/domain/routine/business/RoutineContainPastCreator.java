package com.medeasy.domain.routine.business;

import com.medeasy.domain.medicine.db.MedicineDocument;
import com.medeasy.domain.medicine.service.MedicineDocumentService;
import com.medeasy.domain.routine.converter.RoutineConverter;
import com.medeasy.domain.routine.db.RoutineEntity;
import com.medeasy.domain.routine.dto.RoutineRegisterRequest;
import com.medeasy.domain.user.db.UserEntity;
import com.medeasy.domain.user_schedule.db.UserScheduleEntity;
import com.medeasy.domain.user_schedule.service.UserScheduleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Component
public class RoutineContainPastCreator implements RoutineCreator{
    private final RoutineConverter routineConverter;
    private final MedicineDocumentService medicineDocumentService;
    private final UserScheduleService userScheduleService;

    @Override
    public List<RoutineEntity> createRoutines(RoutineCalculator routineCalculator, RoutineRegisterRequest request, UserEntity userEntity, List<UserScheduleEntity> userScheduleEntities) {
        // 루틴 시작 날짜
        LocalDate startDate = request.getRoutineStartDate();
        LocalDate today = LocalDate.now();
        LocalTime startTime;
        LocalTime currentTime = LocalTime.now();

        /**
         * 루틴 시작 시간 계산
         * 요청에 포함되어 있는 경우 명시한 스케줄부터
         * 아닌 경우 제일 처음 스케줄부터
         * */
        if(request.getStartUserScheduleId() != null){
            Long startUserScheduleId= request.getStartUserScheduleId();
            UserScheduleEntity startUserScheduleEntity = userScheduleService.findById(startUserScheduleId);
            startTime = startUserScheduleEntity.getTakeTime();

        } else{
            startTime = userScheduleEntities.getFirst().getTakeTime();
        }

        List<RoutineEntity> routineEntities = new ArrayList<>();

        // 복용 날짜 계산
        List<LocalDate> routineDates= routineCalculator.calculateRoutineDates(startDate, userScheduleEntities.size(), request);
        log.info("날짜 계산 디버깅 로그: {}", routineDates);

        int dose = request.getDose();
        int quantity = 0;
        MedicineDocument medicineDocument = medicineDocumentService.findMedicineDocumentById(request.getMedicineId());
        if (request.getNickname() == null) {
            request.setNickname(medicineDocument.getItemName());
        }

        // 날짜 분리
        List<LocalDate> pastDates = new ArrayList<>();
        List<LocalDate> todayDates = new ArrayList<>();
        List<LocalDate> futureDates = new ArrayList<>();

        for (LocalDate date : routineDates) {
            if (date.isBefore(today)) {
                pastDates.add(date);
            } else if (date.isEqual(today)) {
                todayDates.add(date);
            } else {
                futureDates.add(date);
            }
        }

        // TODO 복용 날짜가 과거인 경우 생성과 check
        if(pastDates.contains(startDate)) {
            for (UserScheduleEntity userScheduleEntity : userScheduleEntities) {
                if (startTime.isAfter(userScheduleEntity.getTakeTime())) {
                    continue;
                }
                quantity += dose;
                if (quantity > request.getTotalQuantity()) break;

                RoutineEntity routineEntity = routineConverter.toEntityFromRequest(startDate, userEntity, userScheduleEntity);
                routineEntity.setIsTaken(true);
                routineEntities.add(routineEntity);
            }

            // 오늘 날짜 등록 후 리스트 제외
            pastDates.remove(startDate);
        }

        for (LocalDate localDate : pastDates) {
            for (UserScheduleEntity userScheduleEntity : userScheduleEntities) {
                quantity += dose;
                if (quantity > request.getTotalQuantity()) break;

                RoutineEntity routineEntity = routineConverter.toEntityFromRequest(localDate, userEntity, userScheduleEntity);
                routineEntity.setIsTaken(true);
                routineEntities.add(routineEntity);
            }
        }

        // 현재 날짜 루틴 스케줄
        if (!todayDates.isEmpty()) {
            LocalDate todayDate = todayDates.get(0);  // 무조건 1개만 있으니

            for (UserScheduleEntity userScheduleEntity : userScheduleEntities) {
                quantity += dose;
                if (quantity > request.getTotalQuantity()) break;

                RoutineEntity routineEntity = routineConverter.toEntityFromRequest(todayDate, userEntity, userScheduleEntity);

                // 현재 시간이 복용 시간 이후면 isTaken = true
                if (currentTime.isAfter(userScheduleEntity.getTakeTime())) {
                    routineEntity.setIsTaken(true);
                }

                routineEntities.add(routineEntity);
            }
        }

        // 미래 날짜 복용 스케줄
        for (LocalDate localDate : futureDates) {
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
