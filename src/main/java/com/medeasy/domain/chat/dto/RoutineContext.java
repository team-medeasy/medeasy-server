package com.medeasy.domain.chat.dto;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class RoutineContext {
    private String medicineName;

    private Integer intervalDays;

    private Integer dose;

    private List<String> scheduleNames;
}
