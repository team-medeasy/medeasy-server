package com.medeasy.common.filter;

import com.medeasy.common.error.TokenErrorCode;
import com.medeasy.common.error.UserErrorCode;
import com.medeasy.common.exception.ApiException;
import com.medeasy.domain.auth.util.TokenHelperIfs;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final TokenHelperIfs jwtTokenHelper;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String header = request.getHeader("Authorization");

        if (header == null || !header.startsWith("Bearer ")) {
            throw new ApiException(TokenErrorCode.AUTHORIZATION_TOKEN_NOT_FOUND);
        }

        String token = header.substring(7);
        // 토큰 검증 및 클레임 추출
        Map<String, Object> claims = jwtTokenHelper.validationTokenWithThrow(token);

        Object userIdObject = claims.get("userId");

        if(userIdObject == null){
            throw new ApiException(UserErrorCode.USER_NOT_FOUNT);
        }

        Long userId = Long.parseLong(userIdObject.toString());
        log.info("클레임 추출 user_id: {}", userId);

        try {
            // 인증 객체 생성
            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(userId, null, Collections.emptyList());

            // 인증 객체 저장
            SecurityContextHolder.getContext().setAuthentication(authentication);

        } catch (JwtException | IllegalArgumentException e) {
            // 토큰 검증 실패 시 SecurityContext 초기화
            SecurityContextHolder.clearContext();
            throw new ApiException(TokenErrorCode.ERROR_CREATE_AUTHORIZATION);
        }
        filterChain.doFilter(request, response);
    }
}
