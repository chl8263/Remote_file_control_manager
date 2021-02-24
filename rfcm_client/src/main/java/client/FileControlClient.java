package client;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import model.dto.FileChangeDto;
import model.dto.FileMoveCopyDto;
import model.info.ServerInfo;
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
import java.util.logging.Logger;

public class FileControlClient {

    private final static Logger LOG = Logger.getLogger(String.valueOf(FileControlClient.class));

    private AsynchronousChannelGroup channelGroup;
    private AsynchronousSocketChannel socketChannel;
    private ObjectMapper objectMapper = new ObjectMapper();
    private final ServerInfo serverInfo;
    BlockingQueue<String> queue;

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
                                LOG.info("[연결 완료 : " + socketChannel.getRemoteAddress() + "]");
                            } catch (IOException e) {
                            }
                            receive();
                        }

                        @Override
                        public void failed(Throwable exc, Void attachment) {
                            LOG.info("[서버와 통신 안됨]");
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
            LOG.info("[클라이언트 종료]");
            if(channelGroup != null && !channelGroup.isShutdown()){
                channelGroup.shutdownNow();
            }
        } catch (IOException e) {
            e.printStackTrace();
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
                                sendMsg.setEndianType(ByteOrder.BIG_ENDIAN); // Default type in JVM
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
                                sendMsg.setEndianType(ByteOrder.BIG_ENDIAN); // Default type in JVM
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
                                sendMsg.setEndianType(ByteOrder.BIG_ENDIAN); // Default type in JVM
                                sendMsg.setProtocol(MessageProtocol.FILES);

                                String responseData = FileService.getFilesInDirectory(path);
                                sendMsg.add(responseData);

                                byte [] sendData = sendMsg.Finish();
                                send(ByteBuffer.wrap(sendData));

                                break;
                            }

                            case MessageProtocol.CHANGE_FILE_NAME:{

                                try {
                                    String fileChangeJson = receivedMsg.getString();
                                    FileChangeDto fileChangeProtocol = objectMapper.readValue(fileChangeJson, FileChangeDto.class);

                                    MessagePacker sendMsg = new MessagePacker();
                                    sendMsg.setEndianType(ByteOrder.BIG_ENDIAN); // Default type in JVM
                                    sendMsg.setProtocol(MessageProtocol.CHANGE_FILE_NAME);

                                    String responseData = FileService.changeFileName(fileChangeProtocol.getPath(), fileChangeProtocol.getBeforeName(), fileChangeProtocol.getAfterName(), fileChangeProtocol.getExtension());

                                    sendMsg.add(responseData);
                                    byte [] sendData = sendMsg.Finish();
                                    send(ByteBuffer.wrap(sendData));
                                } catch (JsonProcessingException e) {
                                    e.printStackTrace();
                                }

                                break;
                            }

                            case MessageProtocol.MOVE_COPY_FILE:{
                                try {
                                    String moveFileJson = receivedMsg.getString();

                                    FileMoveCopyDto fileMoveCopyDto = objectMapper.readValue(moveFileJson, FileMoveCopyDto.class);

                                    MessagePacker sendMsg = new MessagePacker();
                                    sendMsg.setEndianType(ByteOrder.BIG_ENDIAN); // Default type in JVM
                                    sendMsg.setProtocol(MessageProtocol.MOVE_COPY_FILE);

                                    String responseData = FileService.moveCopyFile(fileMoveCopyDto);
                                    sendMsg.add(responseData);

                                    byte[] sendData = sendMsg.Finish();
                                    send(ByteBuffer.wrap(sendData));

                                } catch (JsonProcessingException e) {
                                    e.printStackTrace();
                                }
                                break;
                            }

//                            case MessageProtocol.MOVE_FILE:{
//                                try {
//                                    String moveFileJson = receivedMsg.getString();
//
//                                    FileMoveCopyDto fileMoveCopyDto = objectMapper.readValue(moveFileJson, FileMoveCopyDto.class);
//
//                                    MessagePacker sendMsg = new MessagePacker();
//                                    sendMsg.setEndianType(ByteOrder.BIG_ENDIAN); // Default type in JVM
//                                    sendMsg.setProtocol(MessageProtocol.MOVE_FILE);
//
//                                    String responseData = FileService.moveFile(fileMoveCopyDto.getFromFilePath(), fileMoveCopyDto.getToDirectoryPath(), fileMoveCopyDto.getFileName());
//                                    sendMsg.add(responseData);
//
//                                    byte[] sendData = sendMsg.Finish();
//                                    send(ByteBuffer.wrap(sendData));
//
//                                } catch (JsonProcessingException e) {
//                                    e.printStackTrace();
//                                }
//                                break;
//                            }

                            case MessageProtocol.COPY_FILE:{
                                try {
                                    String moveFileJson = receivedMsg.getString();

                                    FileMoveCopyDto fileMoveCopyDto = objectMapper.readValue(moveFileJson, FileMoveCopyDto.class);

                                    MessagePacker sendMsg = new MessagePacker();
                                    sendMsg.setEndianType(ByteOrder.BIG_ENDIAN); // Default type in JVM
                                    sendMsg.setProtocol(MessageProtocol.COPY_FILE);

                                    //String responseData = FileService.copyFile(fileMoveCopyDto.getFromFilePath(), fileMoveCopyDto.getToDirectoryPath(), fileMoveCopyDto.getFileName());
                                    //sendMsg.add(responseData);

                                    byte[] sendData = sendMsg.Finish();
                                    send(ByteBuffer.wrap(sendData));

                                } catch (JsonProcessingException e) {
                                    e.printStackTrace();
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
                                        sendMsg.setEndianType(ByteOrder.BIG_ENDIAN); // Default type in JVM
                                        sendMsg.setProtocol(MessageProtocol.FILE_UPLOAD);

                                        String responseData = "true";
                                        sendMsg.add(responseData);

                                        byte[] sendData = sendMsg.Finish();
                                        send(ByteBuffer.wrap(sendData));
                                        break;
                                    }

                                    byte [] buff = receivedMsg.getByte(payloadLength);
                                    FileOutputStream fos = new FileOutputStream(path + fineName, true);
                                    LOG.info("offSet : " + offSet);
                                    LOG.info("payloadLength : " + payloadLength);
                                    fos.write(buff, 0, payloadLength);
                                    fos.close();
                                } catch (Exception e) {
                                    e.printStackTrace();
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
                                        msg.setEndianType(ByteOrder.BIG_ENDIAN); // Default type in JVM
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
                                                        msg.setEndianType(ByteOrder.BIG_ENDIAN); // Default type in JVM
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
                                                        LOG.info("파일전송 마지막");
                                                        MessagePacker msg = new MessagePacker();
                                                        msg.setEndianType(ByteOrder.BIG_ENDIAN); // Default type in JVM
                                                        msg.setProtocol(MessageProtocol.FILE_DOWN_LOAD);
                                                        msg.addLong(file.length());
                                                        msg.addInt(-1);
                                                        msg.getBuffer().flip();
                                                        socketChannel.write(msg.getBuffer(), msg.getBuffer(), new CompletionHandler<Integer, ByteBuffer>() {
                                                            @Override
                                                            public void completed(Integer result, ByteBuffer attachment) {
                                                            }
                                                            @Override
                                                            public void failed(Throwable exc, ByteBuffer attachment) {
                                                            }
                                                        });
                                                        queue.put("success");
                                                    }
                                                } catch (IOException e) {
                                                    e.printStackTrace();
                                                } catch (InterruptedException e) {
                                                    e.printStackTrace();
                                                }
                                            }

                                            @Override
                                            public void failed(Throwable exc, ByteBuffer attachment) { }
                                        });

                                    }
                                    queue.poll(5, TimeUnit.MINUTES);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                break;
                            }
                        }

                        ByteBuffer byteBuffer = ByteBuffer.allocate(bufferSize);
                        socketChannel.read(byteBuffer, byteBuffer, this);
                    }

                    @Override
                    public void failed(Throwable exc, ByteBuffer attachment) {
                        LOG.info("[서버 에서 읽기 실패]");
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
                        LOG.info("[보내기 완료 : ! ]");
                    }

                    @Override
                    public void failed(Throwable exc, Void attachment) {
                        LOG.info("[서버로 보내기 실패]");
                        stopClient();
                    }
                });
    }
}
