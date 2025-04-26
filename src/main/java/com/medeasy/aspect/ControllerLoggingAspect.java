package com.medeasy.aspect;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Aspect
@Component
@Slf4j
public class ControllerLoggingAspect {

    @Pointcut("execution(public * com.medeasy..controller..*(..))")
    public void controllerMethods() {}

    @Around("controllerMethods()")
    public Object logControllerMethods(ProceedingJoinPoint joinPoint) throws Throwable {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        String requestUrl = request.getRequestURI();

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Object principal = authentication != null ? authentication.getPrincipal() : null;
        String userId = principal != null ? principal.toString() : "anonymous";

        log.info("요청 URL: {}, 사용자 ID: {}", requestUrl, userId);

        Object result = joinPoint.proceed();

        if (!requestUrl.startsWith("/open-api/auth")) {
            log.info("요청 완료 URL: {}, 사용자 ID: {}, 응답 결과: {}", requestUrl, userId, result);
        } else {
            log.info("요청 완료 URL: {}, 사용자 ID: {} (응답 결과 미출력)", requestUrl, userId);
        }

        return result;
    }
}
