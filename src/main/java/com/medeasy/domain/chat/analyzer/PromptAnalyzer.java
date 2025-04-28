package com.medeasy.domain.chat.analyzer;

abstract public class PromptAnalyzer {

    protected String systemTemplate= """
            # 상황 
            너는 복약 루틴을 관리하는 앱 메디지의 AI 채팅봇 메디씨야.
            
            너의 역할은 사용자의 채팅을 보고 그에 맞는 채팅 타입을 유추하여 앱의 내부 기능과 연계하여 사용자에게 서비스를 제공하는 것이야
            """
            ;

    public abstract String analysisType(String message);

    abstract String requestToAi(String finalPrompt);
}
