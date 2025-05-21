package com.medeasy.domain.routine.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RoutineCheckEvent implements Serializable {
    private static final long serialVersionUID = 1L; // 클래스 버전 관리

    // 기본 이벤트 정보
    private String eventId;

    private String scheduleName;

    // 복약 체크 정보
    private Long userId;
    private LocalDateTime checkedAt;
}
