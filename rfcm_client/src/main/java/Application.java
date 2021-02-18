import client.FileControlClient;
import client.FileProvider;
import config.PropertiesReader;
import model.ServerInfo;
import service.FileService;

public class Application {

    public static void main(String[] args) {

//        ServerInfo serverInfo = PropertiesReader.getServerInfoFromPropertiesValue();
//        new FileControlClient(serverInfo).startClient();

        //FileService.getDirectoryInRoot();
        //FileService.getUnderLineDirectory("C:\\KiwoomHero4bin\\globalticker");
        //FileService.getFilesInDirectory("C:\\KiwoomHero4");
        FileService.getFilesInDirectory("C:\\KiwoomHero4");

        //FileService.getDirectoryInRoot();

    }
}
