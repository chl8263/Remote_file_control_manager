package com.ewan.rfcm.domain.file.api;

import com.ewan.rfcm.domain.file.model.FileChangeDto;
import com.ewan.rfcm.domain.file.model.FileDeleteDto;
import com.ewan.rfcm.domain.file.model.FileMoveCopyDto;
import com.ewan.rfcm.domain.file.model.FileMoveCopyRole;
import com.ewan.rfcm.server.AsyncFileControlServer;
import com.ewan.rfcm.server.connection.AsyncFileControlClient;
import com.ewan.rfcm.server.protocol.MessagePacker;
import com.ewan.rfcm.server.protocol.MessageProtocol;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.CompletionHandler;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

@RestController
@RequestMapping(value = {"/api/file"}, produces = {MediaType.APPLICATION_JSON_VALUE})
public class FileController {

    private static final Logger log = LoggerFactory.getLogger(FileController.class);

    private final ObjectMapper objectMapper;

    public FileController(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @GetMapping("/directory/{ip}/{path:.+}")
    public ResponseEntity getDirectory(@PathVariable String ip, @PathVariable String path){
        try {
            AsyncFileControlClient client = AsyncFileControlServer.getClient(ip);
            path = preProcessing(path);
            if(client == null || path == null || path.equals(EMPTY)){
                return ResponseEntity.badRequest().body(EMPTY);
            }

            MessagePacker msg = new MessagePacker();
            msg.setEndianType(ByteOrder.BIG_ENDIAN); // Default type in JVM
            if(path.equals("root")){
                msg.setProtocol(MessageProtocol.ROOT_DIRECTORY);
            }else {
                msg.setProtocol(MessageProtocol.DIRECTORY);
                msg.addString(path);
            }

            byte[] data = msg.Finish();
            client.send(data);
            String responseResult = client.getQueue().poll(1, TimeUnit.MINUTES);
            if(responseResult == null || responseResult.equals(EMPTY)){
                return ResponseEntity.badRequest().body(EMPTY);
            }

            return ResponseEntity.ok(responseResult);

        } catch (InterruptedException e) {
            return ResponseEntity.badRequest().body(EMPTY);
        }
    }

    @GetMapping("/{ip}/{path:.+}")
    public ResponseEntity getFiles(@PathVariable String ip, @PathVariable String path){
        try {
            AsyncFileControlClient client = AsyncFileControlServer.getClient(ip);
            path = preProcessing(path);
            if(client == null || path == null || path.equals(EMPTY)){
                return ResponseEntity.badRequest().body(EMPTY);
            }

            MessagePacker msg = new MessagePacker();
            msg.setEndianType(ByteOrder.BIG_ENDIAN); // Default type in JVM
            msg.setProtocol(MessageProtocol.FILES);
            msg.addString(path);

            byte[] data = msg.Finish();
            client.send(data);
            String responseResult = client.getQueue().poll(1, TimeUnit.MINUTES);
            if(responseResult == null || responseResult.equals(EMPTY)){
                return ResponseEntity.badRequest().body(EMPTY);
            }

            return ResponseEntity.ok(responseResult);

        } catch (InterruptedException e) {
            return ResponseEntity.badRequest().body(EMPTY);
        }
    }

    @PutMapping("/{ip}/{path:.+}")
    public ResponseEntity changeFileName(@PathVariable String ip, @PathVariable String path, @RequestBody FileChangeDto fileChangeDto){
        try {

            // s: validations
            if(fileChangeDto.getBeforeName().equals("") || fileChangeDto.getAfterName().equals("")){
                return ResponseEntity.badRequest().body(EMPTY);
            }
            // e: validations

            AsyncFileControlClient client = AsyncFileControlServer.getClient(ip);
            path = preProcessing(path);
            if(client == null || path == null || path.equals(EMPTY)){
                return ResponseEntity.badRequest().body(EMPTY);
            }

            MessagePacker msg = new MessagePacker();
            msg.setEndianType(ByteOrder.BIG_ENDIAN); // Default type in JVM
            msg.setProtocol(MessageProtocol.CHANGE_FILE_NAME);

            fileChangeDto.setPath(path);
            String tranData = objectMapper.writeValueAsString(fileChangeDto);

            msg.addString(tranData);
            byte[] data = msg.Finish();
            client.send(data);
            String responseResult = client.getQueue().poll(1, TimeUnit.MINUTES);
            if(responseResult == null || responseResult.equals(EMPTY)){
                return ResponseEntity.badRequest().body(EMPTY);
            }

            return ResponseEntity.ok(responseResult);

        } catch (InterruptedException e) {
            return ResponseEntity.badRequest().body(EMPTY);
        } catch (JsonProcessingException e) {
            return ResponseEntity.badRequest().body(EMPTY);
        }
    }

    @PutMapping("/move-copy/{ip}")
    public ResponseEntity moveCopyFile(@PathVariable String ip, @RequestBody FileMoveCopyDto fileMoveCopyDto){
        try {
            // s: validations
            if ((fileMoveCopyDto.getPaths().length <= 0 || fileMoveCopyDto.getToDirectoryPath().equals("") || fileMoveCopyDto.getRole() == FileMoveCopyRole.NOTHING)){
                return ResponseEntity.badRequest().body(EMPTY);
            }
            // e: validations

            AsyncFileControlClient client = AsyncFileControlServer.getClient(ip);
            if (client == null) {
                return ResponseEntity.badRequest().body(EMPTY);
            }

            fileMoveCopyDto.setPaths(Arrays.stream(fileMoveCopyDto.getPaths()).map(x -> preProcessing(x)).toArray(String[]::new));
            fileMoveCopyDto.setToDirectoryPath(preProcessing(fileMoveCopyDto.getToDirectoryPath()));

            MessagePacker msg = new MessagePacker();
            msg.setEndianType(ByteOrder.BIG_ENDIAN); // Default type in JVM
            msg.setProtocol(MessageProtocol.MOVE_COPY_FILE);

            String tranData = objectMapper.writeValueAsString(fileMoveCopyDto);

            msg.addString(tranData);
            byte[] data = msg.Finish();
            client.send(data);
            String responseResult = client.getQueue().poll(1, TimeUnit.MINUTES);

            if (responseResult == null || responseResult.equals(EMPTY)) {
                return ResponseEntity.badRequest().body(EMPTY);
            }
            return ResponseEntity.ok(responseResult);
        } catch (InterruptedException e) {
            return ResponseEntity.badRequest().body(EMPTY);
        } catch (JsonProcessingException e) {
            return ResponseEntity.badRequest().body(EMPTY);
        }
    }

    @DeleteMapping("/{ip}")
    public ResponseEntity deleteFile(@PathVariable String ip, @RequestBody FileDeleteDto fileDeleteDto){
        try {
            // s: validations
            if ((fileDeleteDto.getPaths().length <= 0)){
                return ResponseEntity.badRequest().body(EMPTY);
            }
            // e: validations

            AsyncFileControlClient client = AsyncFileControlServer.getClient(ip);
            if (client == null) {
                return ResponseEntity.badRequest().body(EMPTY);
            }

            MessagePacker msg = new MessagePacker();
            msg.setEndianType(ByteOrder.BIG_ENDIAN); // Default type in JVM
            msg.setProtocol(MessageProtocol.DELETE_FILE);

            String tranData = objectMapper.writeValueAsString(fileDeleteDto);

            msg.addString(tranData);
            byte[] data = msg.Finish();
            client.send(data);
            String responseResult = client.getQueue().poll(1, TimeUnit.MINUTES);

            if (responseResult == null || responseResult.equals(EMPTY)) {
                return ResponseEntity.badRequest().body(EMPTY);
            }
            return ResponseEntity.ok(responseResult);
        } catch (InterruptedException e) {
            return ResponseEntity.badRequest().body(EMPTY);
        } catch (JsonProcessingException e) {
            return ResponseEntity.badRequest().body(EMPTY);
        }
    }

//    @PutMapping("/move/{ip}")
//    public ResponseEntity moveFile(@PathVariable String ip, @RequestBody FileMoveCopyDto fileMoveCopyDto){
//        try {
//            // s: validations
//            if (fileMoveCopyDto.getFromFilePath().equals("") || fileMoveCopyDto.getToDirectoryPath().equals("")) {
//                return ResponseEntity.badRequest().body(EMPTY);
//            }
//            // e: validations
//
//            AsyncFileControlClient client = AsyncFileControlServer.getClient(ip);
//
//            if (client == null) {
//                return ResponseEntity.badRequest().body(EMPTY);
//            }
//
//            fileMoveCopyDto.setFromFilePath(preProcessing(fileMoveCopyDto.getFromFilePath()));
//            fileMoveCopyDto.setToDirectoryPath(preProcessing(fileMoveCopyDto.getToDirectoryPath()));
//
//
//            MessagePacker msg = new MessagePacker();
//            msg.setEndianType(ByteOrder.BIG_ENDIAN); // Default type in JVM
//            msg.setProtocol(MessageProtocol.MOVE_FILE);
//
//            String tranData = objectMapper.writeValueAsString(fileMoveCopyDto);
//
//            msg.addString(tranData);
//
//            byte[] data = msg.Finish();
//            client.send(data);
//            String responseResult = client.getQueue().poll(1, TimeUnit.MINUTES);
//
//            if (responseResult == null || responseResult.equals(EMPTY)) {
//                return ResponseEntity.badRequest().body(EMPTY);
//            }
//            return ResponseEntity.ok(responseResult);
//        } catch (InterruptedException e) {
//            return ResponseEntity.badRequest().body(EMPTY);
//        } catch (JsonProcessingException e) {
//            return ResponseEntity.badRequest().body(EMPTY);
//        }
//    }
//
//    @PutMapping("/copy/{ip}")
//    public ResponseEntity copyFile(@PathVariable String ip, @RequestBody FileMoveCopyDto fileMoveCopyDto){
//        try {
//            // s: validations
//            if (fileMoveCopyDto.getFromFilePath().equals("") || fileMoveCopyDto.getToDirectoryPath().equals("")) {
//                return ResponseEntity.badRequest().body(EMPTY);
//            }
//            // e: validations
//
//            AsyncFileControlClient client = AsyncFileControlServer.getClient(ip);
//
//            if (client == null) {
//                return ResponseEntity.badRequest().body(EMPTY);
//            }
//
//            fileMoveCopyDto.setFromFilePath(preProcessing(fileMoveCopyDto.getFromFilePath()));
//            fileMoveCopyDto.setToDirectoryPath(preProcessing(fileMoveCopyDto.getToDirectoryPath()));
//
//
//            MessagePacker msg = new MessagePacker();
//            msg.setEndianType(ByteOrder.BIG_ENDIAN); // Default type in JVM
//            msg.setProtocol(MessageProtocol.COPY_FILE);
//
//            String tranData = objectMapper.writeValueAsString(fileMoveCopyDto);
//
//            msg.addString(tranData);
//
//            byte[] data = msg.Finish();
//            client.send(data);
//            String responseResult = client.getQueue().poll(1, TimeUnit.MINUTES);
//
//            if (responseResult == null || responseResult.equals(EMPTY)) {
//                return ResponseEntity.badRequest().body(EMPTY);
//            }
//            return ResponseEntity.ok(responseResult);
//        } catch (InterruptedException e) {
//            return ResponseEntity.badRequest().body(EMPTY);
//        } catch (JsonProcessingException e) {
//            return ResponseEntity.badRequest().body(EMPTY);
//        }
//    }

    @PostMapping("/upload/{ip}/{path:.+}")
    public ResponseEntity uploadFile(@PathVariable String ip, @PathVariable String path,
                                     @RequestPart(value = "file", required = true)MultipartFile file){
        try {
            // s: validations
            if(file == null){
                return ResponseEntity.badRequest().body(EMPTY);
            }
            AsyncFileControlClient client = AsyncFileControlServer.getClient(ip);
            path = preProcessing(path);

            if (client == null || path == null || path.equals(EMPTY)) {
                return ResponseEntity.badRequest().body(EMPTY);
            }
            // e: validations

            try {
                InputStream is = file.getInputStream();
                int readCount = 0;

                byte[] buffer = new byte[2097152];
                //byte[] buffer = new byte[9000];

                final int[] offSet = {0};

                if ((readCount = is.read(buffer)) != -1) {
                    MessagePacker msg = new MessagePacker();
                    msg.setEndianType(ByteOrder.BIG_ENDIAN); // Default type in JVM
                    msg.setProtocol(MessageProtocol.FILE_UPLOAD);
                    msg.addString(path);
                    msg.addString(file.getOriginalFilename());
                    msg.add(offSet[0]);
                    msg.add(readCount);
                    offSet[0] += readCount;
                    msg.addByte(buffer);
                    ByteBuffer byteBuffer = msg.getBuffer();
                    msg.getBuffer().flip();
                    String finalPath = path;
                    client.getSocketChannel().write(byteBuffer, byteBuffer, new CompletionHandler<Integer, ByteBuffer>() {
                        @Override
                        public void completed(Integer result, ByteBuffer attachment) {
                            try {
                                int readCount = 0;
                                //byte [] newBuff = new byte[9000];
                                byte[] newBuff = new byte[2097152];
                                if ((readCount = is.read(newBuff)) != -1) {
                                    MessagePacker msg = new MessagePacker();
                                    msg.setEndianType(ByteOrder.BIG_ENDIAN); // Default type in JVM
                                    msg.setProtocol(MessageProtocol.FILE_UPLOAD);
                                    msg.addString(finalPath);
                                    msg.addString(file.getOriginalFilename());
                                    msg.add(offSet[0]);
                                    msg.add(readCount);

                                    offSet[0] += readCount;

                                    msg.addByte(newBuff);
                                    msg.getBuffer().flip();

                                    Thread.sleep(15);
                                    client.getSocketChannel().write(msg.getBuffer(), msg.getBuffer(), this);
                                }else {
                                    MessagePacker msg = new MessagePacker();
                                    msg.setEndianType(ByteOrder.BIG_ENDIAN); // Default type in JVM
                                    msg.setProtocol(MessageProtocol.FILE_UPLOAD);
                                    msg.addString(finalPath);
                                    msg.addString(file.getOriginalFilename());
                                    msg.add(-1);
                                    msg.getBuffer().flip();

                                    client.getSocketChannel().write(msg.getBuffer(), msg.getBuffer(), new CompletionHandler<Integer, ByteBuffer>() {
                                        @Override
                                        public void completed(Integer result, ByteBuffer attachment) {

                                        }
                                        @Override
                                        public void failed(Throwable exc, ByteBuffer attachment) {

                                        }
                                    });
                                }
                            } catch (Exception e) { e.printStackTrace(); }
                        }
                        @Override
                        public void failed(Throwable exc, ByteBuffer attachment) {
                        }
                    });
                }

            } catch (Exception ex) { throw new RuntimeException("file Save Error"); }

            String responseResult = client.getQueue().poll(1, TimeUnit.MINUTES);
            if(responseResult == null || responseResult.equals(EMPTY)){
                return ResponseEntity.badRequest().body(EMPTY);
            }
            return ResponseEntity.ok(responseResult);

        } catch (InterruptedException e) {
            return ResponseEntity.badRequest().body(EMPTY);
        }
    }

    @GetMapping("/download/{ip}/{path:.+}/{fileName}")
    public ResponseEntity downloadFile(@PathVariable String ip, @PathVariable String path, @PathVariable String fileName){
        try {
            // s: validations
            AsyncFileControlClient client = AsyncFileControlServer.getClient(ip);
            path = preProcessing(path);

            if(client == null || path == null || path.equals(EMPTY)){
                return ResponseEntity.badRequest().body(EMPTY);
            }
            // e: validations

            MessagePacker msg = new MessagePacker();
            msg.setEndianType(ByteOrder.BIG_ENDIAN); // Default type in JVM
            msg.setProtocol(MessageProtocol.FILE_DOWN_LOAD);
            msg.addString(path);
            msg.addString(fileName);
            byte[] data = msg.Finish();

            ByteBuffer byteBuffer = ByteBuffer.wrap(data);
            client.getSocketChannel().write(byteBuffer, byteBuffer, new CompletionHandler<Integer, ByteBuffer>() {
                @Override
                public void completed(Integer result, ByteBuffer attachment) { }
                @Override
                public void failed(Throwable exc, ByteBuffer attachment) { }
            });

            String result = client.getQueue().poll(2, TimeUnit.MINUTES);
            if(result.equals("success")){
                var byteQueue = client.getByteQueue();
                //int countSize = 0;
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                while (!byteQueue.isEmpty()){
                    byte [] temp = byteQueue.poll();
                    byteArrayOutputStream.write(temp, 0, temp.length);
                }
                byte[] fileContent = byteArrayOutputStream.toByteArray();

                HttpHeaders header = new HttpHeaders();
                header.setContentLength(fileContent.length);
                header.set("Content-Disposition", "attachment; filename=" + fileName);

                return new ResponseEntity<>(fileContent, header, HttpStatus.OK);
            }else {
                return ResponseEntity.badRequest().body(EMPTY);
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(EMPTY);
        }

    }

    private String preProcessing(String path){
        path = path.replace("|", "/");
        if (path.endsWith(":"))
            path += "/";
        return path;
    }

    private final String EMPTY = "";
}
