package com.ewan.rfcm.server;

import com.ewan.rfcm.server.model.FileControlClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.*;

public class FileControlServer implements Runnable {

    private static final Logger log = LoggerFactory.getLogger(FileControlServer.class);

    private Selector selector;
    private ServerSocketChannel serverSocketChannel;
    public static final Set<FileControlClient> connections = new HashSet<>();
    @Override
    public void run() {
        startServer();
    }

    public void startServer(){
        try{
            log.info("[TCP] Start server");

            selector = Selector.open();
            serverSocketChannel = ServerSocketChannel.open();
            serverSocketChannel.configureBlocking(false);
            serverSocketChannel.bind(new InetSocketAddress(15000));
            serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);

            while (true){
                int keyCount = selector.select();
                if(keyCount == 0) continue;
                Set<SelectionKey> selectionKeys = selector.selectedKeys();
                Iterator<SelectionKey> iterator = selectionKeys.iterator();

                while (iterator.hasNext()){
                    SelectionKey selectionKey = iterator.next();

                    if(selectionKey.isAcceptable()){
                        accept(selectionKey);

                    }else if(selectionKey.isReadable()){
                        FileControlClient client = (FileControlClient) selectionKey.attachment();
                        client.receive(selectionKey);

                    }else if(selectionKey.isWritable()){
                        FileControlClient client = (FileControlClient) selectionKey.attachment();
                        client.send(selectionKey);
                    }
                    iterator.remove();
                }
            }

        } catch (IOException e) {
            if(serverSocketChannel.isOpen()){
                stopServer();
                // TODO: 정상 종료일때와 아닐때를 구분하여 사용자에게 전달
                return;
            }
        }
    }

    public void stopServer(){

    }

    public void accept(SelectionKey selectionKey){
        try{
            ServerSocketChannel serverSocketChannel = (ServerSocketChannel) selectionKey.channel();
            SocketChannel socketChannel = serverSocketChannel.accept();

            log.info("[TCP] Connection accept");

            FileControlClient client = new FileControlClient(socketChannel, selector);
            connections.add(client);

            log.info("[TCP] Connection count : " + connections.size());

        } catch (IOException e) {
            if(serverSocketChannel.isOpen()){
                stopServer();
            }
        }
    }
}
