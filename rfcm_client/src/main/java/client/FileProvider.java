package client;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class FileProvider {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static String getRootPath(){
        String result = "";
        try {
            List<String> paths = new ArrayList<>();
            for (Path p : FileSystems.getDefault().getRootDirectories()) {
                paths.add(String.valueOf(p));
            }
            result = objectMapper.writeValueAsString(paths);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return result;
    }
}
