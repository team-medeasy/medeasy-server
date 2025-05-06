//package com.medeasy.config;
//
//import com.medeasy.domain.chat.handler.ChatWebSocketHandler;
//import com.medeasy.interceptor.AuthHandshakeInterceptor;
//import lombok.RequiredArgsConstructor;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.web.socket.config.annotation.EnableWebSocket;
//import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
//import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
//
//@Configuration
//@EnableWebSocket
//@RequiredArgsConstructor
//public class WebSocketConfig implements WebSocketConfigurer {
//
//    private final ChatWebSocketHandler chatWebSocketHandler;
//    private final AuthHandshakeInterceptor authHandshakeInterceptor;
//    @Override
//    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
//        registry.addHandler(chatWebSocketHandler, "/ws/chat")
//                .addInterceptors(authHandshakeInterceptor);
//    }
//}

