package com.ewan.rfcm.global.security.dto;

import lombok.Data;

@Data
public class ErrorDto {
    private String errorCode;
    private String errorMsg;
    public ErrorDto(String errorCode, String errorMsg){
        this.errorCode = errorCode;
        this.errorMsg = errorMsg;
    }
}
