package service;

import client.FileProvider;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import model.dto.FileMoveCopyRole;
import model.info.DirectoryInfo;
import model.dto.FileMoveCopyDto;
import model.ResponseModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.AccessDeniedException;
import java.util.List;

public class FileService {

    private static final Logger logger = LoggerFactory.getLogger(FileService.class);

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
        try {
            return objectMapper.writeValueAsString(responseModel);
        } catch (JsonProcessingException e) {
            logger.error("", e);
            return getNativeJsonParsingErrorResponse();
        }
    }

    public static String getUnderLineDirectory(String pathName){
        ResponseModel<List<String>> responseModel = new ResponseModel<>();
        try{
            List<String> data = FileProvider.getUnderLineDirectory(pathName);
            responseModel.setResponseData(data);

        }catch (AccessDeniedException ed){
            responseModel.setError(true);
            responseModel.setErrorMsg("Access denied to reach");
        }catch (Exception e){
            responseModel.setError(true);
            responseModel.setErrorMsg(e.getMessage());
        }
        try {
            return objectMapper.writeValueAsString(responseModel);
        } catch (JsonProcessingException e) {
            logger.error("", e);
            return getNativeJsonParsingErrorResponse();
        }
    }

    public static String getFilesInDirectory(String pathName) {
        ResponseModel<DirectoryInfo> responseModel = new ResponseModel<>();
        try{
            DirectoryInfo data = FileProvider.getFilesInDirectory(pathName);
            responseModel.setResponseData(data);
        }catch (AccessDeniedException ed){
            responseModel.setError(true);
            responseModel.setErrorMsg("Access denied to reach");
        } catch (Exception e){
            responseModel.setError(true);
            responseModel.setErrorMsg(e.getMessage());
        }
        try {
            return objectMapper.writeValueAsString(responseModel);
        } catch (JsonProcessingException e) {
            logger.error("", e);
            return getNativeJsonParsingErrorResponse();
        }
    }

    public static String changeFileName(String pathName, String beforeName, String afterName, String extension) {
        ResponseModel<Boolean> responseModel = new ResponseModel<>();
        try{
            boolean isChanged = FileProvider.changeFileName(pathName, beforeName, afterName, extension);
            responseModel.setResponseData(isChanged);
            if(!isChanged){
                responseModel.setError(true);
                responseModel.setErrorMsg("Cannot change file name, file busy...");
            }
        }catch (AccessDeniedException ed){
            responseModel.setError(true);
            responseModel.setErrorMsg("Access denied");
        }catch (Exception e){
            responseModel.setError(true);
            responseModel.setErrorMsg(e.getMessage());
        }
        try {
            return objectMapper.writeValueAsString(responseModel);
        } catch (JsonProcessingException e) {
            logger.error("", e);
            return getNativeJsonParsingErrorResponse();
        }
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
        try {
            return objectMapper.writeValueAsString(responseModel);
        } catch (JsonProcessingException e) {
            logger.error("", e);
            return getNativeJsonParsingErrorResponse();
        }
    }

    public static String deleteFile(String [] paths) {
        ResponseModel<List<String>> responseModel = new ResponseModel<>();
        try{
            List<String> result = FileProvider.deleteFile(paths);
            responseModel.setResponseData(result);
            if(result.size() > 0) responseModel.setError(true);
        }catch (Exception e){
            responseModel.setError(true);
            responseModel.setErrorMsg(e.getMessage());
        }
        try {
            return objectMapper.writeValueAsString(responseModel);
        } catch (JsonProcessingException e) {
            logger.error("", e);
            return getNativeJsonParsingErrorResponse();
        }
    }

    private static String getNativeJsonParsingErrorResponse(){
        return "{\"error\":false,\"errorMsg\":\"Json parsing error on server, please check server.\",\"responseData\":true}";
    }
}
