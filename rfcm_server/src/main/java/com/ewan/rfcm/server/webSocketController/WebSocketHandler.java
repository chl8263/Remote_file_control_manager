package com.ewan.rfcm.server.webSocketController;

import com.ewan.rfcm.server.FileControlServer;
import com.ewan.rfcm.server.model.FileControlClient;
import com.ewan.rfcm.server.model.WebSocketReqDto;
import com.ewan.rfcm.server.protocol.MessagePacker;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;

@Component
public class WebSocketHandler extends TextWebSocketHandler {

    private final ObjectMapper objectMapper;

    public WebSocketHandler(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) {
        try {
            String payload = message.getPayload();
            System.out.println("payload : " + payload);
            TextMessage textMessage = new TextMessage("Welcome !");
            session.sendMessage(textMessage);

            WebSocketReqDto result = objectMapper.readValue(payload, WebSocketReqDto.class);
            System.out.println(result);

            switch (result.getReqType()){
                case GET_CONNECTIONS:{
                    System.out.println("현재 클라이언트 갯수 : " + FileControlServer.connections.size());
                    for(FileControlClient client : FileControlServer.connections){
                        System.out.println("Client -> " + client.getSocketChannel().getRemoteAddress());

                        Selector selector = client.getSelector();
                        SelectionKey selectionKey = client.getSocketChannel().keyFor(selector);

                        MessagePacker msg = new MessagePacker();
                        int v1 = 1;
                        int v2 = 2;
                        int v3 = 3;
                        msg.add(v1);
                        msg.add(v2);
                        msg.add(v3);
                        msg.add("Message test for this project.");
                        byte [] data = msg.Finish();

                        client.setSendData(data);
                        selectionKey.interestOps(SelectionKey.OP_WRITE);
                        selector.wakeup();
                    }
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }


    }
}
