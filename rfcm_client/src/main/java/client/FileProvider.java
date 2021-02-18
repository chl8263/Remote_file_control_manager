package client;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import model.FileInfoDto;

import java.io.IOException;
import java.nio.file.*;
import java.text.SimpleDateFormat;
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

    public static String getFilesInDirectory(String pathName){

        String result = "";
        try(DirectoryStream<Path> dir = Files.newDirectoryStream(Paths.get(pathName))){
            List<FileInfoDto> fileList = new ArrayList<>();
            for(Path file : dir){
                if(!Files.isHidden(file)){
                    System.out.print(file.getFileName());
                    System.out.print(" ");
                    if(Files.isDirectory(file)){
                        System.out.print("Directory");
                    }else {
                        System.out.print("File");
                    }
                    System.out.print(" ");
                    SimpleDateFormat df = new SimpleDateFormat("MM/dd/yyyy HH:mm");
                    var a = df.format(Files.getLastModifiedTime(file).toMillis());
                    System.out.print(a);
                    System.out.print(" ");
                    long bytes = Files.size(file);
                    if(bytes == 0L){

                    }
                    String fileSize = String.format("%,dKB", bytes / 1024);
                    System.out.println(fileSize);
                    System.out.print(" ");
                    //fileList.add(String.valueOf(file.getFileName()));
                    //System.out.println(String.valueOf(file.getFileName()));
                }
                result = objectMapper.writeValueAsString(fileList);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }
}
