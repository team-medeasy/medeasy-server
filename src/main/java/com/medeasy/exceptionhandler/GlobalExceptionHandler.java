package com.medeasy.exceptionhandler;

import com.medeasy.common.api.Api;
import com.medeasy.common.error.ErrorCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice // 예외처리 가져오기
@Order(Integer.MAX_VALUE) // 값이 높을 수록 낮은 우선순위 즉 나중에 실행 //디폴트가 맥스
public class GlobalExceptionHandler {

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Api<Object>> handleInvalidJson(HttpMessageNotReadableException ex) {
        Api<Object> errorResponse = Api.ERROR(ErrorCode.BAD_REQEUST, "요청 형식이 올바르지 않습니다 (JSON 파싱 오류)");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    @ExceptionHandler(value=Exception.class) // 예외 전부 //커스텀 예외로 앞에서 다 잡기 때문에 이곳은 예상치 못한 예외 잡힘
    public ResponseEntity<Api<Object>> exception(
            Exception exception //스프링이 주입
    ) {
        log.error("", exception); // 스택 트레이스를 통해서 로그 수집

        return ResponseEntity // 예외시 처리
                .status(500)
                .body(
                        Api.ERROR(ErrorCode.SERVER_ERROR)
                );
    }
}
