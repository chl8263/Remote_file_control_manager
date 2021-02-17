package com.ewan.rfcm.domain.file.api;

import com.ewan.rfcm.domain.file.dto.FileResponseDto;
import com.ewan.rfcm.server.AsyncFileControlServer;
import com.ewan.rfcm.server.connection.AsyncFileControlClient;
import com.ewan.rfcm.server.protocol.MessagePacker;
import com.ewan.rfcm.server.protocol.MessageProtocol;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.nio.ByteOrder;
import java.util.concurrent.*;

@RestController
@RequestMapping(value = {"/api/file"}, produces = {MediaType.APPLICATION_JSON_VALUE})
public class FileController {

    private final ObjectMapper objectMapper;

    public FileController(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @GetMapping("/directory/{ip}/{path}")
    public ResponseEntity getRootDirectory(@PathVariable String ip, @PathVariable String path){
        try {
            AsyncFileControlClient client = AsyncFileControlServer.getClient(ip);
            if(client == null){
                return ResponseEntity.badRequest().body("");
            }

            MessagePacker msg = new MessagePacker();
            msg.setEndianType(ByteOrder.BIG_ENDIAN); // Default type in JVM
            if(path.equals("root")){
                msg.setProtocol(MessageProtocol.ROOT_DIRECTORY);
            }else {
                msg.setProtocol(MessageProtocol.DIRECTORY);
            }

            byte[] data = msg.Finish();

            client.send(data);
            String result = client.getQueue().poll(1, TimeUnit.MINUTES);
            if(result == null || result.equals("")){
                return ResponseEntity.badRequest().body("");
            }

            String responseResult = objectMapper.writeValueAsString(new FileResponseDto(result));

            return ResponseEntity.ok(responseResult);

        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return ResponseEntity.ok("");
    }

}
