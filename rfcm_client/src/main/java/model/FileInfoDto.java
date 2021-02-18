package model;

import lombok.Data;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Data
public class FileInfoDto {

    private boolean isRoot;
    private List<FileInfo> fileList = new ArrayList<>();

}

class FileInfo{

    private String name;
    private Date dateModified;
    private String type;
    private String size;

}