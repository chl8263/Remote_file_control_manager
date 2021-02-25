package com.ewan.rfcm.connection;

import com.ewan.rfcm.connection.model.WebSocketReqDto;
import com.ewan.rfcm.connection.model.WebsocketRequestType;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.HashMap;

@Component
public class WebSocketHandler extends TextWebSocketHandler {

    private static final Logger logger = LoggerFactory.getLogger(WebSocketHandler.class);

    private final ObjectMapper objectMapper;
    public static HashMap<String, WebSocketSession> sessionList;

    public WebSocketHandler(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
        sessionList = new HashMap<>();
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        logger.info("[Websocket] connection : {}", session.getId());
        sessionList.put(session.getId(), session);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        logger.info("[Websocket] disconnection : {}", session.getId());
        sessionList.remove(session.getId());
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) {
        try {
            String payload = message.getPayload();
            WebSocketReqDto requestData = objectMapper.readValue(payload, WebSocketReqDto.class);
            switch (requestData.getReqType()){
                case CONNECTIONS:{
                    sendConnectedClientInfo(session);
                    break;
                }
            }
        } catch (IOException e) {
            logger.error("[Websocket] Failed to handle message from websocket client : {}", session.getId());
        }
    }

    public static void sendConnectedClientInfo(WebSocketSession session){
        try {
            for( String key : AsyncFileControlServer.connections.keySet() ){
                String address = String.valueOf(AsyncFileControlServer.connections.get(key).getSocketChannel().getRemoteAddress()).substring(1);

                WebSocketReqDto resultObj = new WebSocketReqDto();

                resultObj.setReqType(WebsocketRequestType.ADD);
                resultObj.setPayload(address);

                ObjectMapper objectMapper = new ObjectMapper();
                String result = "";
                result = objectMapper.writeValueAsString(resultObj);

                TextMessage textMessage = new TextMessage(result);
                synchronized (session) {
                    session.sendMessage(textMessage);
                }
            }
        } catch (Exception e) {
            try {
                logger.info("[Websocket] Disconnection because of failed to send : {}", session.getId());
                session.close();
            } catch (IOException ioException) {
                logger.error("[Websocket] Failed to close websocket session : {}", session.getId());
            }
        }
    }

    public static void sendClientInfo(String address, WebsocketRequestType requestType){
        try {
            for(String key : WebSocketHandler.sessionList.keySet()){
                WebSocketSession webSocketSession =  WebSocketHandler.sessionList.get(key);

                WebSocketReqDto resultObj = new WebSocketReqDto();
                resultObj.setReqType(requestType);
                resultObj.setPayload(address);

                ObjectMapper objectMapper = new ObjectMapper();
                String result = "";
                result = objectMapper.writeValueAsString(resultObj);

                TextMessage textMessage = new TextMessage(result);
                synchronized (webSocketSession) {
                    webSocketSession.sendMessage(textMessage);
                }
            }
        } catch (IOException e) {
            logger.error("[Websocket] Failed to send client info to websocket session");
        }
    }
}
