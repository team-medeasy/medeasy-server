package com.medeasy.domain.user_schedule.db;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalTime;

@Getter
@AllArgsConstructor
public enum MedicationTime {

    MORNING_BEFORE_MEAL("아침 식사 전", LocalTime.of(7, 30)),
    MORNING_AFTER_MEAL("아침 식사 후", LocalTime.of(8, 30)),
    LUNCH_BEFORE_MEAL("점심 식사 전", LocalTime.of(11, 30)),
    LUNCH_AFTER_MEAL("점심 식사 후", LocalTime.of(12, 30)),
    DINNER_BEFORE_MEAL("저녁 식사 전", LocalTime.of(17, 30)),
    DINNER_AFTER_MEAL("저녁 식사 후", LocalTime.of(18, 30)),
    BEFORE_SLEEP("취침 전", LocalTime.of(22, 0));

    private String name;

    private LocalTime takeTime;

}
