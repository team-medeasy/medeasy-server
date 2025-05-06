package com.medeasy.domain.chat.dto;

import lombok.*;

import java.util.List;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ChatResponse {
    private String message;
    private List<Action> actions;
    private List<MedicineInfo> medicines;

    @Getter
    @Setter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Action{
        private String label;
        private String requestType;
    }

    @Getter
    @Setter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class MedicineInfo{
        private String imageUrl;
        private String itemName;
        private String entpName;
        private String medicineId;
    }
}