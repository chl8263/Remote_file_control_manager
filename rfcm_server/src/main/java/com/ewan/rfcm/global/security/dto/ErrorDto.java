package com.ewan.rfcm.global.security.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
public class ErrorDto {

    private String errorCode;
    private String errorMsg;

    public ErrorDto(String errorCode, String errorMsg){
        this.errorCode = errorCode;
        this.errorMsg = errorMsg;
    }

}
