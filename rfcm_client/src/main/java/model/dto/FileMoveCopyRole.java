package model.dto;

import lombok.Getter;

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
