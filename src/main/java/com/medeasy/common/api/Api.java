package com.medeasy.common.api;

import com.medeasy.common.error.ErrorCodeIfs;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Api<T> { // Json 모양

    private Result result;

    @Valid
    private T body;

    public static <T> Api<T> OK(T data){
        var api=new Api<T>();
        api.result=Result.OK();
        api.body=data;
        return api;
    }

    public static Api<Object> ERROR(Result result){
        var api=new Api<Object>();
        api.result=result;
        return api;
    }

    public static Api<Object> ERROR(ErrorCodeIfs errorCodeIfs){
        var api=new Api<Object>();
        api.result=Result.ERROR(errorCodeIfs);
        return api;
    }

    public static Api<Object> ERROR(ErrorCodeIfs errorCodeIfs, Throwable throwable){
        var api=new Api<Object>();
        api.result=Result.ERROR(errorCodeIfs, throwable);
        return api;
    }

    public static Api<Object> ERROR(ErrorCodeIfs errorCodeIfs, String description){
        var api=new Api<Object>();
        api.result=Result.ERROR(errorCodeIfs, description);
        return api;
    }

}
