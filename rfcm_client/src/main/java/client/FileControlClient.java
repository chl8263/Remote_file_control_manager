package client;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import model.dto.FileChangeDto;
import model.dto.FileDeleteDto;
import model.dto.FileMoveCopyDto;
import model.info.ServerInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import protocol.MessagePacker;
import protocol.MessageProtocol;
import service.FileService;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.AsynchronousChannelGroup;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

public class FileControlClient {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private AsynchronousChannelGroup channelGroup;
    private AsynchronousSocketChannel socketChannel;
    private ObjectMapper objectMapper = new ObjectMapper();
    private final ServerInfo serverInfo;
    private BlockingQueue<String> queue;
    private int bufferSize = 2100000;

    public FileControlClient(ServerInfo serverInfo){
        this.serverInfo = serverInfo;
        queue = new LinkedBlockingQueue<>();
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
                                logger.info("[Client] Connection success : {}", socketChannel.getRemoteAddress());
                            } catch (IOException e) {
                                logger.error("", e);
                            }
                            receive();
                        }

                        @Override
                        public void failed(Throwable exc, Void attachment) {
                            logger.info("[Client] Failed to connection with server");
                            if(socketChannel.isOpen()) {
                                stopClient();
                            }
                        }
                    });

        } catch (IOException e) {
            logger.error("", e);
        }
    }

    public void stopClient(){
        try {
            logger.info("[Client] Stop client and shut down...");
            if(channelGroup != null && !channelGroup.isShutdown()){
                channelGroup.shutdownNow();
            }
        } catch (IOException e) {
            logger.error("", e);
        }
    }

    public void receive(){
        ByteBuffer byteBuffer = ByteBuffer.allocate(bufferSize);
        socketChannel.read(byteBuffer, byteBuffer,
                new CompletionHandler<Integer, ByteBuffer>() {
                    @Override
                    public void completed(Integer result, ByteBuffer attachment) {
                        byte [] byteArr = attachment.array();
                        MessagePacker receivedMsg = new MessagePacker(byteArr);
                        byte protocol = receivedMsg.getProtocol();

                        switch (protocol){
                            case MessageProtocol.ROOT_DIRECTORY:{
                                MessagePacker sendMsg = new MessagePacker();
                                sendMsg.setEndianType(ByteOrder.BIG_ENDIAN);
                                sendMsg.setProtocol(MessageProtocol.ROOT_DIRECTORY);

                                String responseData = FileService.getDirectoryInRoot();
                                sendMsg.add(responseData);

                                byte [] sendData = sendMsg.Finish();
                                send(ByteBuffer.wrap(sendData));
                                break;
                            }
                            case MessageProtocol.DIRECTORY:{
                                String path = receivedMsg.getString();

                                MessagePacker sendMsg = new MessagePacker();
                                sendMsg.setEndianType(ByteOrder.BIG_ENDIAN);
                                sendMsg.setProtocol(MessageProtocol.ROOT_DIRECTORY);

                                String responseData = FileService.getUnderLineDirectory(path);
                                sendMsg.add(responseData);
                                byte [] sendData = sendMsg.Finish();
                                send(ByteBuffer.wrap(sendData));
                                break;
                            }
                            case MessageProtocol.FILES:{
                                String path = receivedMsg.getString();
                                MessagePacker sendMsg = new MessagePacker();
                                sendMsg.setEndianType(ByteOrder.BIG_ENDIAN);
                                sendMsg.setProtocol(MessageProtocol.FILES);

                                String responseData = FileService.getFilesInDirectory(path);
                                sendMsg.add(responseData);

                                byte [] sendData = sendMsg.Finish();
                                send(ByteBuffer.wrap(sendData));

                                break;
                            }
                            case MessageProtocol.CHANGE_FILE_NAME:{
                                try {
                                    String convertedJson = receivedMsg.getString();
                                    FileChangeDto fileChangeProtocol = objectMapper.readValue(convertedJson, FileChangeDto.class);

                                    MessagePacker sendMsg = new MessagePacker();
                                    sendMsg.setEndianType(ByteOrder.BIG_ENDIAN);
                                    sendMsg.setProtocol(MessageProtocol.CHANGE_FILE_NAME);

                                    String responseData = FileService.changeFileName(fileChangeProtocol.getPath(), fileChangeProtocol.getBeforeName(), fileChangeProtocol.getAfterName(), fileChangeProtocol.getExtension());

                                    sendMsg.add(responseData);
                                    byte [] sendData = sendMsg.Finish();
                                    send(ByteBuffer.wrap(sendData));
                                } catch (JsonProcessingException e) {
                                    logger.error("", e);
                                }
                                break;
                            }
                            case MessageProtocol.MOVE_COPY_FILE:{
                                try {
                                    String convertedJson = receivedMsg.getString();
                                    FileMoveCopyDto fileMoveCopyDto = objectMapper.readValue(convertedJson, FileMoveCopyDto.class);

                                    MessagePacker sendMsg = new MessagePacker();
                                    sendMsg.setEndianType(ByteOrder.BIG_ENDIAN);
                                    sendMsg.setProtocol(MessageProtocol.MOVE_COPY_FILE);

                                    String responseData = FileService.moveCopyFile(fileMoveCopyDto);
                                    sendMsg.add(responseData);

                                    byte[] sendData = sendMsg.Finish();
                                    send(ByteBuffer.wrap(sendData));

                                } catch (JsonProcessingException e) {
                                    logger.error("", e);
                                }
                                break;
                            }

                            case MessageProtocol.DELETE_FILE:{
                                try {
                                    String convertedJson = receivedMsg.getString();
                                    FileDeleteDto fileDeleteDto = objectMapper.readValue(convertedJson, FileDeleteDto.class);

                                    MessagePacker sendMsg = new MessagePacker();
                                    sendMsg.setEndianType(ByteOrder.BIG_ENDIAN);
                                    sendMsg.setProtocol(MessageProtocol.DELETE_FILE);

                                    String responseData = FileService.deleteFile(fileDeleteDto.getPaths());
                                    sendMsg.add(responseData);

                                    byte[] sendData = sendMsg.Finish();
                                    send(ByteBuffer.wrap(sendData));

                                } catch (JsonProcessingException e) {
                                    logger.error("", e);
                                }
                                break;
                            }

                            case MessageProtocol.FILE_UPLOAD:{
                                try {
                                    String path = receivedMsg.getString();
                                    String fineName = receivedMsg.getString();
                                    int offSet = receivedMsg.getInt();
                                    int payloadLength = receivedMsg.getInt();

                                    if(offSet == -1){
                                        MessagePacker sendMsg = new MessagePacker();
                                        sendMsg.setEndianType(ByteOrder.BIG_ENDIAN);
                                        sendMsg.setProtocol(MessageProtocol.FILE_UPLOAD);

                                        String responseData = "true";
                                        sendMsg.add(responseData);

                                        byte[] sendData = sendMsg.Finish();
                                        send(ByteBuffer.wrap(sendData));
                                        break;
                                    }

                                    byte [] buff = receivedMsg.getByte(payloadLength);
                                    FileOutputStream fos = new FileOutputStream(path + fineName, true);
                                    fos.write(buff, 0, payloadLength);
                                    fos.close();
                                } catch (Exception e) {
                                    logger.error("", e);
                                }
                                break;
                            }

                            case MessageProtocol.FILE_DOWN_LOAD:{
                                try {
                                    String path = receivedMsg.getString();
                                    String fileName = receivedMsg.getString();

                                    File file = new File(path+"/"+fileName);
                                    FileInputStream fis = new FileInputStream(file);

                                    int readCount = 0;
                                    byte [] buffer = new byte[2097152];
                                    final int[] offSet = {0};
                                    if ((readCount = fis.read(buffer)) != -1) {
                                        MessagePacker msg = new MessagePacker();
                                        msg.setEndianType(ByteOrder.BIG_ENDIAN);
                                        msg.setProtocol(MessageProtocol.FILE_DOWN_LOAD);
                                        msg.addLong(file.length());
                                        msg.addInt(offSet[0]);
                                        msg.addInt(readCount);
                                        offSet[0] += readCount;
                                        msg.addByte(buffer);
                                        msg.getBuffer().flip();

                                    socketChannel.write(msg.getBuffer(), msg.getBuffer(), new CompletionHandler<Integer, ByteBuffer>() {
                                            @Override
                                            public void completed(Integer result, ByteBuffer attachment) {
                                                try {
                                                    int readCount = 0;
                                                    byte [] newBuff = new byte[2097152];
                                                    if ((readCount = fis.read(newBuff)) != -1) {

                                                        MessagePacker msg = new MessagePacker();
                                                        msg.setEndianType(ByteOrder.BIG_ENDIAN);
                                                        msg.setProtocol(MessageProtocol.FILE_DOWN_LOAD);
                                                        msg.addLong(file.length());
                                                        msg.addInt(offSet[0]);
                                                        msg.addInt(readCount);
                                                        offSet[0] += readCount;
                                                        msg.addByte(newBuff);
                                                        msg.getBuffer().flip();
                                                        Thread.sleep(20);
                                                        socketChannel.write(msg.getBuffer(), msg.getBuffer(), this);
                                                    }else {
                                                        MessagePacker msg = new MessagePacker();
                                                        msg.setEndianType(ByteOrder.BIG_ENDIAN);
                                                        msg.setProtocol(MessageProtocol.FILE_DOWN_LOAD);
                                                        msg.addLong(file.length());
                                                        msg.addInt(-1);
                                                        msg.getBuffer().flip();
                                                        socketChannel.write(msg.getBuffer(), msg.getBuffer(), new CompletionHandler<Integer, ByteBuffer>() {
                                                            @Override
                                                            public void completed(Integer result, ByteBuffer attachment) {
                                                                logger.info("[client] Success to send file : {}", fileName);
                                                            }
                                                            @Override
                                                            public void failed(Throwable exc, ByteBuffer attachment) {
                                                            }
                                                        });
                                                        queue.put("success");
                                                    }
                                                } catch (Exception e) {
                                                    logger.error("", e);
                                                }
                                            }

                                            @Override
                                            public void failed(Throwable exc, ByteBuffer attachment) { }
                                        });

                                    }
                                    queue.poll(5, TimeUnit.MINUTES);
                                } catch (Exception e) {
                                    logger.error("", e);
                                }
                                break;
                            }
                        }

                        ByteBuffer byteBuffer = ByteBuffer.allocate(bufferSize);
                        socketChannel.read(byteBuffer, byteBuffer, this);
                    }

                    @Override
                    public void failed(Throwable exc, ByteBuffer attachment) {
                        logger.info("[client] Failed to read from server, shutdown client..");
                        stopClient();
                    }
                }
        );
    }

    public void send(ByteBuffer byteBuffer){
        socketChannel.write(byteBuffer, null,
                new CompletionHandler<Integer, Void>() {
                    @Override
                    public void completed(Integer result, Void attachment) {
                        logger.info("[client] Send success");
                    }
                    @Override
                    public void failed(Throwable exc, Void attachment) {
                        logger.info("[client] Failed to send from server, shutdown client..");
                        stopClient();
                    }
                });
    }
}
