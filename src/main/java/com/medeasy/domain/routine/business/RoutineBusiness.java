package com.medeasy.domain.routine.business;

import com.medeasy.common.annotation.Business;
import com.medeasy.common.error.ErrorCode;
import com.medeasy.common.exception.ApiException;
import com.medeasy.domain.medicine.db.MedicineEntity;
import com.medeasy.domain.medicine.service.MedicineService;
import com.medeasy.domain.routine.db.RoutineEntity;
import com.medeasy.domain.routine.dto.RoutineRegisterRequest;
import com.medeasy.domain.routine.service.RoutineService;
import com.medeasy.domain.user.db.UserEntity;
import com.medeasy.domain.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Business
@RequiredArgsConstructor
public class RoutineBusiness {

    private final RoutineService routineService;
    private final UserService userService;
    private final MedicineService medicineService;

    public void registerRoutine(Long userId, RoutineRegisterRequest routineRegisterRequest) {

        /*
        약 루틴 저장
        spring sequrity의 usercontext에서 사용자 정보를 가져오고
        약 정보, 별명, 1회 복용량, 총 개수 저장

        스케줄 저장
        * 1. 일단 사용자 개인의 커스텀 시간을 가져온다.
        * 2. 요청으로 들어온 date의 개수 * 시간의 개수만큼 RoutineSchedule entity를 생성한다.
        * 3. 리스트로 만들어 한번에 저장
        * */

        UserEntity userEntity = userService.getUserById(userId);
        MedicineEntity medicineEntity = medicineService.getMedicineById(routineRegisterRequest.getMedicineId());

        String nickname=routineRegisterRequest.getNickname() == null ? medicineEntity.getItemName() : routineRegisterRequest.getNickname();

        int quantity=0;
        List<RoutineEntity> routineEntities=new ArrayList<>();

        for(int i=0; i<routineRegisterRequest.getDates().size(); i++){
            for(int j=0; j<routineRegisterRequest.getTypes().size(); j++){
                if(quantity>routineRegisterRequest.getTotalQuantity()) break;

                // 사용자 시간 변환
                LocalTime time = convertUserTimeToLocalTime(routineRegisterRequest.getTypes().get(j), userEntity);
                LocalDate date = routineRegisterRequest.getDates().get(i);
                // 날짜랑 시간 합치기
                LocalDateTime dateTime = LocalDateTime.of(date, time);

                RoutineEntity routineEntity=RoutineEntity.builder()
                        .nickname(nickname)
                        .isTaken(false)
                        .takeTime(dateTime)
                        .dose(routineRegisterRequest.getDose())
                        .type(routineRegisterRequest.getTypes().get(j))
                        .medicine(medicineEntity)
                        .user(userEntity)
                        .build()
                        ;

                routineEntities.add(routineEntity);
            }
        }
        routineService.saveAll(routineEntities);
    }


    private LocalTime convertUserTimeToLocalTime(String type, UserEntity userEntity) {
        log.info(String.valueOf(userEntity.getMorning()));
        log.info(String.valueOf(userEntity.getLunch()));

        return switch (type) {
            case "MORNING" -> userEntity.getMorning();
            case "LUNCH" -> userEntity.getLunch();
            case "DINNER" -> userEntity.getDinner();
            case "BEDTIME" -> userEntity.getBedTime();
            default -> throw new ApiException(ErrorCode.BAD_REQEUST, "아침, 점심, 저녁, 자기전 외의 시간 입력 오류");
        };
    }
}
