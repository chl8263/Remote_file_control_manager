package com.ewan.rfcm.server.model;

import com.ewan.rfcm.server.protocol.MessageRequestProtocol;

public class SocketResponseDto {
    public int socketRequestType = MessageRequestProtocol.GET_WHOLE_FOLDER;
    public Object object = new Object();
}
