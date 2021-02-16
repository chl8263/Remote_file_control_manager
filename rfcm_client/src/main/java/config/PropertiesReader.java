package config;

import model.ServerInfo;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Properties;
import java.util.logging.Logger;

public class PropertiesReader {

    private final static Logger LOG = Logger.getLogger(String.valueOf(PropertiesReader.class));

    public final static ServerInfo getServerInfoFromPropertiesValue(){
        ServerInfo serverInfo = new ServerInfo();
        String propFileName = "app.properties";

        try(InputStream inputStream = PropertiesReader.class.getClassLoader().getResourceAsStream(propFileName);){
            Properties prop = new Properties();

            if (inputStream != null) {
                prop.load(inputStream);
            } else {
                throw new FileNotFoundException("property file '" + propFileName + "' not found in the classpath");
            }

            serverInfo.setIp(prop.getProperty("ip"));
            serverInfo.setPort(Integer.parseInt(prop.getProperty("port")));

        } catch(Exception e){
            LOG.warning("Cannot trace [app.properties] file");
            System.out.println("Exception : " + e);
        }
        return serverInfo;
    }

}
