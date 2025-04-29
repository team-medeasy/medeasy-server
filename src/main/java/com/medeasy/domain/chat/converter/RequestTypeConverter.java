package com.medeasy.domain.chat.converter;

import com.medeasy.common.annotation.Converter;
import com.medeasy.common.error.ErrorCode;
import com.medeasy.common.exception.ApiException;
import com.medeasy.domain.chat.request_type.BasicRequestType;
import com.medeasy.domain.chat.request_type.RequestTypeIfs;
import com.medeasy.domain.chat.request_type.RoutineRequestType;

import java.util.Arrays;

@Converter
public class RequestTypeConverter {
    public RequestTypeIfs mapBasicRequestType(String requestType) {
        return Arrays.stream(BasicRequestType.values())
                .filter(e -> e.getType().equalsIgnoreCase(requestType))
                .findFirst()
                .orElseThrow(() -> new ApiException(ErrorCode.BAD_REQEUST, "Invalid request_type"));
    }

    public RequestTypeIfs mapRoutineRequestType(String requestType) {
        return Arrays.stream(RoutineRequestType.values())
                .filter(e -> e.getType().equalsIgnoreCase(requestType))
                .findFirst()
                .orElseThrow(() -> new ApiException(ErrorCode.BAD_REQEUST, "Invalid request_type"));
    }
}
