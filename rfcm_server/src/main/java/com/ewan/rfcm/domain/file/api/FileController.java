package com.ewan.rfcm.domain.file.api;

import com.ewan.rfcm.domain.file.model.dto.FileChangeDto;
import com.ewan.rfcm.domain.file.model.dto.FileDeleteDto;
import com.ewan.rfcm.domain.file.model.dto.FileMoveCopyDto;
import com.ewan.rfcm.domain.file.model.dto.FileMoveCopyRole;
import com.ewan.rfcm.connection.AsyncFileControlServer;
import com.ewan.rfcm.connection.AsyncFileControlClient;
import com.ewan.rfcm.connection.protocol.MessagePacker;
import com.ewan.rfcm.connection.protocol.MessageProtocol;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
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

@AllArgsConstructor
@RestController
@RequestMapping(value = {"/api/file"}, produces = {MediaType.APPLICATION_JSON_VALUE})
public class FileController {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final ObjectMapper objectMapper;

    @GetMapping("/directory/{ip}/{path:.+}")
    public ResponseEntity getDirectory(@PathVariable String ip, @PathVariable String path){
        try {
            AsyncFileControlClient client = AsyncFileControlServer.getClient(ip);
            path = preProcessing(path);
            if(client == null || path == null || path.equals(EMPTY)){
                return ResponseEntity.badRequest().body(EMPTY);
            }

            MessagePacker msg = new MessagePacker();
            msg.setEndianType(ByteOrder.BIG_ENDIAN);
            if(path.equals("root")){
                msg.setProtocol(MessageProtocol.ROOT_DIRECTORY);
            }else {
                msg.setProtocol(MessageProtocol.DIRECTORY);
                msg.addString(path);
            }

            byte[] data = msg.Finish();
            client.send(data);
            String responseResult = client.setPoll(1, TimeUnit.MINUTES);
            if(responseResult == null || responseResult.equals(EMPTY)){
                return ResponseEntity.badRequest().body(EMPTY);
            }

            return ResponseEntity.ok(responseResult);

        } catch (Exception e) {
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
            msg.setEndianType(ByteOrder.BIG_ENDIAN);
            msg.setProtocol(MessageProtocol.FILES);
            msg.addString(path);

            byte[] data = msg.Finish();
            client.send(data);
            String responseResult = client.setPoll(1, TimeUnit.MINUTES);
            if(responseResult == null || responseResult.equals(EMPTY)){
                return ResponseEntity.badRequest().body(EMPTY);
            }

            return ResponseEntity.ok(responseResult);

        } catch (Exception e) {
            return ResponseEntity.badRequest().body(EMPTY);
        }
    }

    @PutMapping("/{ip}/{path:.+}")
    public ResponseEntity changeFileName(@PathVariable String ip, @PathVariable String path, @RequestBody FileChangeDto fileChangeDto){
        try {
            if(fileChangeDto.getBeforeName().equals("") || fileChangeDto.getAfterName().equals("")){
                return ResponseEntity.badRequest().body(EMPTY);
            }

            AsyncFileControlClient client = AsyncFileControlServer.getClient(ip);
            path = preProcessing(path);
            if(client == null || path == null || path.equals(EMPTY)){
                return ResponseEntity.badRequest().body(EMPTY);
            }

            MessagePacker msg = new MessagePacker();
            msg.setEndianType(ByteOrder.BIG_ENDIAN);
            msg.setProtocol(MessageProtocol.CHANGE_FILE_NAME);

            fileChangeDto.setPath(path);
            String tranData = objectMapper.writeValueAsString(fileChangeDto);

            msg.addString(tranData);
            byte[] data = msg.Finish();
            client.send(data);
            String responseResult = client.setPoll(1, TimeUnit.MINUTES);
            if(responseResult == null || responseResult.equals(EMPTY)){
                return ResponseEntity.badRequest().body(EMPTY);
            }

            return ResponseEntity.ok(responseResult);

        } catch (Exception e) {
            return ResponseEntity.badRequest().body(EMPTY);
        }
    }

    @PutMapping("/move-copy/{ip}")
    public ResponseEntity moveCopyFile(@PathVariable String ip, @RequestBody FileMoveCopyDto fileMoveCopyDto){
        try {
            if ((fileMoveCopyDto.getPaths().length <= 0 || fileMoveCopyDto.getToDirectoryPath().equals("") || fileMoveCopyDto.getRole() == FileMoveCopyRole.NOTHING)){
                return ResponseEntity.badRequest().body(EMPTY);
            }

            AsyncFileControlClient client = AsyncFileControlServer.getClient(ip);
            if (client == null) {
                return ResponseEntity.badRequest().body(EMPTY);
            }

            fileMoveCopyDto.setPaths(Arrays.stream(fileMoveCopyDto.getPaths()).map(x -> preProcessing(x)).toArray(String[]::new));
            fileMoveCopyDto.setToDirectoryPath(preProcessing(fileMoveCopyDto.getToDirectoryPath()));

            MessagePacker msg = new MessagePacker();
            msg.setEndianType(ByteOrder.BIG_ENDIAN);
            msg.setProtocol(MessageProtocol.MOVE_COPY_FILE);

            String tranData = objectMapper.writeValueAsString(fileMoveCopyDto);

            msg.addString(tranData);
            byte[] data = msg.Finish();
            client.send(data);
            String responseResult = client.setPoll(1, TimeUnit.MINUTES);

            if (responseResult == null || responseResult.equals(EMPTY)) {
                return ResponseEntity.badRequest().body(EMPTY);
            }
            return ResponseEntity.ok(responseResult);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(EMPTY);
        }
    }

    @DeleteMapping("/{ip}")
    public ResponseEntity deleteFile(@PathVariable String ip, @RequestBody FileDeleteDto fileDeleteDto){
        try {
            if ((fileDeleteDto.getPaths().length <= 0)){
                return ResponseEntity.badRequest().body(EMPTY);
            }

            AsyncFileControlClient client = AsyncFileControlServer.getClient(ip);
            if (client == null) {
                return ResponseEntity.badRequest().body(EMPTY);
            }

            MessagePacker msg = new MessagePacker();
            msg.setEndianType(ByteOrder.BIG_ENDIAN);
            msg.setProtocol(MessageProtocol.DELETE_FILE);

            String tranData = objectMapper.writeValueAsString(fileDeleteDto);

            msg.addString(tranData);
            byte[] data = msg.Finish();
            client.send(data);
            String responseResult = client.setPoll(1, TimeUnit.MINUTES);

            if (responseResult == null || responseResult.equals(EMPTY)) {
                return ResponseEntity.badRequest().body(EMPTY);
            }
            return ResponseEntity.ok(responseResult);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(EMPTY);
        }
    }

    @PostMapping("/upload/{ip}/{path:.+}")
    public ResponseEntity uploadFile(@PathVariable String ip, @PathVariable String path,
                                     @RequestPart(value = "file", required = true)MultipartFile file){
        try {
            if(file == null){
                return ResponseEntity.badRequest().body(EMPTY);
            }
            AsyncFileControlClient client = AsyncFileControlServer.getClient(ip);
            path = preProcessing(path);

            if (client == null || path == null || path.equals(EMPTY)) {
                return ResponseEntity.badRequest().body(EMPTY);
            }

            InputStream is = file.getInputStream();
            int readCount = 0;
            byte[] buffer = new byte[2097152];
            final int[] offSet = {0};

            if ((readCount = is.read(buffer)) != -1) {
                MessagePacker msg = new MessagePacker();
                msg.setEndianType(ByteOrder.BIG_ENDIAN);
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
                            byte[] newBuff = new byte[2097152];
                            if ((readCount = is.read(newBuff)) != -1) {
                                MessagePacker msg = new MessagePacker();
                                msg.setEndianType(ByteOrder.BIG_ENDIAN);
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
                                msg.setEndianType(ByteOrder.BIG_ENDIAN);
                                msg.setProtocol(MessageProtocol.FILE_UPLOAD);
                                msg.addString(finalPath);
                                msg.addString(file.getOriginalFilename());
                                msg.add(-1);
                                msg.getBuffer().flip();

                                client.getSocketChannel().write(msg.getBuffer(), msg.getBuffer(), new CompletionHandler<Integer, ByteBuffer>() {
                                    @Override
                                    public void completed(Integer result, ByteBuffer attachment) { }
                                    @Override
                                    public void failed(Throwable exc, ByteBuffer attachment) { }
                                });
                            }
                        } catch (Exception e) {
                            logger.error("[File Api - Upload] {}", e.getMessage());
                        }
                    }
                    @Override
                    public void failed(Throwable exc, ByteBuffer attachment) {
                    }
                });
            }
            String responseResult = client.setPoll(1, TimeUnit.MINUTES);

            if(responseResult == null || responseResult.equals(EMPTY)){
                return ResponseEntity.badRequest().body(EMPTY);
            }
            return ResponseEntity.ok(responseResult);

        } catch (Exception e) {
            return ResponseEntity.badRequest().body(EMPTY);
        }
    }

    @GetMapping("/download/{ip}/{path:.+}/{fileName}")
    public ResponseEntity downloadFile(@PathVariable String ip, @PathVariable String path, @PathVariable String fileName){
        try {
            AsyncFileControlClient client = AsyncFileControlServer.getClient(ip);
            path = preProcessing(path);
            if(client == null || path == null || path.equals(EMPTY)){
                return ResponseEntity.badRequest().body(EMPTY);
            }

            MessagePacker msg = new MessagePacker();
            msg.setEndianType(ByteOrder.BIG_ENDIAN);
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

            String result = client.setPoll(2, TimeUnit.MINUTES);

            if(result.equals("success")){
                byte[] fileContent = client.getByteInQueue();

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
