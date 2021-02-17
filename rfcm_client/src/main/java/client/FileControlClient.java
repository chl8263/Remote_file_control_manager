package client;

import model.ServerInfo;
import protocol.MessagePacker;
import protocol.MessageProtocol;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.AsynchronousChannelGroup;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.concurrent.Executors;

public class FileControlClient {

    private AsynchronousChannelGroup channelGroup;
    private AsynchronousSocketChannel socketChannel;

    private final ServerInfo serverInfo;

    public FileControlClient(ServerInfo serverInfo){
        this.serverInfo = serverInfo;
    }

    public void startClient(){
        try {
            channelGroup = AsynchronousChannelGroup.withFixedThreadPool(
                    Runtime.getRuntime().availableProcessors(),
                    Executors.defaultThreadFactory()
            );

            socketChannel = AsynchronousSocketChannel.open(channelGroup);
            socketChannel.connect(new InetSocketAddress(serverInfo.getIp(), serverInfo.getPort()), null,
                    new CompletionHandler<Void, Void>() {
                        @Override
                        public void completed(Void result, Void attachment) {
                            try {
                                System.out.println("[연결 완료 : " + socketChannel.getRemoteAddress() + "]");

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
        ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
        socketChannel.read(byteBuffer, byteBuffer,
                new CompletionHandler<Integer, ByteBuffer>() {
                    @Override
                    public void completed(Integer result, ByteBuffer attachment) {
                        //attachment.flip();
                        byte [] byteArr = attachment.array();
                        MessagePacker receivedMsg = new MessagePacker(byteArr);
                        byte protocol = receivedMsg.getProtocol();

                        switch (protocol){
                            case MessageProtocol.ROOT_DIRECTORY:{

                                MessagePacker sendMsg = new MessagePacker();
                                sendMsg.setEndianType(ByteOrder.BIG_ENDIAN); // Default type in JVM
                                sendMsg.setProtocol(MessageProtocol.ROOT_DIRECTORY);

                                String responseData = FileProvider.getDirectoryInRoot();
                                sendMsg.add(responseData);

                                byte [] sendData = sendMsg.Finish();
                                send(ByteBuffer.wrap(sendData));

                                break;
                            }

                            case MessageProtocol.DIRECTORY:{

                                String path = receivedMsg.getString();

                                MessagePacker sendMsg = new MessagePacker();
                                sendMsg.setEndianType(ByteOrder.BIG_ENDIAN); // Default type in JVM
                                sendMsg.setProtocol(MessageProtocol.ROOT_DIRECTORY);

                                String responseData = FileProvider.getUnderLineDirectory(path);

                                sendMsg.add(responseData);

                                byte [] sendData = sendMsg.Finish();
                                send(ByteBuffer.wrap(sendData));

                                break;
                            }
                        }

                        ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
                        socketChannel.read(byteBuffer, byteBuffer, this);
                    }

                    @Override
                    public void failed(Throwable exc, ByteBuffer attachment) {
                        System.out.println("[서버 에서 읽기 실패]");
                        stopClient();
                    }
                }
        );
    }

    public void send(ByteBuffer byteBuffer){
//        Charset charset = Charset.forName(Encoding.UTF_8);
//        ByteBuffer byteBuffer = charset.encode(data);
        socketChannel.write(byteBuffer, null,
                new CompletionHandler<Integer, Void>() {
                    @Override
                    public void completed(Integer result, Void attachment) {
                        System.out.println("[보내기 완료 : ! ]");
                    }

                    @Override
                    public void failed(Throwable exc, Void attachment) {
                        System.out.println("[서버로 보내기 실패]");
                        stopClient();
                    }
                });
    }

}
