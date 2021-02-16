import client.FileControlClient;
import config.PropertiesReader;
import model.ServerInfo;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class Application {

    public static void main(String[] args) {

        ServerInfo serverInfo = PropertiesReader.getServerInfoFromPropertiesValue();
        new FileControlClient(serverInfo).startClient();

    }
}
