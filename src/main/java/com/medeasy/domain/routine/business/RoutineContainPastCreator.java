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
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Service
public class RoutineContainPastCreator implements RoutineCreator{
    private final RoutineCalculator routineCalculator;
    private final RoutineConverter routineConverter;
    private final MedicineDocumentService medicineDocumentService;
    private final UserScheduleService userScheduleService;

    @Override
    public List<RoutineEntity> createRoutines(RoutineRegisterRequest request, UserEntity userEntity, List<UserScheduleEntity> userScheduleEntities) {
        // 루틴 시작 날짜
        LocalDate startDate = request.getRoutineStartDate();
        LocalTime startTime;

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
        List<LocalDate> routineDates=routineCalculator.calculateRoutineDates(startDate, userScheduleEntities.size(), request);
        int dose = request.getDose();
        int quantity = 0;
        MedicineDocument medicineDocument = medicineDocumentService.findMedicineDocumentById(request.getMedicineId());
        String nickname=request.getNickname() == null ? medicineDocument.getItemName() : request.getNickname();

        // 과거 날짜와 현재, 미래 날짜 분리
        List<LocalDate> pastDates = new ArrayList<>();
        List<LocalDate> futureOrTodayDates = new ArrayList<>();

        for (LocalDate date : routineDates) {
            if (date.isBefore(LocalDate.now())) {
                pastDates.add(date);
            } else {
                futureOrTodayDates.add(date);
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

                RoutineEntity routineEntity = routineConverter.toEntityFromRequest(startDate, nickname, userEntity, userScheduleEntity, request);
                routineEntity.setIsTaken(true);
                routineEntities.add(routineEntity);
            }

            // 오늘 날짜 등록 후 리스트 제외
            pastDates.remove(startDate);
        }

        // 오늘 날짜를 제외한 루틴 생성
        for (LocalDate localDate : pastDates) {
            for (UserScheduleEntity userScheduleEntity : userScheduleEntities) {
                quantity += dose;
                if (quantity > request.getTotalQuantity()) break;

                RoutineEntity routineEntity = routineConverter.toEntityFromRequest(localDate, nickname, userEntity, userScheduleEntity, request);
                routineEntity.setIsTaken(true);
                routineEntities.add(routineEntity);
            }
        }

        // TODO 현재, 미래 날짜 복용 스케줄
        for (LocalDate localDate : futureOrTodayDates) {
            for (UserScheduleEntity userScheduleEntity : userScheduleEntities) {
                quantity += dose;
                if (quantity > request.getTotalQuantity()) break;

                RoutineEntity routineEntity = routineConverter.toEntityFromRequest(localDate, nickname, userEntity, userScheduleEntity, request);
                routineEntities.add(routineEntity);
            }
        }

        return routineEntities;
    }
}
