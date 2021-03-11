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
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.concurrent.*;

import static protocol.MessageProtocol.DOWNLOAD_FAIL;
import static protocol.MessageProtocol.DOWNLOAD_SUCCESS;

public class FileControlClient {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private ExecutorService executorService;
    private AsynchronousChannelGroup channelGroup;
    private AsynchronousSocketChannel socketChannel;
    private ObjectMapper objectMapper = new ObjectMapper();
    private final ServerInfo serverInfo;
    //private BlockingQueue<String> queue;
    //private static ConcurrentHashMap upLoadMap = new ConcurrentHashMap();
    protected static ConcurrentHashMap upLoadMap = new ConcurrentHashMap();
    private int bufferSize = 2150000;

    public FileControlClient(ServerInfo serverInfo) {
        this.serverInfo = serverInfo;
        //queue = new LinkedBlockingQueue<>();
        executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
    }

    public void startClient() {
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
                            if (socketChannel.isOpen()) {
                                stopClient();
                            }
                        }
                    });

        } catch (IOException e) {
            logger.error("", e);
        }
    }

    public void stopClient() {
        try {
            logger.info("[Client] Stop client and shut down...");
            if (channelGroup != null && !channelGroup.isShutdown()) {
                channelGroup.shutdownNow();
            }
        } catch (IOException e) {
            logger.error("", e);
        }
    }

    public void receive() {
        ByteBuffer byteBuffer = ByteBuffer.allocate(bufferSize);
        socketChannel.read(byteBuffer, byteBuffer,
                new CompletionHandler<Integer, ByteBuffer>() {
                    @Override
                    public void completed(Integer result, ByteBuffer attachment) {
                        byte[] byteArr = attachment.array();
                        MessagePacker receivedMsg = new MessagePacker(byteArr);
                        byte protocol = receivedMsg.getProtocol();

                        switch (protocol) {
                            case MessageProtocol.ROOT_DIRECTORY: {
                                executorService.execute(() -> {
                                    int uidLen = receivedMsg.getInt();
                                    String uid = (String) receivedMsg.getObject(uidLen);

                                    MessagePacker sendMsg = new MessagePacker();
                                    sendMsg.setEndianType(ByteOrder.BIG_ENDIAN);
                                    sendMsg.setProtocol(MessageProtocol.ROOT_DIRECTORY);
                                    sendMsg.add(uid);

                                    String responseData = FileService.getDirectoryInRoot();
                                    sendMsg.add(responseData);

                                    byte[] sendData = sendMsg.finish();
                                    send(ByteBuffer.wrap(sendData));
                                });
                                break;
                            }
                            case MessageProtocol.DIRECTORY: {
                                executorService.execute(() -> {
                                    int uidLen = receivedMsg.getInt();
                                    String uid = (String) receivedMsg.getObject(uidLen);

                                    String path = receivedMsg.getString();
                                    MessagePacker sendMsg = new MessagePacker();
                                    sendMsg.setEndianType(ByteOrder.BIG_ENDIAN);
                                    sendMsg.setProtocol(MessageProtocol.DIRECTORY);
                                    sendMsg.add(uid);

                                    String responseData = FileService.getUnderLineDirectory(path);
                                    sendMsg.add(responseData);
                                    byte[] sendData = sendMsg.finish();
                                    send(ByteBuffer.wrap(sendData));
                                });
                                break;
                            }
                            case MessageProtocol.FILES: {
                                executorService.execute(() -> {
                                    int uidLen = receivedMsg.getInt();
                                    String uid = (String) receivedMsg.getObject(uidLen);

                                    String path = receivedMsg.getString();
                                    MessagePacker sendMsg = new MessagePacker();
                                    sendMsg.setEndianType(ByteOrder.BIG_ENDIAN);
                                    sendMsg.setProtocol(MessageProtocol.FILES);
                                    sendMsg.add(uid);

                                    String responseData = FileService.getFilesInDirectory(path);
                                    sendMsg.add(responseData);

                                    byte[] sendData = sendMsg.finish();
                                    send(ByteBuffer.wrap(sendData));
                                });
                                break;
                            }
                            case MessageProtocol.CHANGE_FILE_NAME: {
                                executorService.execute(() -> {
                                    try {
                                        int uidLen = receivedMsg.getInt();
                                        String uid = (String) receivedMsg.getObject(uidLen);

                                        String convertedJson = receivedMsg.getString();
                                        FileChangeDto fileChangeProtocol = objectMapper.readValue(convertedJson, FileChangeDto.class);

                                        MessagePacker sendMsg = new MessagePacker();
                                        sendMsg.setEndianType(ByteOrder.BIG_ENDIAN);
                                        sendMsg.setProtocol(MessageProtocol.CHANGE_FILE_NAME);
                                        sendMsg.add(uid);

                                        String responseData = FileService.changeFileName(fileChangeProtocol.getPath(), fileChangeProtocol.getBeforeName(), fileChangeProtocol.getAfterName(), fileChangeProtocol.getExtension());
                                        sendMsg.add(responseData);
                                        byte[] sendData = sendMsg.finish();
                                        send(ByteBuffer.wrap(sendData));
                                    } catch (JsonProcessingException e) {
                                        logger.error("", e);
                                    }
                                });
                                break;
                            }
                            case MessageProtocol.MOVE_COPY_FILE: {
                                executorService.execute(() -> {
                                    try {
                                        int uidLen = receivedMsg.getInt();
                                        String uid = (String) receivedMsg.getObject(uidLen);

                                        String convertedJson = receivedMsg.getString();
                                        FileMoveCopyDto fileMoveCopyDto = objectMapper.readValue(convertedJson, FileMoveCopyDto.class);

                                        MessagePacker sendMsg = new MessagePacker();
                                        sendMsg.setEndianType(ByteOrder.BIG_ENDIAN);
                                        sendMsg.setProtocol(MessageProtocol.MOVE_COPY_FILE);
                                        sendMsg.add(uid);

                                        String responseData = FileService.moveCopyFile(fileMoveCopyDto);
                                        sendMsg.add(responseData);

                                        byte[] sendData = sendMsg.finish();
                                        send(ByteBuffer.wrap(sendData));
                                    } catch (JsonProcessingException e) {
                                        logger.error("", e);
                                    }
                                });
                                break;
                            }
                            case MessageProtocol.DELETE_FILE: {
                                executorService.execute(() -> {
                                    try {
                                        int uidLen = receivedMsg.getInt();
                                        String uid = (String) receivedMsg.getObject(uidLen);

                                        String convertedJson = receivedMsg.getString();
                                        FileDeleteDto fileDeleteDto = objectMapper.readValue(convertedJson, FileDeleteDto.class);

                                        MessagePacker sendMsg = new MessagePacker();
                                        sendMsg.setEndianType(ByteOrder.BIG_ENDIAN);
                                        sendMsg.setProtocol(MessageProtocol.DELETE_FILE);
                                        sendMsg.add(uid);

                                        String responseData = FileService.deleteFile(fileDeleteDto.getPaths());
                                        sendMsg.add(responseData);

                                        byte[] sendData = sendMsg.finish();
                                        send(ByteBuffer.wrap(sendData));
                                    } catch (JsonProcessingException e) {
                                        logger.error("", e);
                                    }
                                });
                                break;
                            }

                            case MessageProtocol.FILE_UPLOAD: {
                                executorService.execute(() -> {
                                    try {
                                        int uidLen = receivedMsg.getInt();
                                        String uid = (String) receivedMsg.getObject(uidLen);

                                        String path = receivedMsg.getString();
                                        String fileName = receivedMsg.getString();
                                        int offSet = receivedMsg.getInt();
                                        int payloadLength = receivedMsg.getInt();

                                        File tFile = new File(path + fileName);
                                        if(!upLoadMap.containsKey(tFile.getAbsolutePath())){
                                            upLoadMap.put(tFile.getAbsolutePath(), uid);
                                        }

                                        if(upLoadMap.get(tFile.getAbsolutePath()).equals(uid)){
                                            System.out.println(upLoadMap.get(tFile.getAbsolutePath()));
                                            if (offSet == -1) {
                                                MessagePacker sendMsg = new MessagePacker();
                                                sendMsg.setEndianType(ByteOrder.BIG_ENDIAN);
                                                sendMsg.setProtocol(MessageProtocol.FILE_UPLOAD);
                                                sendMsg.add(uid);

                                                String responseData = responseJson(false, "", false);//"true";
                                                sendMsg.add(responseData);

                                                if(upLoadMap.containsKey(tFile.getAbsolutePath())){
                                                    upLoadMap.remove(tFile.getAbsolutePath());
                                                }

                                                byte[] sendData = sendMsg.finish();
                                                send(ByteBuffer.wrap(sendData));
                                            } else {
                                                byte[] buff = receivedMsg.getByte(payloadLength);
                                                FileOutputStream fos = new FileOutputStream(path + fileName, true);
                                                fos.write(buff, 0, payloadLength);
                                                fos.close();
                                            }
                                        }else if(tFile.exists() || !upLoadMap.get(tFile.getAbsolutePath()).equals(uid)) {
                                            System.out.println("!!!!!!!!!"+ upLoadMap.get(tFile.getAbsolutePath()));
                                            MessagePacker sendMsg = new MessagePacker();
                                            sendMsg.setEndianType(ByteOrder.BIG_ENDIAN);
                                            sendMsg.setProtocol(MessageProtocol.FILE_UPLOAD);
                                            sendMsg.add(uid);

                                            String responseData = responseJson(true, path + "/" + fileName + " already exists. Please check again.", false);
                                            sendMsg.add(responseData);

                                            byte[] sendData = sendMsg.finish();
                                            send(ByteBuffer.wrap(sendData));
                                        }
                                    } catch (Exception e) {
                                        logger.error("", e);
                                    }
                                });
                                break;
                            }

                            case MessageProtocol.FILE_DOWN_LOAD: {
                                executorService.execute(() -> {
                                    try {
                                        BlockingQueue<String> queue = new LinkedBlockingQueue<>();

                                        int uidLen = receivedMsg.getInt();
                                        String uid = (String) receivedMsg.getObject(uidLen);

                                        String path = receivedMsg.getString();
                                        String fileName = receivedMsg.getString();

                                        File file = new File(path + "/" + fileName);

                                        if(!Files.isWritable(Path.of(path)) || !file.renameTo(file) || FileControlClient.upLoadMap.containsKey(file.getAbsolutePath())) {
                                            String message = "";
                                            if (!Files.isWritable(Path.of(path))) {
                                                message = responseJson(true, "Write access deny " + path + "/" + fileName, false);
                                            } else if (!file.renameTo(file)) {
                                                message = responseJson(true, "Cannot download " + path + "/" + fileName + "file busy...", false);
                                            } else if(FileControlClient.upLoadMap.containsKey(file.getAbsolutePath())){
                                                message = responseJson(true, "Cannot download " + path + "/" + fileName + " during the file upload...", false);
                                            }
                                            MessagePacker fmsg = new MessagePacker();
                                            fmsg.setEndianType(ByteOrder.BIG_ENDIAN);
                                            fmsg.setProtocol(MessageProtocol.FILE_DOWN_LOAD);
                                            fmsg.add(uid);
                                            fmsg.addLong(file.length());
                                            fmsg.addInt(-2);
                                            fmsg.add(message);
                                            fmsg.getBuffer().flip();
                                            socketChannel.write(fmsg.getBuffer(), fmsg.getBuffer(), new CompletionHandler<Integer, ByteBuffer>() {
                                                @Override
                                                public void completed(Integer result, ByteBuffer attachment) {
                                                    try {
                                                        System.out.println(uid + " , current Thread => " + Thread.currentThread());
                                                        queue.put(DOWNLOAD_FAIL);
                                                    } catch (InterruptedException e) {
                                                        e.printStackTrace();
                                                    }
                                                }

                                                @Override
                                                public void failed(Throwable exc, ByteBuffer attachment) {
                                                }
                                            });
                                        }else {
                                            FileInputStream fis = new FileInputStream(file);
                                            int readCount = 0;
                                            byte[] buffer = new byte[2097152];
                                            final int[] offSet = {0};
                                            if ((readCount = fis.read(buffer)) != -1) {
                                                MessagePacker fmsg = new MessagePacker();
                                                fmsg.setEndianType(ByteOrder.BIG_ENDIAN);
                                                fmsg.setProtocol(MessageProtocol.FILE_DOWN_LOAD);
                                                fmsg.add(uid);
                                                fmsg.addLong(file.length());
                                                fmsg.addInt(offSet[0]);
                                                fmsg.addInt(readCount);
                                                offSet[0] += readCount;
                                                fmsg.addByte(buffer);
                                                fmsg.getBuffer().flip();

                                                socketChannel.write(fmsg.getBuffer(), fmsg.getBuffer(), new CompletionHandler<Integer, ByteBuffer>() {
                                                    @Override
                                                    public void completed(Integer result, ByteBuffer attachment) {
                                                        try {

                                                            int readCount = 0;
                                                            byte[] newBuff = new byte[2097152];
                                                            if ((readCount = fis.read(newBuff)) != -1) {
                                                                MessagePacker msg = new MessagePacker();
                                                                msg.setEndianType(ByteOrder.BIG_ENDIAN);
                                                                msg.setProtocol(MessageProtocol.FILE_DOWN_LOAD);
                                                                msg.add(uid);
                                                                msg.addLong(file.length());
                                                                msg.addInt(offSet[0]);
                                                                msg.addInt(readCount);
                                                                offSet[0] += readCount;
                                                                msg.addByte(newBuff);
                                                                msg.getBuffer().flip();
                                                                Thread.sleep(20);
                                                                socketChannel.write(msg.getBuffer(), msg.getBuffer(), this);
                                                            } else {
                                                                MessagePacker msg = new MessagePacker();
                                                                msg.setEndianType(ByteOrder.BIG_ENDIAN);
                                                                msg.setProtocol(MessageProtocol.FILE_DOWN_LOAD);
                                                                msg.add(uid);
                                                                msg.addLong(file.length());
                                                                msg.addInt(-1);
                                                                msg.addString(DOWNLOAD_SUCCESS);
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
                                                                queue.put(DOWNLOAD_SUCCESS);
                                                                fis.close();
                                                            }
                                                        } catch (Exception e) {
                                                            logger.error("", e);
                                                        }
                                                    }

                                                    @Override
                                                    public void failed(Throwable exc, ByteBuffer attachment) {
                                                    }
                                                });
                                            }
                                        }
                                        queue.poll(5, TimeUnit.MINUTES);
                                    } catch (Exception e) {
                                        logger.error("", e);
                                    }
                                });
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

    public void send(ByteBuffer byteBuffer) {
        synchronized (this) {
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

    private String responseJson(boolean error, String errorMsg, Object responseData){

        return "{\"error\":"+ error +",\"errorMsg\": \""+ errorMsg +"\", \"responseData\": " + responseData + "}";

    }
}
