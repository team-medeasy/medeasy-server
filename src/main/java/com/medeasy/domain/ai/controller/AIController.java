package com.medeasy.domain.ai.controller;

import com.medeasy.common.api.Api;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/ai")
@RequiredArgsConstructor
public class AIController {

    @Operation(summary = "(미구현) AI 채팅 기능 리스트 조회 api", description =
            """
                "AI 채팅 기능 리스트 조회: AI 챗봇으로 수행할 수 있는 기능 리스트를 제공한다.
            
                반환값: 기능 이름, 기능 호출 엔드포인트
            """
    )
    @GetMapping("/chat")
    public Api<Object> getRoutineListByDate(
    ) {
        return null;
    }
}
