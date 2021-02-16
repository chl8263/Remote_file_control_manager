package com.ewan.rfcm.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Set;
import java.util.Vector;

public class Client {

    private static final Logger log = LoggerFactory.getLogger(Client.class);

    private SocketChannel socketChannel;
    private String sendData;
    Selector selector;

    private Set<Client> connections;

    public Client(SocketChannel socketChannel, Selector selector, Set<Client> connections){
        try {
            this.selector  = selector;
            this.socketChannel = socketChannel;
            this.connections = connections;
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
            Charset charset = Charset.forName("UTF-8");
            String data = charset.decode(byteBuffer).toString();

            String message = "[요청 처리 : " + socketChannel.getRemoteAddress() + " : " + Thread.currentThread().getName() +
                    " 내용 : " + data + "]";
            log.info(message);

        } catch (Exception e) {
            try {
                connections.remove(this);
                String message = "[클라이언트 통신 안됨 : " + socketChannel.getRemoteAddress() + " : " + Thread.currentThread().getName() + "]";
                System.out.println(message);
                socketChannel.close();
            } catch (Exception e2) {
            }
        }
    }

    public void send(SelectionKey selectionKey){
        try {
            connections.remove(this);
            String message = "[클라이언트 통신 안됨 : " + socketChannel.getRemoteAddress() + " : " + Thread.currentThread().getName() + "]";
            System.out.println(message);
            socketChannel.close();
        } catch (Exception e2) {
        }
    }
}
