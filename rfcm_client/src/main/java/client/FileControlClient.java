package client;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousChannelGroup;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.nio.charset.Charset;
import java.util.concurrent.Executors;

public class FileControlClient {

    AsynchronousChannelGroup channelGroup;
    AsynchronousSocketChannel socketChannel;

    public void startClient(){
        try {
            channelGroup = AsynchronousChannelGroup.withFixedThreadPool(
                    Runtime.getRuntime().availableProcessors(),
                    Executors.defaultThreadFactory()
            );

            socketChannel = AsynchronousSocketChannel.open(channelGroup);
            socketChannel.connect(new InetSocketAddress("127.0.0.1", 15000), null,
                    new CompletionHandler<Void, Void>() {
                        @Override
                        public void completed(Void result, Void attachment) {
                            try {
                                System.out.println("[연결 완료 : " + socketChannel.getRemoteAddress());


                            } catch (IOException e) {

                            }
                            receive();
                        }

                        @Override
                        public void failed(Throwable exc, Void attachment) {
                            System.out.println("[서버와 통신 안됨]");
                            if(socketChannel.isOpen()) {
                                stopClient();
                            }
                        }
                    });

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void stopClient(){
        try {
            System.out.println("[연결 끊음]");
            if(channelGroup != null && !channelGroup.isShutdown()){
                channelGroup.shutdownNow();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void receive(){
        ByteBuffer byteBuffer = ByteBuffer.allocate(100);
        socketChannel.read(byteBuffer, byteBuffer,
                new CompletionHandler<Integer, ByteBuffer>() {
                    @Override
                    public void completed(Integer result, ByteBuffer attachment) {
                        attachment.flip();
                        Charset charset = Charset.forName("UTF-8");
                        String data = charset.decode(attachment).toString();
                        System.out.println("[받기 완료 : " + data + " ]");

                        ByteBuffer byteBuffer = ByteBuffer.allocate(100);
                        socketChannel.read(byteBuffer, byteBuffer, this);
                    }

                    @Override
                    public void failed(Throwable exc, ByteBuffer attachment) {
                        System.out.println("[서버 에서 읽기 실패]");
                        ByteBuffer byteBuffer = ByteBuffer.allocate(100);
                        socketChannel.read(byteBuffer, byteBuffer, this);
                    }
                }
        );
    }

    public void send(String data){
        Charset charset = Charset.forName("UTF-8");
        ByteBuffer byteBuffer = charset.encode(data);
        socketChannel.write(byteBuffer, null,
                new CompletionHandler<Integer, Void>() {
                    @Override
                    public void completed(Integer result, Void attachment) {
                        System.out.println("[보내기 완료 : " + data + " ]");
                    }

                    @Override
                    public void failed(Throwable exc, Void attachment) {
                        System.out.println("[서버로 보내기 실패]");
                        stopClient();
                    }
                });
    }

}
