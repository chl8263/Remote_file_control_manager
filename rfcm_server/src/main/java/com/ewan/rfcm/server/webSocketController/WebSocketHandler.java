package com.ewan.rfcm.server.webSocketController;

import com.ewan.rfcm.server.AsyncFileControlServer;
import com.ewan.rfcm.server.FileControlServer;
import com.ewan.rfcm.server.model.FileControlClient;
import com.ewan.rfcm.server.model.WebSocketReqDto;
import com.ewan.rfcm.server.protocol.MessagePacker;
import com.ewan.rfcm.server.protocol.MessageProtocol;
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
                    WebSocketReqDto resultObj = new WebSocketReqDto();
                    String temp = "";

                    Iterator<String> keys = AsyncFileControlServer.connections.keySet().iterator();
                    for( String key : AsyncFileControlServer.connections.keySet() ){
                        System.out.println(key);
                        temp += AsyncFileControlServer.connections.get(key).getSocketChannel().getRemoteAddress();
                    }

                    System.out.println(temp);

//                    for(FileControlClient client : FileControlServer.connections){
//                        temp += client.getSocketChannel().getRemoteAddress();
//                    }

                    resultObj.setReqType(CONNECTIONS);
                    resultObj.setPayload(temp);

                    String result = objectMapper.writeValueAsString(resultObj);

                    TextMessage textMessage = new TextMessage(result);
                    session.sendMessage(textMessage);
                    break;
                }
                case ROOT_PATHS:{
                    System.out.println(requestData.getIp());
                    FileControlClient client = FileControlServer.connections.get(requestData.getIp());

                    Selector selector = client.getSelector();
                    if(selector != null) {
                        SelectionKey selectionKey = client.getSocketChannel().keyFor(selector);

                        System.out.println("[보내기 : 루트파일 경로좀 주세요ㅋ]" + session.getId() + "이새끼한테 줄거임 ㅋ");
                        MessagePacker msg = new MessagePacker();
                        msg.setEndianType(ByteOrder.BIG_ENDIAN); // Default type in JVM
                        msg.setProtocol(MessageProtocol.ROOT_DIRECTORY);
                        msg.addString(session.getId());
                        byte[] data = msg.Finish();

                        client.setSendData(data);
                        selectionKey.interestOps(SelectionKey.OP_WRITE);
                        selector.wakeup();
                    }
                    break;
                }
                case PATHS:{
                    System.out.println("현재 클라이언트 갯수 : " + FileControlServer.connections.size());
//                    for(FileControlClient client : FileControlServer.connections){
//                        System.out.println("Client -> " + client.getSocketChannel().getRemoteAddress());
//
//                        Selector selector = client.getSelector();
//                        SelectionKey selectionKey = client.getSocketChannel().keyFor(selector);
//
//                        System.out.println("[보내기 : 파일경로좀 ㅋ]");
//                        MessagePacker msg = new MessagePacker();
//                        msg.setEndianType(ByteOrder.BIG_ENDIAN); // Default type in JVM
//                        msg.setProtocol(MessageProtocol.FOLDER_LIST);
////                        int v1 = 1;
////                        int v2 = 2;
////                        int v3 = 3;
////                        msg.add(v1);
////                        msg.add(v2);
////                        msg.add(v3);
////                        msg.add("Message test for this project.");
//                        byte [] data = msg.Finish();
//
//                        client.setSendData(data);
//                        selectionKey.interestOps(SelectionKey.OP_WRITE);
//                        selector.wakeup();
//                    }
                    break;
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }


    }
}
