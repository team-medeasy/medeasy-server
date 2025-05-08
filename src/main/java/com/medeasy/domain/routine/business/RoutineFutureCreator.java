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
public class RoutineFutureCreator implements RoutineCreator{
    private final RoutineConverter routineConverter;
    private final MedicineDocumentService medicineDocumentService;
    private final UserScheduleService userScheduleService;

    @Override
    public List<RoutineEntity> createRoutines(RoutineCalculator routineCalculator, RoutineRegisterRequest request, UserEntity userEntity, List<UserScheduleEntity> userScheduleEntities) {
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
            log.info("루틴 시작 스케줄 id: {}, 시간: {}", startUserScheduleId, startDate);
        } else{
            startTime = userScheduleEntities.getFirst().getTakeTime();
            log.info("루틴 시작 스케줄 명시 x 첫 스케줄부터 등록 스케줄id: {}, 시간: {}", userScheduleEntities.getFirst().getId(), userScheduleEntities.getFirst().getTakeTime());
        }

        List<RoutineEntity> routineEntities = new ArrayList<>();

        // 복용 날짜 계산
        List<LocalDate> routineDates= routineCalculator.calculateRoutineDates(startDate, userScheduleEntities.size(), request);
        int dose = request.getDose();
        int quantity = 0;
        MedicineDocument medicineDocument = medicineDocumentService.findMedicineDocumentById(request.getMedicineId());
        if (request.getNickname() == null) {
            request.setNickname(medicineDocument.getItemName());
        }

        // 루틴 시작 첫번째 날짜가 시작 날짜인 경우
        if(routineDates.contains(startDate)) {
            for (UserScheduleEntity userScheduleEntity : userScheduleEntities) {
                if (startTime.isAfter(userScheduleEntity.getTakeTime())) {
                    continue;
                }
                quantity += dose;
                if (quantity > request.getTotalQuantity()) break;

                RoutineEntity routineEntity = routineConverter.toEntityFromRequest(startDate, userEntity, userScheduleEntity);
                routineEntities.add(routineEntity);
            }

            // 시작 날짜 루틴 등록 후 날짜 리스트 제외
            routineDates.remove(startDate);
        }

        // 남은 날짜 스케줄
        for (LocalDate localDate : routineDates) {
            for (UserScheduleEntity userScheduleEntity : userScheduleEntities) {
                quantity += dose;
                if (quantity > request.getTotalQuantity()) break;

                RoutineEntity routineEntity = routineConverter.toEntityFromRequest(localDate, userEntity, userScheduleEntity);
                routineEntities.add(routineEntity);

                log.info("루틴 업데이트 디버깅 루틴 생성 부분 user_schedule_id: {}, quantity: {}", userScheduleEntity.getId(), quantity);
            }
        }

        return routineEntities;
    }
}
