package com.medeasy.domain.alarm.dto;

import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AlarmDto {

    private Long userId;

    private String scheduleName;

    private List<String> medicineNames;

    private LocalDateTime takeTime;

    private String status;

    //TODO nok 추가
}
