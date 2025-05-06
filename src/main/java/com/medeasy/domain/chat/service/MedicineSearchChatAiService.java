package com.medeasy.domain.chat.service;

import com.medeasy.domain.chat.analyzer.CommonPromptAnalyzer;
import com.medeasy.domain.chat.prompt.PromptTemplate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class MedicineSearchChatAiService {
    private final CommonPromptAnalyzer commonPromptAnalyzer;

    public String analyzerMedicineNameFromMessage(String message) {
        String response=commonPromptAnalyzer.requestToAi(
                PromptTemplate.SYSTEM_PROMPT.getContent(),
                PromptTemplate.ANALYSIS_MEDICINE_NAME_RESPONSE_TEMPLATE.getContent(),
                "",
                "",
                ""
        );

        return response;
    }
}
