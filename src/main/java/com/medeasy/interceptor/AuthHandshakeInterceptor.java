package com.medeasy.interceptor;

import com.medeasy.common.error.TokenErrorCode;
import com.medeasy.common.error.UserErrorCode;
import com.medeasy.common.exception.AuthException;
import com.medeasy.domain.auth.util.TokenHelperIfs;
import lombok.RequiredArgsConstructor;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class AuthHandshakeInterceptor implements HandshakeInterceptor {

    private final TokenHelperIfs jwtTokenHelper;

    @Override
    public boolean beforeHandshake(ServerHttpRequest request,
                                   ServerHttpResponse response,
                                   WebSocketHandler wsHandler,
                                   Map<String, Object> attributes) throws Exception {

        String header=request.getHeaders().getFirst("Authorization");

        if (header == null || !header.startsWith("Bearer ")) {
            throw new AuthException(TokenErrorCode.AUTHORIZATION_TOKEN_NOT_FOUND);
        }

        String token = header.substring(7);

        // 토큰 검증 및 클레임 추출
        Map<String, Object> claims = jwtTokenHelper.validationTokenWithThrow(token);
        Object userIdObject = claims.get("userId");

        if(userIdObject == null){
            throw new AuthException(UserErrorCode.USER_NOT_FOUNT);
        }
        attributes.put("userId", userIdObject);

        return true;
    }

    @Override
    public void afterHandshake(ServerHttpRequest request,
                               ServerHttpResponse response,
                               WebSocketHandler wsHandler,
                               Exception exception) {
        // Handshake 끝난 후 작업 (거의 안 씀)
    }
}

