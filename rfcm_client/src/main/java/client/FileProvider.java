package client;

import com.fasterxml.jackson.databind.ObjectMapper;
import model.DirectoryInfo;
import model.FileInfo;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class FileProvider {

    private final static Logger LOG = Logger.getLogger(String.valueOf(FileProvider.class));

    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static List<String> getDirectoryInRoot() {
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
        if (Paths.get(pathName).getParent() == null) fileInfoDto.setRoot(true);

        DirectoryStream<Path> dir = Files.newDirectoryStream(Paths.get(pathName));
        for (Path file : dir) {
            FileInfo fileInfo = new FileInfo();
            if (!Files.isHidden(file)) {
                fileInfo.setName(String.valueOf(file.getFileName()));

                if (Files.isDirectory(file)) {
                    fileInfo.setType("directory");
                } else {
                    fileInfo.setType("file");
                    long bytes = Files.size(file);
                    if (bytes > 0L) {
                        String fileSize = String.format("%,dKB", bytes / 1024);
                        fileInfo.setSize(fileSize);
                    }
                }
                SimpleDateFormat df = new SimpleDateFormat("yyyy/MM/dd HH:mm");
                var modifiedFileDate = df.format(Files.getLastModifiedTime(file).toMillis());
                fileInfo.setDateModified(modifiedFileDate);
                fileInfoDto.getFileList().add(fileInfo);
            }
        }
        return fileInfoDto;
    }

    public static boolean changeFileName(String pathName, String beforeName, String afterName, String extension) throws Exception {

        String finalExtension = "";
        if (!extension.equals("")) {
            finalExtension = "." + extension;
        }
        File file1 = new File(pathName + "/" + beforeName + finalExtension);
        if(!file1.exists()){
            throw new IllegalArgumentException("The file want to change not found");
        }
        if(!file1.canWrite()){
            throw new IllegalArgumentException("The file access deny");
        }
        File file2 = new File(pathName + "/" + afterName + finalExtension);
        if(file2.exists()){
            throw new IllegalArgumentException("Cannot change file name because of already exist same file name");
        }
        var result = file1.renameTo(file2);
        return result;
    }

    public static boolean moveFile(String fromFilePath, String toDirectoryPath, String fileName) {
        try {
            Path file = Paths.get(fromFilePath);
            Path movePath = Paths.get(toDirectoryPath+ "/" + fileName);

            if (file == null || movePath == null) throw new NullPointerException();

            Files.move(file, movePath, StandardCopyOption.REPLACE_EXISTING);
            return true;
        }catch (Exception e){
            return false;
        }
    }

    public static boolean copyFile(String fromFilePath, String toDirectoryPath, String fileName) {
        try {
            Path file = Paths.get(fromFilePath);
            Path movePath = Paths.get(toDirectoryPath+ "/" + fileName);

            if (file == null || movePath == null) throw new NullPointerException();

            Files.copy(file, movePath);
            return true;
        }catch (Exception e){
            return false;
        }
    }
}
