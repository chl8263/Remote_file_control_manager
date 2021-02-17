import client.FileControlClient;
import client.FileProvider;
import config.PropertiesReader;
import model.ServerInfo;

public class Application {

    public static void main(String[] args) {

        ServerInfo serverInfo = PropertiesReader.getServerInfoFromPropertiesValue();
        new FileControlClient(serverInfo).startClient();

        //FileProvider.getRootPath();
        //FileProvider.getUnderLineDirectory("C:\\KiwoomHero4bin\\globalticker");

    }
}
