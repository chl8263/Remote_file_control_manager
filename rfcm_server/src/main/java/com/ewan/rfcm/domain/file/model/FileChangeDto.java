package com.ewan.rfcm.domain.file.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FileChangeDto {
    private String path = "";
    private String beforeName = "";
    private String afterName = "";
    private String extension = "";
}
