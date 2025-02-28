package com.medeasy.domain.user.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserScheduleResponse {
    private LocalTime morning;

    private LocalTime lunch;

    private LocalTime dinner;

    private LocalTime bedTime;
}
