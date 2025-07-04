package com.medeasy.domain.routine.business;

import com.medeasy.domain.routine.dto.RoutineRegisterRequest;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Component
public class RoutineCalculatorByDayOfWeeks implements RoutineCalculator{
    /**
     * 3/16
     * 약을 복용할 날짜 구하기
     * */
    public List<LocalDate> calculateRoutineDates(LocalDate startDate, int scheduleSize, RoutineRegisterRequest routineRegisterRequest) {
        int dailyDose=scheduleSize * routineRegisterRequest.getDose();

        int requiredDays=(int) Math.ceil((double) routineRegisterRequest.getTotalQuantity()/dailyDose)+1; // 반올림

        List<LocalDate> dates = new ArrayList<>();
        LocalDate currentDate = startDate;

        while(dates.size() < requiredDays) {
            int todayDayValue = currentDate.getDayOfWeek().getValue();

            if(routineRegisterRequest.getDayOfWeeks().contains(todayDayValue)) {
                dates.add(currentDate);
            }

            currentDate = currentDate.plusDays(1);
        }

        return dates;
    }
}
