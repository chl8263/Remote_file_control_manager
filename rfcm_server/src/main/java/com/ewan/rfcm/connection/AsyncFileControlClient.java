package com.ewan.rfcm.connection;

import com.ewan.rfcm.connection.model.SocketResponseModel;
import com.ewan.rfcm.connection.protocol.MessagePacker;
import com.ewan.rfcm.connection.protocol.MessageProtocol;
import com.ewan.rfcm.connection.model.WebsocketRequestType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import static com.ewan.rfcm.connection.protocol.MessageProtocol.*;

public class AsyncFileControlClient {

    private static final Logger logger = LoggerFactory.getLogger(AsyncFileControlClient.class);

    private int bufferSize = 2150000;

    private AsynchronousSocketChannel socketChannel;
    private BlockingQueue<String> queue;
    private ConcurrentHashMap<Integer, BlockingQueue<SocketResponseModel>> queueHash;
    private BlockingQueue<byte[]> byteQueue;
    private boolean isBlocked = false;

    public AsyncFileControlClient(AsynchronousSocketChannel socketChannel){
        this.socketChannel = socketChannel;
        this.queue = new LinkedBlockingQueue<>();
        this.byteQueue = new LinkedBlockingQueue<>();
        this.queueHash = new ConcurrentHashMap<>();
        queueHash.put((int) ROOT_DIRECTORY, new LinkedBlockingQueue<>());
        queueHash.put((int) DIRECTORY, new LinkedBlockingQueue<>());
        queueHash.put((int) FILES, new LinkedBlockingQueue<>());
        queueHash.put((int) CHANGE_FILE_NAME, new LinkedBlockingQueue<>());
        queueHash.put((int) MOVE_COPY_FILE, new LinkedBlockingQueue<>());
        queueHash.put((int) DELETE_FILE, new LinkedBlockingQueue<>());
        queueHash.put((int) FILE_UPLOAD, new LinkedBlockingQueue<>());
        queueHash.put((int) FILE_DOWN_LOAD, new LinkedBlockingQueue<>());

        receive();
    }

    public void receive(){
        try{
            ByteBuffer byteBuffer = ByteBuffer.allocate(bufferSize);
            socketChannel.read(byteBuffer, byteBuffer,
                    new CompletionHandler<Integer, ByteBuffer>() {
                        @Override
                        public void completed(Integer result, ByteBuffer attachment) {
                            try {
                                attachment.flip();

                                byte [] byteArr = attachment.array();
                                MessagePacker msg = new MessagePacker(byteArr);
                                byte protocol = msg.getProtocol();

                                BlockingQueue<SocketResponseModel> tQueue = queueHash.get((int) protocol);
                                if(tQueue == null) throw new NullPointerException("Cannot find queue");

                                SocketResponseModel responseModel = new SocketResponseModel();
                                int uidLen = msg.getInt();
                                String uid = (String) msg.getObject(uidLen);
                                responseModel.setUid(uid);

                                if(protocol == MessageProtocol.FILE_DOWN_LOAD){
                                    try {
                                        float fileSize = msg.getLong();
                                        int offSet = msg.getInt();
                                        if(offSet == -1){
                                            responseModel.setResponseData(DOWNLOAD_SUCCESS);
                                            tQueue.put(responseModel);
                                        }else {
                                            int payloadLength = msg.getInt();
                                            byte [] buff = msg.getByte(payloadLength);
                                            byteQueue.add(buff);
                                        }
                                    } catch (Exception e) {
                                        logger.error("[Async client]", e);
                                    }
                                }else {
                                    int payloadLength = msg.getInt();
                                    String payload = (String) msg.getObject(payloadLength);
                                    responseModel.setResponseData(payload);
                                    tQueue.put(responseModel);
                                }
                                receive();
                            } catch (Exception e) {
                                logger.error("[Async client]", e);
                            }
                        }

                        @Override
                        public void failed(Throwable exc, ByteBuffer attachment) {
                            try {
                                String address = socketChannel.getRemoteAddress().toString().substring(1);
                                AsyncFileControlServer.connections.remove(address);
                                socketChannel.close();
                                WebSocketHandler.sendClientInfo(address, WebsocketRequestType.REMOVE);

                                logger.info("[Async client] Cannot connection, close socket : {}", address + " , Thread => " + Thread.currentThread().getName());
                            } catch (Exception e) {
                                logger.error("[Async client]", e);
                            }
                        }
                    });
        }catch (Exception e){
            logger.error("[Async client]", e);
        }
    }

    public void send(byte[] sendData){
        ByteBuffer byteBuffer = ByteBuffer.wrap(sendData);
        socketChannel.write(byteBuffer, byteBuffer, new CompletionHandler<Integer, ByteBuffer>() {
            @Override
            public void completed(Integer result, ByteBuffer attachment) {
                try {
                    //receive();
                } catch (Exception e) {
                    logger.error("[Async client]", e);
                }
            }

            @Override
            public void failed(Throwable exc, ByteBuffer attachment) {
                try {
                    String address = socketChannel.getRemoteAddress().toString().substring(1);
                    AsyncFileControlServer.connections.remove(address);
                    socketChannel.close();
                    WebSocketHandler.sendClientInfo(address, WebsocketRequestType.REMOVE);

                    logger.info("[Async client] Cannot connection, close socket : {}", address + " , Thread => " + Thread.currentThread().getName());
                } catch (Exception e) {
                    logger.error("[Async client]", e);
                }
            }
        });
    }

    public AsynchronousSocketChannel getSocketChannel() {
        return socketChannel;
    }

    public SocketResponseModel setPoll(int key, int timeout, TimeUnit timeUnit){
        try {
            BlockingQueue<SocketResponseModel> queue = queueHash.get(key);
            if(queue == null) throw new NullPointerException("Cannot find queue");
            SocketResponseModel result = queue.take();//queue.poll(timeout, timeUnit);
            return result;
        } catch (InterruptedException e) {
            logger.error("", e);
            isBlocked = false;
        }
        return new SocketResponseModel();
    }

    public SocketResponseModel setPut(int key, SocketResponseModel socketResponseModel){
        try {
            BlockingQueue<SocketResponseModel> queue = queueHash.get(key);
            if(queue == null) throw new NullPointerException("Cannot find queue");
            queue.put(socketResponseModel);
        } catch (InterruptedException e) {
            logger.error("", e);
            isBlocked = false;
        }
        return new SocketResponseModel();
    }

    public byte[] getByteInQueue(){
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        while (!byteQueue.isEmpty()){
            byte [] temp = byteQueue.poll();
            byteArrayOutputStream.write(temp, 0, temp.length);
        }
        return byteArrayOutputStream.toByteArray();
    }

    public boolean isBlocked(){
        return isBlocked;
    }
}
