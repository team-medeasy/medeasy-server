package com.medeasy.domain.routine.dto;

import com.medeasy.domain.user_schedule.dto.UserScheduleDto;
import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RoutinePrescriptionResponse {
    private String imageUrl;

    private String medicineId;

    private String medicineName;

    private String entpName;

    private int dose;

    private int totalQuantity;

    private int totalDays;

    private String className;

    private String etcOtcName;

    private List<UserScheduleDto> userSchedules;

    private List<Integer> dayOfWeeks;
}
