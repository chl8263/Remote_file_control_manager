package com.ewan.rfcm.server;

import com.ewan.rfcm.server.connection.AsyncFileControlClient;
import com.ewan.rfcm.server.model.FileControlClient;
import com.ewan.rfcm.server.webSocketController.WebSocketHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.AsynchronousChannelGroup;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.HashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

public class AsyncFileControlServer implements Runnable {

    private static final Logger log = LoggerFactory.getLogger(AsyncFileControlServer.class);

    AsynchronousChannelGroup channelGroup;
    AsynchronousServerSocketChannel serverSocketChannel;
    public static final HashMap<String, AsyncFileControlClient> connections = new HashMap<>();

    @Override
    public void run() {
        startServer();
    }

    public void startServer(){
        try {
            channelGroup = AsynchronousChannelGroup.withFixedThreadPool(
                    Runtime.getRuntime().availableProcessors(),
                    Executors.defaultThreadFactory()
            );

            serverSocketChannel = AsynchronousServerSocketChannel.open(channelGroup);
            serverSocketChannel.bind(new InetSocketAddress(15000));

        } catch (IOException e) {
            if(serverSocketChannel.isOpen()) {
                stopServer();
            }
            return;
        }

        log.info("[Async Server] 서버시작");

        serverSocketChannel.accept(null, new CompletionHandler<AsynchronousSocketChannel, Void>() {
            @Override
            public void completed(AsynchronousSocketChannel socketChannel, Void attachment) {
                try {
                    log.info("[Async Server] 연결 수락 : " + socketChannel.getRemoteAddress());
                    AsyncFileControlClient client = new AsyncFileControlClient(socketChannel, new LinkedBlockingQueue<>());
                    connections.put(client.getSocketChannel().getRemoteAddress().toString().substring(1), client);
                    log.info("[Async Server] 연결 갯수 : " + connections.size());

                    serverSocketChannel.accept(null, this);

                    // Send new client information to connected web socket session
                    WebSocketHandler.sendWholeClientInfoToWholeWebSocket();

                } catch (IOException e) {
                    // Send new client information to connected web socket session
                    WebSocketHandler.sendWholeClientInfoToWholeWebSocket();
                }
            }

            @Override
            public void failed(Throwable exc, Void attachment) {
                if(serverSocketChannel.isOpen()){
                    stopServer();
                }
            }
        });
    }

    public void stopServer(){
        try {
            connections.clear();
            if(channelGroup != null && !channelGroup.isShutdown()){
                channelGroup.shutdownNow();
            }
            log.info("[Async Server] 서버 멈춤");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static AsyncFileControlClient getClient(String ip){
        if(connections.containsKey(ip)){
            return connections.get(ip);
        }else return null;
    }

}
