package service;

import client.FileProvider;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import model.DirectoryInfo;
import model.ResponseModel;

import java.util.List;
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
}
