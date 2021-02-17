package client;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;

public class FileProvider {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static String getDirectoryInRoot(){
        String result = "";
        try {
            List<String> directoryList = new ArrayList<>();
            for (Path p : FileSystems.getDefault().getRootDirectories()) {
                directoryList.add(String.valueOf(p));
            }
            result = objectMapper.writeValueAsString(directoryList);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return result;
    }

    public static String getUnderLineDirectory(String pathName){

        String result = "";

        try(DirectoryStream<Path> dir = Files.newDirectoryStream(Paths.get(pathName))){

            List<String> directoryList = new ArrayList<>();

            for(Path file : dir){
                if(!Files.isHidden(file) && Files.isDirectory(file)){
                    directoryList.add(String.valueOf(file.getFileName()));
                    //System.out.println(String.valueOf(file.getFileName()));
                }
            }

            result = objectMapper.writeValueAsString(directoryList);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    public static String getWholeFileInDirectory(String pathName){

        String result = "";
        try(DirectoryStream<Path> dir = Files.newDirectoryStream(Paths.get(pathName))){
            List<String> directoryList = new ArrayList<>();
            for(Path file : dir){
                if(!Files.isHidden(file) && Files.isDirectory(file)){
                    directoryList.add(String.valueOf(file.getFileName()));
                    System.out.println(String.valueOf(file.getFileName()));
                }
                result = objectMapper.writeValueAsString(directoryList);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }
}
