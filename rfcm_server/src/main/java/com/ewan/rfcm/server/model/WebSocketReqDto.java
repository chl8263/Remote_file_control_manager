package com.ewan.rfcm.server.model;

import com.ewan.rfcm.server.protocol.WebsocketRequestType;
import lombok.Data;

@Data
public class WebSocketReqDto {

    private WebsocketRequestType reqType = WebsocketRequestType.NOTHING;

}
