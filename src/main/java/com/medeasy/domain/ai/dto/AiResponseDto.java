package com.medeasy.domain.ai.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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

    public static class DoseDto{
        @JsonProperty("name")
        private String name;
        @JsonProperty("dose")
        private Integer dose;
        @JsonProperty("type_count")
        private Integer typeCount;
        @JsonProperty("total_days")
        private Integer totalDays;
        @JsonProperty("use_method")
        private String useMethod;
    }
}
