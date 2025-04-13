package com.medeasy.domain.routine.business;

import com.medeasy.domain.routine.dto.RoutineRegisterRequest;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Component
public class RoutineCalculatorByInterval implements RoutineCalculator {
    public List<LocalDate> calculateRoutineDates(LocalDate startDate, int scheduleSize, RoutineRegisterRequest routineRegisterRequest) {
        int dailyDose=scheduleSize * routineRegisterRequest.getDose();

        int requiredDays=(int) Math.ceil((double) routineRegisterRequest.getTotalQuantity()/dailyDose); // 반올림

        List<LocalDate> dates = new ArrayList<>();
        LocalDate currentDate = startDate;

        while(dates.size() < requiredDays) {
            dates.add(currentDate);
            currentDate = currentDate.plusDays(routineRegisterRequest.getIntervalDays());
        }

        return dates;
    }
}
