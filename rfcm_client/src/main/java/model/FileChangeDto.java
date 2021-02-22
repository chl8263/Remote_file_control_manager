package model;

import lombok.Data;

@Data
public class FileChangeDto {
    private String path = "";
    private String beforeName = "";
    private String afterName = "";
    private String extension = "";
}
