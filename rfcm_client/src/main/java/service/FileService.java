package service;

import client.FileProvider;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import model.dto.FileMoveCopyRole;
import model.info.DirectoryInfo;
import model.dto.FileMoveCopyDto;
import model.info.ResponseModel;
import java.util.List;
import java.util.OptionalInt;
import java.util.logging.Logger;

public class FileService {

    private final static Logger LOG = Logger.getLogger(String.valueOf(FileService.class));


    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static String getDirectoryInRoot(){
        ResponseModel<List<String>> responseModel = new ResponseModel<>();
        try{
            List<String> data = FileProvider.getDirectoryInRoot();
            responseModel.setResponseData(data);

        }catch (Exception e){
            responseModel.setError(true);
            responseModel.setErrorMsg(e.getMessage());
        }
        String result = null;
        try {
            result = objectMapper.writeValueAsString(responseModel);
        } catch (JsonProcessingException e) {
            LOG.warning(e.getMessage());
        }
        return result;
    }

    public static String getUnderLineDirectory(String pathName){
        ResponseModel<List<String>> responseModel = new ResponseModel<>();
        try{
            List<String> data = FileProvider.getUnderLineDirectory(pathName);
            responseModel.setResponseData(data);

        }catch (Exception e){
            responseModel.setError(true);
            responseModel.setErrorMsg(e.getMessage());
        }
        String result = null;
        try {
            result = objectMapper.writeValueAsString(responseModel);
        } catch (JsonProcessingException e) {
            LOG.warning(e.getMessage());
        }
        return result;
    }

    public static String getFilesInDirectory(String pathName) {
        ResponseModel<DirectoryInfo> responseModel = new ResponseModel<>();
        try{
            DirectoryInfo data = FileProvider.getFilesInDirectory(pathName);
            responseModel.setResponseData(data);
        }catch (Exception e){
            responseModel.setError(true);
            responseModel.setErrorMsg(e.getMessage());
        }
        String result = null;
        try {
            result = objectMapper.writeValueAsString(responseModel);
        } catch (JsonProcessingException e) {
            LOG.warning(e.getMessage());
        }
        return result;
    }

    public static String changeFileName(String pathName, String beforeName, String afterName, String extension) {
        ResponseModel<Boolean> responseModel = new ResponseModel<>();
        try{
            boolean isChanged = FileProvider.changeFileName(pathName, beforeName, afterName, extension);
            responseModel.setResponseData(isChanged);
        }catch (Exception e){
            responseModel.setError(true);
            responseModel.setErrorMsg(e.getMessage());
        }
        String result = null;
        try {
            result = objectMapper.writeValueAsString(responseModel);
        } catch (JsonProcessingException e) {
            LOG.warning(e.getMessage());
        }
        return result;
    }

    public static String moveCopyFile(FileMoveCopyDto fileMoveCopyDto) {
        ResponseModel<List<String>> responseModel = new ResponseModel<>();
        try{
            if(fileMoveCopyDto.getRole() == FileMoveCopyRole.NOTHING) {
                throw new IllegalArgumentException("Invalid type NOTHING.");
            }
            List<String> result = FileProvider.moveCopyFile(fileMoveCopyDto);
            responseModel.setResponseData(result);
            if(result.size() > 0) responseModel.setError(true);
        }catch (Exception e){
            responseModel.setError(true);
            responseModel.setErrorMsg(e.getMessage());
        }
        String result = null;
        try {
            result = objectMapper.writeValueAsString(responseModel);
        } catch (JsonProcessingException e) {
            LOG.warning(e.getMessage());
        }
        return result;
    }

//    public static String moveFile(String fromFilePath, String toDirectoryPath, String fileName) {
//        ResponseModel<Boolean> responseModel = new ResponseModel<>();
//        try{
//            boolean isMoved = FileProvider.moveFile(fromFilePath, toDirectoryPath, fileName);
//            responseModel.setResponseData(isMoved);
//        }catch (Exception e){
//            responseModel.setError(true);
//            responseModel.setErrorMsg(e.getMessage());
//        }
//        String result = null;
//        try {
//            result = objectMapper.writeValueAsString(responseModel);
//        } catch (JsonProcessingException e) {
//            LOG.warning(e.getMessage());
//        }
//        return result;
//    }
//
//    public static String copyFile(String fromFilePath, String toDirectoryPath, String fileName) {
//        ResponseModel<Boolean> responseModel = new ResponseModel<>();
//        try{
//            boolean isCopied = FileProvider.copyFile(fromFilePath, toDirectoryPath, fileName);
//            responseModel.setResponseData(isCopied);
//        }catch (Exception e){
//            responseModel.setError(true);
//            responseModel.setErrorMsg(e.getMessage());
//        }
//        String result = null;
//        try {
//            result = objectMapper.writeValueAsString(responseModel);
//        } catch (JsonProcessingException e) {
//            LOG.warning(e.getMessage());
//        }
//        return result;
//    }
}
