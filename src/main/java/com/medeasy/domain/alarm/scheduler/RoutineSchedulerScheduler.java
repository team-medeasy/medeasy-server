package com.medeasy.domain.alarm.scheduler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.medeasy.common.error.SchedulerError;
import com.medeasy.common.exception.ApiException;
import com.medeasy.domain.medicine.service.MedicineDocumentService;
import com.medeasy.domain.routine.db.RoutineEntity;
import com.medeasy.domain.routine.db.RoutineRepository;
import com.medeasy.domain.alarm.dto.AlarmDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneOffset;
import java.util.List;

@Service
@Slf4j
public class RoutineSchedulerScheduler {

    private final RoutineRepository routineRepository;
    private final StringRedisTemplate redisTemplateAlarm;
    private final MedicineDocumentService medicineDocumentService;
    private final ObjectMapper objectMapper;

    @Autowired
    public RoutineSchedulerScheduler(
            RoutineRepository routineRepository,
            @Qualifier("alarmRedisTemplate") StringRedisTemplate redisTemplateAlarm,
            MedicineDocumentService medicineDocumentService,
            ObjectMapper objectMapper
    ) {
        this.routineRepository = routineRepository;
        this.redisTemplateAlarm = redisTemplateAlarm;
        this.medicineDocumentService = medicineDocumentService;
        this.objectMapper = objectMapper;
    }

    /**
     * 한시간에 한번 동작하는 스케줄러
     * 현재시간부터 한시간 후에 해당하는 루틴들을 수집
     * 관련 정보들을 Alarm Redis에 저장한다.
     * */
//    @Scheduled(fixedRate = 600000)
//    public void saveRoutineInAlarmDatabase() {
//        /*List<RoutineEntity> routineEntities=routineRepository.findAllByTakeDateAndTakeTimeBetweenWithMedicine(
//                LocalDate.of(2025, 3, 24),
//                LocalTime.of(8, 0, 0),
//                LocalTime.of(22, 0, 0)
//            );*/
//        List<RoutineEntity> routineEntities=routineRepository.findAllByTakeDateAndTakeTimeBetweenWithMedicine(
//                LocalDate.now(),
//                LocalTime.now(),
//                LocalTime.now().plusHours(1)
//        );
//
//        log.info("routine scheduling check: {}", routineEntities.size());
//
//        /**
//         * 반환해야 할 값 user_id, schedule_name, medicineNames, take_time, nok_id
//         * */
//        routineEntities.forEach(routine -> {
//            Long userId = routine.getUser().getId();
//
//            String scheduleName=routine.getUserSchedule().getName();
//
//            List<String> medicineNames = routine.getRoutineMedicines().stream().map(routineMedicine -> {
//                return medicineDocumentService.findMedicineDocumentById(routineMedicine.getMedicineId()).getItemName();
//            }).toList();
//
//            LocalDateTime takeTime= LocalDateTime.of(routine.getTakeDate(), routine.getUserSchedule().getTakeTime());
//
//            AlarmDto alarmData=AlarmDto.builder()
//                    .userId(userId)
//                    .scheduleName(scheduleName)
//                    .medicineNames(medicineNames)
//                    .takeTime(takeTime)
//                    .status("scheduled")
//                    .build()
//                    ;
//
//            try {
//                String alarmDataJson = objectMapper.writeValueAsString(alarmData);
//                String redisKey = "alarm";
//
//                long score =  takeTime.toEpochSecond(ZoneOffset.UTC);
//
//                log.info("알람 스케줄러 시간대 디버깅: {}", score);
//
//                String member = userId + ":" + score;
//
//                // redis zset에 추가 - 같은 값은 중복 저장 x
//                redisTemplateAlarm.opsForZSet().add(redisKey, member, score);
//                // zset 멤버에 해당하는 json 데이터 저장
//                redisTemplateAlarm.opsForValue().set("alarm_data:" + member, alarmDataJson);
//
//                log.info("Saved alarm data in Redis ZSET: {}", alarmDataJson);
//            }catch (JsonProcessingException e){
//                throw new ApiException(SchedulerError.SERVER_ERROR, "JSON PROCESSING ERROR");
//            }
//        });
//    }
}
