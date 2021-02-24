package model.info;

import lombok.Data;

@Data
public class ResponseModel<T> {
    private boolean error;
    private String errorMsg = "";
    private T responseData;
}
