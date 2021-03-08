package client;

import com.fasterxml.jackson.databind.ObjectMapper;
import model.dto.FileMoveCopyDto;
import model.dto.FileMoveCopyRole;
import model.info.DirectoryInfo;
import model.info.FileInfo;

import java.io.File;
import java.io.IOException;
import java.io.InvalidObjectException;
import java.nio.file.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
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
        File tFile = new File(pathName);
        if(!tFile.canRead()){
            throw new AccessDeniedException("Access denied to read");
        }

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
        File tFile = new File(pathName);
        if(!tFile.canRead()){
            throw new AccessDeniedException("Access denied to read");
        }

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
        String beforeFullName = pathName + "/" + beforeName + finalExtension;
        File file1 = new File(beforeFullName);
        if(!file1.exists()){
            throw new IllegalArgumentException("The file want to change not found");
        }
        if(!Files.isWritable(Path.of(beforeFullName))){
            throw new IllegalArgumentException("Write access deny [" + beforeFullName + "]");
        }
        File file2 = new File(pathName + "/" + afterName + finalExtension);
        if(file2.exists()){
            throw new IllegalArgumentException("Cannot change file name because of already exist same file name");
        }
        var result = file1.renameTo(file2);
        return result;
    }

    public static List<String> moveCopyFile(FileMoveCopyDto fileMoveCopyDto) {
        List<String> errorMsgList = new ArrayList<>();
        try {
            String toPath = fileMoveCopyDto.getToDirectoryPath();
            File toFile = new File(toPath);
            if(!toFile.exists()){
                throw new IllegalArgumentException("Not exists folder to move, please check again.");
            }
            for(String path: fileMoveCopyDto.getPaths()){
                File tempFile = new File(path);
                if(!tempFile.exists()){
                    errorMsgList.add("Cannot find file : [" + path + "]");
                    continue;
                }
                if(!Files.isWritable(Path.of(path))){
                    errorMsgList.add("Write access deny [" + path + "]");
                    continue;
                }

                //File tempSameFile = new File(path);
                if(tempFile.renameTo(tempFile)){
                    System.out.println("file is closed");
                }else{
                    // if the file didnt accept the renaming operation
                    System.out.println("file is opened");
                }

                try{
                    if(fileMoveCopyDto.getRole() == FileMoveCopyRole.MOVE){
                        if(tempFile.isFile()){
                            if(!moveFile(path, toPath)){
                                errorMsgList.add("Cannot move file [" + path + "]");
                            }
                        }else if(tempFile.isDirectory()){
                            if(!moveFolder(path, toPath)){
                                errorMsgList.add("Cannot move file [" + path + "]");
                            }
                        }
                    }else if (fileMoveCopyDto.getRole() == FileMoveCopyRole.COPY){
                        if(tempFile.isFile()){
                            if(!copyFile(path, toPath)){
                                errorMsgList.add("Cannot copy file [" + path + "]");
                            }
                        }else if(tempFile.isDirectory()){
                            if(!copyFolder(path, toPath)){
                                errorMsgList.add("Cannot copy file [" + path + "]");
                            }
                        }
                    }
                }catch (Exception e){
                    errorMsgList.add("Cannot conduct file [" + path + "]" + e.getMessage());
                }
            }
            return errorMsgList;
        } catch (Exception e){
            errorMsgList.add(e.getMessage());
            return errorMsgList;
        }
    }

    public static boolean moveFile(String fromFilePath, String toDirectoryPath) throws IOException {
        int index = fromFilePath.lastIndexOf("/");
        String fileName = fromFilePath.substring(index + 1);

        Path file = Paths.get(fromFilePath);
        Path movePath = Paths.get(toDirectoryPath+ "/" + fileName);

        if(new File(toDirectoryPath+ "/" + fileName).exists()){
            throw new InvalidObjectException("Already exist with same file name");
        }

        if (file == null || movePath == null) throw new NullPointerException("Please check path");

        Files.move(file, movePath, StandardCopyOption.REPLACE_EXISTING);
        return true;
    }

    public static boolean copyFile(String fromFilePath, String toDirectoryPath) throws IOException {
        int index = fromFilePath.lastIndexOf("/");
        String fileName = fromFilePath.substring(index + 1);

        Path file = Paths.get(fromFilePath);
        Path movePath = Paths.get(toDirectoryPath+ "/" + fileName);

        if(new File(toDirectoryPath+ "/" + fileName).exists()){
            throw new InvalidObjectException("Already exist with same file name");
        }

        if (file == null || movePath == null) throw new NullPointerException("Please check path");

        Files.copy(file, movePath, StandardCopyOption.REPLACE_EXISTING);
        return true;
    }

    public static boolean moveFolder(String fromFilePath, String toDirectoryPath) throws IOException {
        int index = fromFilePath.lastIndexOf("/");
        String fileName = fromFilePath.substring(index + 1);

        String source = fromFilePath;
        String target = toDirectoryPath;
        File targetDirectory = new File(toDirectoryPath + "/" + fileName);

        if(targetDirectory.exists()){
            throw new InvalidObjectException("Already exist with same directory");
        }
        if(!targetDirectory.mkdir()) {
            throw new InvalidObjectException("Fail to create directory");
        }

        target += "/" + fileName;
        Path sourcePath = Paths.get(source);
        Path targetPath = Paths.get(target);

        MoveFileVisitor visitor = new MoveFileVisitor(sourcePath, targetPath);
        Files.walkFileTree(sourcePath, visitor);
        return true;
    }

    public static boolean copyFolder(String fromFilePath, String toDirectoryPath) throws IOException {
        int index = fromFilePath.lastIndexOf("/");
        String fileName = fromFilePath.substring(index + 1);

        String source = fromFilePath;
        String target = toDirectoryPath;
        File targetDirectory = new File(toDirectoryPath + "/" + fileName);

        if (targetDirectory.exists()) {
            throw new InvalidObjectException("Already exist with same directory");
        }
        if (!targetDirectory.mkdir()) {
            throw new InvalidObjectException("Fail to create directory");
        }

        target += ("/" + fileName);
        Path sourcePath = Paths.get(source);
        Path targetPath = Paths.get(target);

        CopyFileVisitor visitor = new CopyFileVisitor(sourcePath, targetPath);
        Files.walkFileTree(sourcePath, visitor);
        return true;
    }

    public static List<String> deleteFile(String[] paths) throws IOException {
        List<String> errorMsgList = new ArrayList<>();
        try {
            for(String path: paths){
                File tempFile = new File(path);
                if(!tempFile.exists()){
                    errorMsgList.add("Cannot find file : [" + path + "]");
                    continue;
                }
                if(!Files.isWritable(Path.of(path))){
                    errorMsgList.add("Write access deny [" + path + "]");
                    continue;
                }
                try{
                    File rootDir = new File(path);
                    Files.walk(rootDir.toPath())
                            .sorted(Comparator.reverseOrder())
                            .map(Path::toFile)
                            .forEach(File::delete);
                }catch (AccessDeniedException ae) {
                    errorMsgList.add("Access denied [" + path + "]");
                }catch (IOException e) {
                    errorMsgList.add("Cannot delete file [" + path + "]");
                }
            }
        } catch (Exception e){
            errorMsgList.add(e.getMessage());
            return errorMsgList;
        }
        return errorMsgList;
    }
}
