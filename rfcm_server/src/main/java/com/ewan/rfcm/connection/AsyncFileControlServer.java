package com.ewan.rfcm.connection;

import com.ewan.rfcm.connection.model.WebsocketRequestType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.AsynchronousChannelGroup;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;

public class AsyncFileControlServer implements Runnable {

    private static final Logger logger = LoggerFactory.getLogger(AsyncFileControlServer.class);

    private AsynchronousChannelGroup channelGroup;
    private AsynchronousServerSocketChannel serverSocketChannel;
    public static final ConcurrentHashMap<String, AsyncFileControlClient> connections = new ConcurrentHashMap<>();

    @Override
    public void run() {
        startServer();
    }

    public void startServer(){
        logger.info("[Async Server] Start server");
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
        serverSocketChannel.accept(null, new CompletionHandler<AsynchronousSocketChannel, Void>() {
            @Override
            public void completed(AsynchronousSocketChannel socketChannel, Void attachment) {
                try {
                    String address = socketChannel.getRemoteAddress().toString().substring(1);
                    AsyncFileControlClient client = new AsyncFileControlClient(socketChannel);
                    connections.put(address, client);
                    serverSocketChannel.accept(null, this);
                    WebSocketHandler.sendClientInfo(address, WebsocketRequestType.ADD);

                    logger.info("[Async Server] Accept connection : " + address);
                    logger.info("[Async Server] Connection count : " + connections.size());
                } catch (IOException e) {
                    logger.error("[Async Server] Fail to accept client", e);
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
            logger.info("[Async Server] Close server ...");
        } catch (IOException e) {
            logger.error("[Async Server] Fail to stop server", e);
        }
    }

    public static AsyncFileControlClient getClient(String ip){
        return connections.getOrDefault(ip, null);
    }
}
