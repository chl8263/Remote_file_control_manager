package com.ewan.rfcm.domain.file.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FileDeleteDto {
    private String [] paths;
}