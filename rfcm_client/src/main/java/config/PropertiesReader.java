package config;

import model.info.ServerInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Properties;

public class PropertiesReader {

    private static final Logger logger = LoggerFactory.getLogger(PropertiesReader.class);

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
            logger.error("", e);
        }
        return serverInfo;
    }
}
