package com.ewan.rfcm.server.model;

import com.ewan.rfcm.server.FileControlServer;
import com.ewan.rfcm.server.protocol.MessagePacker;
import com.ewan.rfcm.server.protocol.MessageProtocol;
import com.ewan.rfcm.server.webSocketController.WebSocketHandler;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.client.WebSocketClient;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;

import static com.ewan.rfcm.server.protocol.WebsocketRequestType.CONNECTIONS;
import static com.ewan.rfcm.server.protocol.WebsocketRequestType.ROOT_PATHS;

public class FileControlClient {

    private static final Logger log = LoggerFactory.getLogger(FileControlClient.class);

    private ObjectMapper objectMapper = new ObjectMapper();

    private SocketChannel socketChannel;
    private byte[] sendData;
    private Selector selector;


    public FileControlClient(SocketChannel socketChannel, Selector selector){
        try {
            this.selector  = selector;
            this.socketChannel = socketChannel;
            socketChannel.configureBlocking(false);
            SelectionKey selectionKey = socketChannel.register(selector, SelectionKey.OP_READ);
            selectionKey.attach(this);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void receive(SelectionKey selectionKey){
        try{
            ByteBuffer byteBuffer = ByteBuffer.allocate(9999);
            int byteCount = socketChannel.read(byteBuffer);
            if(byteCount == -1){
                throw new IOException();
            }

            byteBuffer.flip();
//            Charset charset = Charset.forName("UTF-8");
//            String data = charset.decode(byteBuffer).toString();
            byte [] byteArr = byteBuffer.array();
            MessagePacker msg = new MessagePacker(byteArr);
            byte protocol = msg.getProtocol();

            switch (protocol){
                case MessageProtocol.ROOT_DIRECTORY:{
                    System.out.println("[받기 완료 : 루트 파일 경로 줘라에 대한 답변 ]");
                    String sessionId = msg.getString();
                    System.out.println("[받기 완료 : 근데 누구였드라 ]" + sessionId);
                    int payloadLength = msg.getInt() ;
                    System.out.println("페이로드 길이 : " + payloadLength);
                    String payload = (String) msg.getObject(payloadLength);
                    System.out.println("페이로드  : " + payload);

                    WebSocketSession session = WebSocketHandler.sessionList.get(sessionId);

                    WebSocketReqDto resultObj = new WebSocketReqDto();
                    resultObj.setReqType(ROOT_PATHS);
                    resultObj.setPayload(payload);

                    String result = objectMapper.writeValueAsString(resultObj);
                    TextMessage textMessage = new TextMessage(result);
                    session.sendMessage(textMessage);
                }
            }

//            String message = "[요청 처리 : " + socketChannel.getRemoteAddress() + " : " + Thread.currentThread().getName() +
//                    " 내용 : " + data + "]";
//            log.info(message);

        } catch (Exception e) {
            try {
                FileControlServer.connections.remove(socketChannel.getRemoteAddress().toString().substring(1));
                String message = "[클라이언트 통신 안됨 : " + socketChannel.getRemoteAddress() + " : " + Thread.currentThread().getName() + "]";
                log.info(message);
                socketChannel.close();

                // Send new client information to connected web socket session
                WebSocketHandler.sendWholeClientInfoToWholeWebSocket();

            } catch (Exception e2) {
            }
        }
    }

    public void send(SelectionKey selectionKey){
        try {
//            Charset charset = Charset.forName("UTF-8");
//            ByteBuffer byteBuffer = charset.encode("!!!!!!!!!!!!");
            ByteBuffer byteBuffer = ByteBuffer.wrap(sendData);//sendData;
            //byteBuffer.flip();
            socketChannel.write(byteBuffer);
            selectionKey.interestOps(SelectionKey.OP_READ);
            selector.wakeup();

        } catch (Exception e) {
            try {
                FileControlServer.connections.remove(socketChannel.getRemoteAddress().toString().substring(1));
                String message = "[클라이언트 통신 안됨 : " + socketChannel.getRemoteAddress() + " : " + Thread.currentThread().getName() + "]";
                log.info(message);
                socketChannel.close();

                // Send new client information to connected web socket session
                WebSocketHandler.sendWholeClientInfoToWholeWebSocket();

            } catch (Exception e2) {
            }
        }
    }

    public SocketChannel getSocketChannel(){
        return this.socketChannel;
    }

    public Selector getSelector(){
        return this.selector;
    }

    public void setSendData(byte[] sendData){
        this.sendData = sendData;
    }
}
