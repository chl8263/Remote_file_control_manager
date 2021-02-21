package protocol;

import lombok.Data;

@Data
public class FileChangeProtocol {
    private String path = "";
    private String beforeName = "";
    private String afterName = "";
    private String extension = "";
}
