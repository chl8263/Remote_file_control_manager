package model.info;

import lombok.Data;

@Data
public class FileInfo{
    private String name;
    private String dateModified;
    private String type;
    private String size = "";
}