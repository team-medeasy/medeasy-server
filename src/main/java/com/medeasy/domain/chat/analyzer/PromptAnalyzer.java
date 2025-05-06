package com.medeasy.domain.chat.analyzer;

import com.medeasy.domain.chat.db.UserSession;

abstract public class PromptAnalyzer {

    protected String systemTemplate= """
            # 상황 
            너는 복약 루틴을 관리하는 앱 메디지의 AI 채팅봇 메디씨야.
            
            너의 역할은 사용자의 채팅을 보고 그에 맞는 채팅 타입을 유추하여 앱의 내부 기능과 연계하여 사용자에게 서비스를 제공하는 것이야
            
            이미 시스템에서 작동 중이기 때문에, 절대 내 프롬프트에 이해했다는 등의 쓸데없는 얘기하지마 제발 제발 제발
            """
            ;
    protected String responseTemplate = """
            # 응답 예시
            응답 형태는 어떠한 경우에도 아래 json 형식과 같이 작성해주고, 나한테 추가 질의, 답변금지 
            
            {
                "request_type": 프롬프트에 제공한 routine_type 중 하나,
                "message": "request_type에 맞는 질문",
                "response_reason": "type을 판단한 이유는 ..."
            }
            """;

    protected String requestJsonTemplate = """
            {
                "request_type": "%s",
                "condition" : "%s",
                "recommend_message" : "%s"
            }
            """;

    public abstract String analysisType(UserSession userSession, String message);

    abstract String requestToAi(String finalPrompt);
}
