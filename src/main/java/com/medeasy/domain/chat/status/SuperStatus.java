package com.medeasy.domain.chat.status;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.Optional;

/**
 * 루틴 등록 필요한 데이터 수집 중 .. 이런 거랑
 * 루틴 등록이랑 분리해서
 *
 * 루틴 등록이 나올시 바로 루틴 등록 메서드 실행되게끔
 * */
@Getter
@AllArgsConstructor
public enum SuperStatus implements ChatStatusIfs{
    BASIC("basic", "아무 기능 안 하는 기본 상태", 0),
    ROUTINE_REGISTER("routine_register","루틴 등록 기능", 1),
    SCHEDULE_MANAGE("schedule_manage","스케줄 관리 기능", 1),
    MEDICINE_SEARCH("medicine_search", "약 정보 검색 기능",1)
    ;

    private final String intent;
    private final String description;
    private final Integer level;

    public static Optional<SuperStatus> fromIntent(String intent) {
        return Arrays.stream(SuperStatus.values())
                .filter(status -> status.getIntent().equalsIgnoreCase(intent))
                .findFirst();
    }
}
