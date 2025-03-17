package com.medeasy.domain.ai.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class AiResponseDto {
    @JsonProperty("totalTokenCount")
    private Integer totalTokenCount;
    @JsonProperty("results")
    private List<DoseDto> doseDtos;

    @Getter
    @Setter
    @ToString
    public static class DoseDto{
        @JsonProperty("edi_code")
        private String ediCode;
        @JsonProperty("name")
        private String name;
        @JsonProperty("dose")
        private Integer dose; // 1회 투약량
        @JsonProperty("schedule_count")
        private Integer scheduleCount; // 1일 투여횟수
        @JsonProperty("total_days")
        private Integer totalDays; // 총 투약일수
        @JsonProperty("use_method")
        private String useMethod;
    }
}
