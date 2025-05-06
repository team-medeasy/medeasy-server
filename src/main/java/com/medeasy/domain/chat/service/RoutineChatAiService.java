package com.medeasy.domain.chat.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.medeasy.common.api.Api;
import com.medeasy.domain.chat.db.UserSession;
import com.medeasy.domain.chat.dto.ChatResponse;
import com.medeasy.domain.chat.dto.RoutineAiChatResponse;
import com.medeasy.domain.chat.parser.GeminiResponseParser;
import com.medeasy.domain.chat.analyzer.PromptAnalyzer;
import com.medeasy.domain.medicine.db.MedicineDocument;
import com.medeasy.domain.medicine.service.MedicineDocumentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class RoutineChatAiService {

    private final GeminiResponseParser geminiResponseParser;
    private final ObjectMapper objectMapper;
    private final MedicineSearchChatAiService medicineSearchChatAiService;
    private final MedicineDocumentService medicineDocumentService;


    public String registerDefaultRoutine(PromptAnalyzer promptAnalyzer, UserSession userSession, String clientMessage) {
        ChatResponse chatResponse=null;
        try {
            UserSession.RoutineContext routineContext=userSession.getRoutineContext();
            if(routineContext.getStep() == null){
                chatResponse=ChatResponse.builder()
                        .message("복용 스케줄 등록하시려는 의약품 이름을 말씀해주세요!")
                        .build()
                        ;

                routineContext.setStep(1);
            }

            /**
             * TODO 디테일한 부분 추가 필요
             * 1. 사용자 의약품 선택 단계
             * */
            if (routineContext.getStep() == 1) {
                String medicineName=medicineSearchChatAiService.analyzerMedicineNameFromMessage(clientMessage);
                List<MedicineDocument> medicineDocumentList= medicineDocumentService.findFirstMedicineByName(medicineName, 5);

                List<ChatResponse.MedicineInfo> medicineInfos=medicineDocumentList.stream().map(medicineDocument -> {
                    return ChatResponse.MedicineInfo.builder()
                            .itemName(medicineDocument.getItemName())
                            .medicineId(medicineDocument.getId())
                            .imageUrl(medicineDocument.getItemImage())
                            .entpName(medicineDocument.getEntpName())
                            .build()
                            ;
                }).toList();

                chatResponse= ChatResponse.builder()
                        .message("맞는 의약품을 선택해주세요!")
                        .medicines(medicineInfos)
                        .build()
                        ;

                routineContext.setStep(2);
            }

            /**
             * 2. 의약품 선택 검증 및 스케줄 선택
             * */
            if (routineContext.getStep() == 2) {
                String medicineName=clientMessage;
                chatResponse= ChatResponse.builder()
                        .message("하루 중 언제 복용 예정이신가요?")
                        .build()
                ;

                routineContext.setStep(3);
            }







            userSession.getMessages().add(clientMessage);
            String aiJsonResponse = promptAnalyzer.analysisType(userSession, clientMessage);
            RoutineAiChatResponse routineAiChatResponse=geminiResponseParser.parseRoutineGeminiResponse(aiJsonResponse);

            userSession.setPastRequestType(routineAiChatResponse.getRequestType());

//            ChatResponse chatResponse=ChatResponse.builder()
//                    .message(routineAiChatResponse.getMessage())
//                    .build()
//                    ;

            var response=Api.OK(chatResponse);
            return objectMapper.writeValueAsString(response);
        } catch (Exception e) {
            log.error("AI 분석 및 파싱 실패", e);
        }
        return null;
    }

    // TODO 처방전 루틴 등록
    public RoutineAiChatResponse registerPrescriptionRoutine() {
        return null;
    }

    // TODO 알약 사진 루틴 등록
    public RoutineAiChatResponse registerPillsPhotoRoutine() {
        return null;
    }

    private String responseMessageCreator(UserSession userSession) {
        UserSession.RoutineContext routineContext=userSession.getRoutineContext();
        if (routineContext.getMedicineName() != null) {

        }
        return null;
    }

    public void routineProcessDetailSelector(PromptAnalyzer promptAnalyzer, UserSession userSession, String clientMessage) {
//        AiChatResponse aiChatResponse=doRequest(promptAnalyzer, userSession, clientMessage);
//
//        log.info("type 판단 이유 디버깅 {}", aiChatResponse.getResponseReason());
//        ChatResponse chatResponse=ChatResponse.builder()
//                .message(aiChatResponse.getMessage())
//                .clientAction(null)
//                .requestType(aiChatResponse.getRequestType())
//                .build()
//                ;
//
//        var response= Api.OK(chatResponse);
//        String responseJson = objectMapper.writeValueAsString(response);
//        session.sendMessage(new TextMessage(responseJson));
//
//        userSession.setPastRequestType(aiChatResponse.getRequestType());
    }
}
