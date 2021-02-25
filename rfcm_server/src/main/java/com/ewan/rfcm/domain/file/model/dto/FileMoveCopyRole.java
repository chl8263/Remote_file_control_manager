package com.ewan.rfcm.domain.file.model.dto;

import lombok.Getter;

import java.util.Arrays;
import java.util.NoSuchElementException;

@Getter
public enum FileMoveCopyRole {
    NOTHING("")
    , MOVE("MOVE")
    , COPY("COPY")
    ;

    private String roleName;

    FileMoveCopyRole(String roleName){
        this.roleName = roleName;
    }
}
