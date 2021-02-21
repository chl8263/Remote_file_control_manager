package com.ewan.rfcm.global.security.error;

public class InvalidJwtException extends RuntimeException {
    public InvalidJwtException(String msg){
        super(msg);
    }
}
