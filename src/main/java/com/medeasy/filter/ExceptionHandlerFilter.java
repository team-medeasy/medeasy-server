package com.medeasy.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.medeasy.common.api.Api;
import com.medeasy.common.error.ErrorCode;
import com.medeasy.common.error.ErrorCodeIfs;
import com.medeasy.common.error.TokenErrorCode;
import com.medeasy.common.exception.ApiException;
import com.medeasy.common.exception.AuthException;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.TransactionRolledbackException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
@Component
public class ExceptionHandlerFilter extends OncePerRequestFilter {

    @Autowired
    private ObjectMapper objectMapper;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        try{
            filterChain.doFilter(request, response);
        }catch (ExpiredJwtException e){
            setErrorResponse(response, TokenErrorCode.EXPIRED_TOKEN);
        }catch (JwtException | IllegalArgumentException e){
            setErrorResponse(response, TokenErrorCode.INVALID_TOKEN);
        }catch (AuthException e) {  // ApiException 처리 추가
            log.error("API 예외 발생: {}", e.getMessage());
            setErrorResponse(response, ErrorCode.BAD_REQEUST, e.getMessage());
        }
    }
    private void setErrorResponse(
            HttpServletResponse response,
            ErrorCodeIfs errorCode
    ){
        response.setStatus(errorCode.getHttpStatusCode());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");

        try{
            response.getWriter().write(objectMapper.writeValueAsString(Api.ERROR(errorCode)));
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    private void setErrorResponse(
            HttpServletResponse response,
            ErrorCodeIfs errorCode,
            String message
    ){
        response.setStatus(errorCode.getHttpStatusCode());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");

        try{
            response.getWriter().write(objectMapper.writeValueAsString(Api.ERROR(errorCode, message)));
        }catch (IOException e){
            e.printStackTrace();
        }
    }
}
