package com.medeasy.common.error;

public interface ErrorCodeIfs {
    Integer getHttpStatusCode();

    Integer getErrorCode();

    String getDescription();
}
