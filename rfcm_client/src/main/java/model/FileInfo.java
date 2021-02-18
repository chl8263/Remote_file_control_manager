package model;

import lombok.Data;

import java.util.Date;

@Data
public class FileInfo{

    private String name;
    private String dateModified;
    private String type;
    private String size = "";

}