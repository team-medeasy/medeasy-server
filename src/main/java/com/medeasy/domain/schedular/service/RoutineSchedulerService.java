package com.medeasy.domain.schedular.service;

import com.medeasy.domain.routine.db.RoutineEntity;
import com.medeasy.domain.routine.db.RoutineRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Service
@Slf4j
public class RoutineSchedulerService {

    private final RoutineRepository routineRepository;
    private final StringRedisTemplate redisTemplateAlarm;

    @Autowired
    public RoutineSchedulerService(
            RoutineRepository routineRepository, RoutineRepository routineRepository1,
            @Qualifier("alarmRedisTemplate") StringRedisTemplate redisTemplateAlarm
    ) {
        this.routineRepository = routineRepository1;
        this.redisTemplateAlarm = redisTemplateAlarm;
    }


    @Scheduled(fixedRate = 600000)
    public void saveRoutineInAlarmDatabase() {
        List<RoutineEntity> routineEntities=routineRepository.findAllByTakeDateAndTakeTimeBetweenWithMedicine(
                LocalDate.of(2025, 3, 19),
                LocalTime.of(8, 0, 0),
                LocalTime.of(22, 0, 0)
            );

        log.info("routine scheduling check: {}", routineEntities.size());
    }
}
