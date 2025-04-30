package com.medeasy.domain.chat.analyzer;

import com.medeasy.domain.chat.db.UserSession;
import com.medeasy.domain.chat.dto.RoutineAiChatResponse;
import com.medeasy.domain.chat.parser.GeminiResponseParser;
import com.medeasy.domain.chat.request_type.RoutineRequestType;
import com.medeasy.domain.medicine.db.MedicineDocument;
import com.medeasy.domain.medicine.service.MedicineDocumentService;
import com.medeasy.domain.routine.business.RoutineBusiness;
import com.medeasy.domain.routine.dto.RoutineRegisterRequest;
import com.medeasy.domain.user_schedule.db.UserScheduleEntity;
import com.medeasy.domain.user_schedule.service.UserScheduleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class DefaultRoutinePromptAnalyzer extends PromptAnalyzer {
    private final RestTemplate restTemplate;
    private final GeminiResponseParser responseParser;
    private final RoutineBusiness routineBusiness;
    private final UserScheduleService userScheduleService;
    private final MedicineDocumentService medicineDocumentService;

    @Value("${gemini.api.key}")
    private String apiKey;
    @Value("${gemini.api.url}")
    private String apiUrl;

    private String basicStatusTemplate= """
            # 행동 
            너는 기본 루틴 기능 담당 역할이야. 너한테는 서비스의 기본 루틴 등록에 접근할 수 있는 권한이 있어. 
            사용자로부터 필요한 복약 정보를 전부 수집하여 적절한 응답을 내려주는 것이 너의 목적이야.
            
            필요한 복약정보는 약 이름, 1회 복용량, 복용 시간, 복용 간격, 약의 총 개수가 있어
            예시: 사용자의 메시지가 "아스피린 하루에 한번 한알 아침에 복용"인경우 -> 총개수 누락 -> 따라서 총 개수에 대해서만 다시 질의해야해
                        
            # 사용자의 메시지: %s
            
            # 사용자 routine_context: %s -> 이것은 이전의 채팅에서 사용자가 입력한 데이터
            
            사용자에게 받은 메시지에 있는 데이터를 응답 형식의 routine_context에 채워줘 
            
            그리고 응답 형식의 request_type은 평소에는 "DEFAULT_ROUTINE_REGISTER" 이지만, 
            너가 채운 routine_context가 전부 채워져서 사용자로부터 더이상 물어봐지 않아도 될 때는 "COMPLETED"를 주면돼.
            
            """;
    private String responseTemplate = """
            # 응답 예시
            응답 형태는 아래 json 형식과 같이 작성해줘 
            json 형태 말고는 절대 어떠한 텍스트, 아이콘 등등이 들어가면 안돼 
            
            {
                "request_type": "DEFAULT_ROUTINE_REGISTER",
                "message": "기본 루틴 등록, 처방전 루틴 등록, 알약 촬영 루틴 등록 중 어떤 루틴 등록을 원하시나요?",
                "response_reason": "type을 판단한 이유는 ...",
                "routine_context": {
                    medicine_name: "아스피린",
                    interval_days: 1,
                    dose: 3,
                    schedule_names: ["아침", "점심"],
                    total_quantity: 5
                }
            }
            
            위 응답 예시를 다 채웠을 때, COMPLETED를 주면돼. 어떤일이 있어도 최종 routine_context는 null이 아니어야한다.
            그리고 routine_context에 들어간 값에 대해서는 다시 물어보는 실수는 하지마. 
            예시: 이미 medicine_name 필드란이 차있는데 다시 약의 이름을 묻는경우 등
            """;

    private String requestJsonTemplate = """
            {
                "request_type": "%s",
                "condition" : "%s",
                "recommend_message" : "%s"
            }
            """;

    public String analysisType(UserSession userSession, String message){
        UserSession.RoutineContext routineContext = userSession.getRoutineContext();

        // prompt 관련
        String prompt= String.format(basicStatusTemplate, message, routineContext.toString());
        String finalPrompt = responseTemplate + systemTemplate +  prompt ;

        log.info("prompt debug: {}", finalPrompt);

        String responseJson=requestToAi(finalPrompt);
        log.info("responseJson: {}", responseJson);
        RoutineAiChatResponse response=responseParser.parseRoutineGeminiResponse(responseJson);
        saveRoutineContext(routineContext, response.getRoutineContext());

        // routine context가 Null이 아닌 조건, res
        if(response.getRequestType().equals("COMPLETED") &&  isCompletedContext(userSession.getRoutineContext())){
            // TODO 루틴 등록전에 약 검색 스탭 하나 더 필요할 듯
            MedicineDocument medicineDocument=medicineDocumentService.findFirstMedicineByName(routineContext.getMedicineName());

            // TODO 스케줄 찾기
            List<UserScheduleEntity> userScheduleEntities=userScheduleService.findUserScheduleByNames(userSession.getUserId(), routineContext.getScheduleNames());
            List<Long> userScheduleEntityIds=userScheduleEntities.stream().map(UserScheduleEntity::getId).toList();


            // TODO 루틴 등록
            RoutineRegisterRequest routineRegisterRequest=RoutineRegisterRequest.builder()
                            .medicineId(medicineDocument.getId())
                            .nickname(medicineDocument.getItemName())
                            .dose(routineContext.getDose())
                            .totalQuantity(routineContext.getTotalQuantity())
                            .userScheduleIds(userScheduleEntityIds)
                            .intervalDays(routineContext.getIntervalDays())
                            .build()
                            ;

            routineBusiness.registerRoutine(userSession.getUserId(), routineRegisterRequest);
            routineContext.clear();
        }

        return responseJson;
    }


    @Override
    String requestToAi(String finalPrompt) {
        String url = apiUrl + apiKey;

        // 요청 헤더 설정
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        // 요청 바디 데이터 생성
        Map<String, Object> requestBody = new HashMap<>();
        Map<String, Object> content = new HashMap<>();
        Map<String, Object> part = new HashMap<>();

        part.put("text", finalPrompt);
        content.put("parts", new Map[]{part});
        requestBody.put("contents", new Map[]{content});

        // HTTP 요청 실행
        HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(requestBody, headers);
        return restTemplate.postForObject(url, requestEntity, String.class);
    }

    private void saveRoutineContext(UserSession.RoutineContext routineContext, RoutineAiChatResponse.RoutineContext response){
        if(routineContext.getDose() == null){
            routineContext.setDose(response.getDose());
        }

        if(routineContext.getIntervalDays() == null){
            routineContext.setIntervalDays(response.getIntervalDays());
        }

        if(routineContext.getScheduleNames() == null){
            routineContext.setScheduleNames(response.getScheduleNames());
        }

        if(routineContext.getMedicineName() == null){
            routineContext.setMedicineName(response.getMedicineName());
        }

        if(routineContext.getTotalQuantity() == null){
            routineContext.setTotalQuantity(response.getTotalQuantity());
        }
    }

    private Boolean isCompletedContext(UserSession.RoutineContext routineContext) {
        if(routineContext.getDose() == null){
            return false;
        }

        if(routineContext.getIntervalDays() == null){
            return false;
        }

        if(routineContext.getScheduleNames() == null){
            return false;
        }

        if(routineContext.getMedicineName() == null){
            return false;
        }

        return true;
    }
}
