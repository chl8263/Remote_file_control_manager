package client;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import model.FileInfo;
import model.DirectoryInfo;

import java.io.IOException;
import java.nio.file.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class FileProvider {

    private final static Logger LOG = Logger.getLogger(String.valueOf(FileProvider.class));

    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static List<String> getDirectoryInRoot(){

        List<String> directoryList = new ArrayList<>();
        for (Path p : FileSystems.getDefault().getRootDirectories()) {
            directoryList.add(String.valueOf(p));
        }

        return directoryList;
    }

    public static List<String> getUnderLineDirectory(String pathName) throws IOException {

        List<String> directoryList = new ArrayList<>();

        DirectoryStream<Path> dir = Files.newDirectoryStream(Paths.get(pathName));

        for (Path file : dir) {
            if (!Files.isHidden(file) && Files.isDirectory(file)) {
                directoryList.add(String.valueOf(file.getFileName()));
            }
        }

        return directoryList;
    }

    public static DirectoryInfo getFilesInDirectory(String pathName) throws IOException {

        DirectoryInfo fileInfoDto = new DirectoryInfo();

        DirectoryStream<Path> dir = Files.newDirectoryStream(Paths.get(pathName));
        for (Path file : dir) {
            FileInfo fileInfo = new FileInfo();
            if (!Files.isHidden(file)) {
                fileInfo.setName(String.valueOf(file.getFileName()));

                if (Files.isDirectory(file)) {
                    fileInfo.setType("file");
                } else {
                    fileInfo.setType("directory");
                }

                SimpleDateFormat df = new SimpleDateFormat("MM/dd/yyyy HH:mm");
                var modifiedFileDate = df.format(Files.getLastModifiedTime(file).toMillis());
                fileInfo.setDateModified(modifiedFileDate);

                long bytes = Files.size(file);
                if (bytes > 0L) {
                    String fileSize = String.format("%,dKB", bytes / 1024);
                    fileInfo.setSize(fileSize);
                }
            }
            fileInfoDto.getFileList().add(fileInfo);
        }

        return fileInfoDto;
    }

    public static String test(String pathName) {
        Path path = Paths.get("/src/pathexam/PathExample.java");
        return "true";
    }
}
