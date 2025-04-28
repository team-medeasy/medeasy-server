package com.medeasy.domain.chat.message_creator;

import com.medeasy.domain.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class BasicMessageCreator implements MessageCreator{
    private final UserService userService;

    public String helloMessage(Long userId) {
        String userName=userService.getUserById(userId).getName();

        String template = """
                안녕하세요! %s님 챗봇 메디씨입니다. 무엇을 도와드릴까요? """;

        return String.format(template, userName);
    }
}
