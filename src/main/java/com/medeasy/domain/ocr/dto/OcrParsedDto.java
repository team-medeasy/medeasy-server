package com.medeasy.domain.ocr.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OcrParsedDto {
    private String text;
    List<OcrVertexDto> boundingPoly;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OcrVertexDto {
        private double x;
        private double y;
    }
}

