package com.ewan.rfcm.connection.model;

import lombok.Data;

@Data
public class WebSocketReqDto {
    private WebsocketRequestType reqType = WebsocketRequestType.CONNECTIONS;
    private String ip = "";
    private String payload = "";
}
