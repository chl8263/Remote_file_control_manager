package com.ewan.rfcm.domain.file.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FileMoveCopyDto {
    private String fileName = "";
    private String fromFilePath = "";
    private String toDirectoryPath = "";
}
