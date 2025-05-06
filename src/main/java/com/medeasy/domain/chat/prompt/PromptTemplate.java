package com.medeasy.domain.chat.prompt;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.stream.Collectors;

@Getter
@AllArgsConstructor
public enum PromptTemplate {
    SYSTEM_PROMPT(
        "SYSTEM_PROMPT",
            "메디지 기본 시스템 프롬프트",
            """
            # 상황 
            너는 복약 루틴을 관리하는 앱 메디지의 AI 채팅봇 메디씨야.
            
            너의 역할은 사용자의 채팅을 보고 그에 맞는 채팅 타입을 유추하여 앱의 내부 기능과 연계하여 사용자에게 서비스를 제공하는 것이야
            
            이미 시스템에서 작동 중이기 때문에, 절대 내 프롬프트에 이해했다는 등의 쓸데없는 얘기하지마 제발 제발 제발
            """
    ),

    ANALYSIS_MEDICINE_NAME_RESPONSE_TEMPLATE(
            "ANALYSIS_MEDICINE_NAME_RESPONSE_TEMPLATE",
            "사용자 메시지로부터 약 이름 추출",
            """
                    사용자의 메시지로부터 약 이름만 추출해줘
                    절대 응답형식 외에는 대답, 텍스트, 아이콘 등등을 절대 포함하지마.
                    
                    응답형식: 
                    {
                        "medicine_name": "약 이름" 
                    }
                    """
    ),


    ;

    private final String code;

    private final String name;

    private final String content;

    /**
     * 여러 템플릿을 조합하여 최종 프롬프트 생성
     * @param templates 조합할 템플릿 목록
     * @return 조합된 프롬프트 내용
     */
    public static String combineTemplates(PromptTemplate... templates) {
        return Arrays.stream(templates)
                .map(PromptTemplate::getContent)
                .collect(Collectors.joining("\n\n"));
    }

    /**
     * 코드로 프롬프트 템플릿 찾기
     * @param code 찾을 템플릿 코드
     * @return 해당 코드의 템플릿 (없으면 null)
     */
    public static PromptTemplate findByCode(String code) {
        return Arrays.stream(values())
                .filter(template -> template.getCode().equals(code))
                .findFirst()
                .orElse(null);
    }
}
