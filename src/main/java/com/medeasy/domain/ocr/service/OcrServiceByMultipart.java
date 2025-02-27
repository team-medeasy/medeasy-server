package com.medeasy.domain.ocr.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.medeasy.domain.ocr.dto.OcrParsedDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class OcrServiceByMultipart{

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    @Value("${ocr.api.url}")
    private String ocrApiUrl;

    @Value("${ocr.secret}")
    private String ocrSecret;

    public List<OcrParsedDto> sendOcrRequest(MultipartFile file) {
        try {
            // 1. message JSON 객체 생성
            Map<String, Object> message = new HashMap<>();
            message.put("version", "V2");
            message.put("requestId", UUID.randomUUID().toString()); // 랜덤 UUID 생성
            message.put("timestamp", System.currentTimeMillis()); // 현재 타임스탬프
            message.put("lang", "ko");
            message.put("enableTableDetection", false); // 표 인식 여부

            Map<String, String> images = new HashMap<>();
            images.put("format", "pdf");
            images.put("name", "uploaded_file");

            message.put("images", new Object[]{images});


            // JSON -> String 변환
            String messageJson = objectMapper.writeValueAsString(message);

            // 2. Multipart 요청 데이터 생성
            MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
            body.add("message", messageJson); // JSON을 text로 추가
            body.add("file", file.getResource()); // MultipartFile을 파일로 추가

            // 3. HTTP 요청 헤더 설정
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.MULTIPART_FORM_DATA);
            headers.set("X-OCR-SECRET", ocrSecret);
            headers.set("Content-Type", "multipart/form-data");

            // 4. 요청 엔티티 생성
            HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

            // 5. REST API 호출
            ResponseEntity<String> response = restTemplate.exchange(
                    ocrApiUrl,
                    HttpMethod.POST,
                    requestEntity,
                    String.class
            );

            List<OcrParsedDto> ocrParsedDtos=parseOcrResponseOnlyMedicineInformation(response.getBody());
            return ocrParsedDtos;

        } catch (Exception e) {
            log.error("OCR 요청 중 오류 발생", e);
            throw new RuntimeException("OCR 요청 실패", e);
        }
    }

    private List<OcrParsedDto> parseOcrResponse(String responseJson) {
        List<OcrParsedDto> extractedData = new ArrayList<>();

        try {
            JsonNode root = objectMapper.readTree(responseJson);
            JsonNode imagesNode = root.path("images");

            for (JsonNode imageNode : imagesNode) {
                JsonNode fieldsNode = imageNode.path("fields");

                for (JsonNode fieldNode : fieldsNode) {
                    String text = fieldNode.path("inferText").asText();

                    List<OcrParsedDto.OcrVertexDto> boundingPoly = new ArrayList<>();
                    JsonNode boundingPolyNode = fieldNode.path("boundingPoly").path("vertices");

                    for (JsonNode vertexNode : boundingPolyNode) {
                        double x = vertexNode.path("x").asDouble();
                        double y = vertexNode.path("y").asDouble();
                        boundingPoly.add(new OcrParsedDto.OcrVertexDto(x, y));
                    }

                    OcrParsedDto parsedDto = new OcrParsedDto();
                    parsedDto.setText(text);
                    parsedDto.setBoundingPoly(boundingPoly);

                    extractedData.add(parsedDto);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("OCR 응답 파싱 중 오류 발생", e);
        }

        return extractedData;
    }

    /*
    * 의약품 정보만 파싱
    * */
    private List<OcrParsedDto> parseOcrResponseOnlyMedicineInformation(String responseJson) {
        List<OcrParsedDto> extractedData = new ArrayList<>();
        boolean startParsing = false;

        try {
            JsonNode root = objectMapper.readTree(responseJson);
            JsonNode imagesNode = root.path("images");

            for (JsonNode imageNode : imagesNode) {
                JsonNode fieldsNode = imageNode.path("fields");

                for (JsonNode fieldNode : fieldsNode) {
                    String text = fieldNode.path("inferText").asText();

                    // "의약품"이 등장하면 이후부터 저장 시작
                    if (!startParsing && text.contains("의약품")) {
                        startParsing = true;
                    }

                    if (startParsing) {
                        List<OcrParsedDto.OcrVertexDto> boundingPoly = new ArrayList<>();
                        JsonNode boundingPolyNode = fieldNode.path("boundingPoly").path("vertices");

                        for (JsonNode vertexNode : boundingPolyNode) {
                            double x = vertexNode.path("x").asDouble();
                            double y = vertexNode.path("y").asDouble();
                            boundingPoly.add(new OcrParsedDto.OcrVertexDto(x, y));
                        }

                        OcrParsedDto parsedDto = new OcrParsedDto();
                        parsedDto.setText(text);
                        parsedDto.setBoundingPoly(boundingPoly);

                        extractedData.add(parsedDto);
                    }
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("OCR 응답 파싱 중 오류 발생", e);
        }

        return extractedData;
    }
}
