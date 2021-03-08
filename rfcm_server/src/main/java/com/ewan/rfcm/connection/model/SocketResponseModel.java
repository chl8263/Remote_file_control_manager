package com.ewan.rfcm.connection.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SocketResponseModel {
    private String uid;
    private String responseData;
}
