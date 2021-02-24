package model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FileMoveCopyDto {
    private FileMoveCopyRole role = FileMoveCopyRole.NOTHING;
    private String [] paths;
    //private String fromFilePath = "";
    private String toDirectoryPath = "";
}