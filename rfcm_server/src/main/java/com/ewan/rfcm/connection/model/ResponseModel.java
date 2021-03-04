package com.ewan.rfcm.connection.model;

import lombok.Data;

@Data
public class ResponseModel<T> {
    private boolean error;
    private String errorMsg = "";
    private T responseData;
}
