package com.ewan.rfcm.server.webSocketController;

import com.ewan.rfcm.server.AsyncFileControlServer;
import com.ewan.rfcm.server.FileControlServer;
import com.ewan.rfcm.server.model.FileControlClient;
import com.ewan.rfcm.server.model.WebSocketReqDto;
import com.ewan.rfcm.server.protocol.MessagePacker;
import com.ewan.rfcm.server.protocol.MessageProtocol;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import static com.ewan.rfcm.server.protocol.WebsocketRequestType.CONNECTIONS;

@Component
public class WebSocketHandler extends TextWebSocketHandler {

    private static final Logger log = LoggerFactory.getLogger(WebSocketHandler.class);

    private final ObjectMapper objectMapper;
    public static HashMap<String, WebSocketSession> sessionList;

    public WebSocketHandler(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
        sessionList = new HashMap<>();
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        log.info("[Websocket] 접속 : " + session.getId());
        sessionList.put(session.getId(), session);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        log.info("[Websocket] 접속 해제 : " + session.getId());
        sessionList.remove(session.getId());
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) {
        try {
            String payload = message.getPayload();
            System.out.println("payload : " + payload);
//            TextMessage textMessage = new TextMessage("Welcome !");
//            session.sendMessage(textMessage);

            WebSocketReqDto requestData = objectMapper.readValue(payload, WebSocketReqDto.class);
            System.out.println(requestData);

            switch (requestData.getReqType()){
                case CONNECTIONS:{
                    sendConnectedClientInfo(session);
                    break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void sendConnectedClientInfo(WebSocketSession session){

        try {
            WebSocketReqDto resultObj = new WebSocketReqDto();
            String temp = "";

            //Iterator<String> keys = AsyncFileControlServer.connections.keySet().iterator();
            for( String key : AsyncFileControlServer.connections.keySet() ){
                System.out.println(key);
                temp += AsyncFileControlServer.connections.get(key).getSocketChannel().getRemoteAddress();
            }

            System.out.println(temp);

            resultObj.setReqType(CONNECTIONS);
            resultObj.setPayload(temp);

            ObjectMapper objectMapper = new ObjectMapper();
            String result = "";
            result = objectMapper.writeValueAsString(resultObj);

            TextMessage textMessage = new TextMessage(result);
            session.sendMessage(textMessage);

        } catch (Exception e) {
            try {
                log.info("[Websocket] 전송실패로 접속 해제 : " + session.getId());
                session.close();
            } catch (IOException ioException) { }
        }
    }
}
