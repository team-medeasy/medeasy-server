package com.medeasy.domain.routine.business;

import com.medeasy.common.annotation.Business;
import com.medeasy.common.error.ErrorCode;
import com.medeasy.common.exception.ApiException;
import com.medeasy.domain.medicine.db.MedicineEntity;
import com.medeasy.domain.medicine.service.MedicineService;
import com.medeasy.domain.routine.converter.RoutineConverter;
import com.medeasy.domain.routine.db.RoutineEntity;
import com.medeasy.domain.routine.dto.RoutineCheckResponse;
import com.medeasy.domain.routine.dto.RoutineGroupDto;
import com.medeasy.domain.routine.dto.RoutineGroupResponse;
import com.medeasy.domain.routine.dto.RoutineRegisterRequest;
import com.medeasy.domain.routine.service.RoutineService;
import com.medeasy.domain.user.db.UserEntity;
import com.medeasy.domain.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Slf4j
@Business
@RequiredArgsConstructor
public class RoutineBusiness {

    private final RoutineService routineService;
    private final UserService userService;
    private final MedicineService medicineService;
    private final RoutineConverter routineConverter;

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

        /*
        * 1. 오늘 날짜 가져오기
        * 2. 하루에 몇번 먹는지 한번에 몇개의 약을 먹는지
        *
        * 예를 들어 total_quantity=29
        * 하루에 약을 3번 한번에 2개 먹는다고 가정
        * 그러면 29/6 = 4
        * 총 4일동안 먹고 5개가 남는다.
        * 즉 마지막 날에 2번 더 먹을 수 있음.
        * 따라서 4+1로 올림 처리를 해야함.
        *
        * 월~일 -> 1~7
        * 예를들어 월 수 금
        * requiredDays가 5일
        *
        * 오늘이
        *
        * 월요일, 수요일, 금요일, 월요일, 수요일 -> 날짜로 표현
        * 2/24, 2/26, 2/28, 3/3, 3/5
        * */
        // Entity 값 가져오기
        UserEntity userEntity = userService.getUserById(userId);
        MedicineEntity medicineEntity = medicineService.getMedicineById(routineRegisterRequest.getMedicineId());

        // 요청 값 변환
        List<String> types = routineRegisterRequest.getTypes();
        List<LocalTime> times = convertTypesToTimes(types, userEntity);
        String nickname=routineRegisterRequest.getNickname() == null ? medicineEntity.getItemName() : routineRegisterRequest.getNickname();
        int dose = routineRegisterRequest.getDose();

        // 계산을 위한 변수
        List<RoutineEntity> routineEntities=new ArrayList<>();
        int quantity=0;

        // 오늘 날짜의 복용 루틴 저장
        LocalDate currnetDate = LocalDate.now();
        int currentDayValue = currnetDate.getDayOfWeek().getValue();

        if(routineRegisterRequest.getDayOfWeeks().contains(currentDayValue)) {
            LocalTime currentTime = LocalTime.now();

            for(int i=0; i<types.size(); i++) {
                if(currentTime.isBefore(times.get(i))){
                    RoutineEntity routineEntity=RoutineEntity.builder()
                            .nickname(nickname)
                            .isTaken(false)
                            .takeDate(currnetDate)
                            .takeTime(times.get(i))
                            .dose(routineRegisterRequest.getDose())
                            .type(types.get(i))
                            .medicine(medicineEntity)
                            .user(userEntity)
                            .build()
                            ;
                    routineEntities.add(routineEntity);
                    quantity+=dose;
                }
            }

        }

        //TODO 내일 부터 계산
        // 하루에 몇 개 먹는지
        int dailyDose=types.size()*routineRegisterRequest.getDose();
        int totalQuantity=routineRegisterRequest.getTotalQuantity()-quantity;
        int requiredDays=(int) Math.ceil((double) totalQuantity/dailyDose); // 반올림
        LocalDate nextDate = LocalDate.now().plusDays(1);

        // 약을 복용하는 날짜
        List<LocalDate> dates = new ArrayList<>();

        while(dates.size() < requiredDays) {
            int nextDayValue = nextDate.getDayOfWeek().getValue();

            if(routineRegisterRequest.getDayOfWeeks().contains(nextDayValue)) {
                dates.add(nextDate);
            }

            nextDate = nextDate.plusDays(1);
        }

        quantity=0;
        for(int i=0; i<dates.size(); i++){
            for(int j=0; j<routineRegisterRequest.getTypes().size(); j++){
                quantity+=routineRegisterRequest.getDose();
                if(quantity>totalQuantity) break;

                // 사용자 시간 변환
                LocalTime time = convertTypeToLocalTime(routineRegisterRequest.getTypes().get(j), userEntity);
                LocalDate date = dates.get(i);

                RoutineEntity routineEntity=RoutineEntity.builder()
                        .nickname(nickname)
                        .isTaken(false)
                        .takeDate(date)
                        .takeTime(time)
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


    private LocalTime convertTypeToLocalTime(String type, UserEntity userEntity) {
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

    private List<LocalDate> convertDayOfWeeksToDates(List<String> dayOfWeeks){
        List<LocalDate> dates = new ArrayList<>();

        // 오늘 날짜를 가져온다.

        //
        return null;
    }


    /*
    * MORNING, LUNCH, DINNER 리스트를 사용자 정의 시간 리스트로 변경
    * */
    private List<LocalTime> convertTypesToTimes(List<String> types, UserEntity userEntity){
        List<LocalTime> times = new ArrayList<>();

        for (String type : types) {
            switch (type.toUpperCase()) {
                case "MORNING":
                    times.add(userEntity.getMorning());
                    break;
                case "LUNCH":
                    times.add(userEntity.getLunch());
                    break;
                case "DINNER":
                    times.add(userEntity.getDinner());
                    break;
                case "BEDTIME":
                    times.add(userEntity.getBedTime());
                    break;
                default:
                    throw new IllegalArgumentException("알 수 없는 타입: " + type);
            }
        }
        return times;
    }

    public List<RoutineGroupResponse> getRoutineListByDate(Long userId, LocalDate date) {
        List<RoutineGroupDto> routineGroupDtos=routineService.getRoutineGroups(date, userId);

        return routineGroupDtos.stream()
                .map(routineConverter::toGroupResponse).toList();
    }

    public void test(LocalDate date) {

        UserEntity userEntity = userService.getUserById(7L);
        MedicineEntity medicineEntity = medicineService.getMedicineById(3594L);

        List<RoutineEntity> entities = new ArrayList<>();
        for (int i = 0; i < 500; i++) {
            date=date.plusDays(1);
            for(int j = 0; j < 3; j++) {
                LocalTime localTime = LocalTime.of(j*3, 0);

                RoutineEntity routineEntity = RoutineEntity.builder()
                        .nickname("test")
                        .isTaken(false)
                        .takeDate(date)
                        .takeTime(localTime)
                        .dose(1)
                        .type("테스트")
                        .medicine(medicineEntity)
                        .user(userEntity)
                        .build()
                        ;

                entities.add(routineEntity);
            }
        }
        routineService.saveAll(entities);
    }

    @Transactional()
    public RoutineCheckResponse checkRoutine(Long routineId, Boolean isTaken) {

        RoutineEntity routineEntity=routineService.getRoutineById(routineId);
        Boolean beforeIsTaken=routineEntity.getIsTaken();
        routineEntity.setIsTaken(isTaken);

        return RoutineCheckResponse.builder()
                .routineId(routineEntity.getId())
                .beforeIsTaken(beforeIsTaken)
                .afterIsTaken(routineEntity.getIsTaken())
                .build()
                ;
    }
}
