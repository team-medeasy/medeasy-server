package com.medeasy.domain.chat.dto;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
/**
 * 응답 객체 기준으로 ai 응답 파싱하기 때문에, 이 부분만 수정해줘도 됨.
 * */
public class RoutineAiChatResponse {
    private String requestType;
    private String message;
    private String responseReason;
    private RoutineContext routineContext;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class RoutineContext {
        private String medicineName;

        private Integer intervalDays;

        private Integer dose;

        private List<String> scheduleNames;

        private Integer totalQuantity;
    }
}
