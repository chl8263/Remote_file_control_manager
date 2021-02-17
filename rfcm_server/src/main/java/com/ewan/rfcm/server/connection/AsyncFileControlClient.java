package com.ewan.rfcm.server.connection;

import com.ewan.rfcm.server.AsyncFileControlServer;
import com.ewan.rfcm.server.FileControlServer;
import com.ewan.rfcm.server.protocol.MessagePacker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.concurrent.BlockingQueue;

public class AsyncFileControlClient {

    private static final Logger log = LoggerFactory.getLogger(AsyncFileControlClient.class);

    AsynchronousSocketChannel socketChannel;
    BlockingQueue<String> queue;

    public AsyncFileControlClient(AsynchronousSocketChannel socketChannel, BlockingQueue<String> queue){
        this.socketChannel = socketChannel;
        this.queue = queue;
        //receive();
    }

    public void receive(){
        try{
            ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
            socketChannel.read(byteBuffer, byteBuffer,
                    new CompletionHandler<Integer, ByteBuffer>() {
                        @Override
                        public void completed(Integer result, ByteBuffer attachment) {
                            try {
                                attachment.flip();
                                //                        Charset charset = Charset.forName("UTF-8");
                                //                        String data = charset.decode(attachment).toString();

                                ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
                                socketChannel.read(byteBuffer, byteBuffer, this);

                                attachment.flip();
                                byte [] byteArr = attachment.array();
                                MessagePacker msg = new MessagePacker(byteArr);

                                byte protocol = msg.getProtocol();
                                int payloadLength = msg.getInt() ;
                                String payload = (String) msg.getObject(payloadLength);

                                queue.put(payload);

                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void failed(Throwable exc, ByteBuffer attachment) {
                            try {
                                FileControlServer.connections.remove(socketChannel.getRemoteAddress().toString().substring(1));
                                String message = "[클라이언트 통신 안됨 : " + socketChannel.getRemoteAddress() + " : " + Thread.currentThread().getName() + "]";
                                System.out.println(message);
                                AsyncFileControlServer.connections.remove(socketChannel.getRemoteAddress().toString().substring(1));
                                socketChannel.close();
                            } catch (Exception e2) {
                            }
                        }
                    });
        }catch (Exception e){}

    }

    public void send(byte[] sendData){
        ByteBuffer byteBuffer = ByteBuffer.wrap(sendData);
        socketChannel.write(byteBuffer, byteBuffer, new CompletionHandler<Integer, ByteBuffer>() {
            @Override
            public void completed(Integer result, ByteBuffer attachment) {
                try {
                    receive();

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void failed(Throwable exc, ByteBuffer attachment) {
                try {
                    FileControlServer.connections.remove(socketChannel.getRemoteAddress().toString().substring(1));
                    String message = "[클라이언트 통신 안됨 : " + socketChannel.getRemoteAddress() + " : " + Thread.currentThread().getName() + "]";
                    System.out.println(message);
                    AsyncFileControlServer.connections.remove(socketChannel.getRemoteAddress().toString().substring(1));
                    socketChannel.close();
                } catch (Exception e2) {
                }
            }
        });
    }

    public AsynchronousSocketChannel getSocketChannel() {
        return socketChannel;
    }

    public BlockingQueue<String> getQueue(){
        return this.queue;
    }
}
