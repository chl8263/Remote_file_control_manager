import client.FileControlClient;
import config.PropertiesReader;
import model.ServerInfo;

public class Application {

    public static void main(String[] args) throws Exception {

        ServerInfo serverInfo = PropertiesReader.getServerInfoFromPropertiesValue();

        new FileControlClient(serverInfo).startClient();

//        for(int i = 0; i<100; i++){
//            Thread thread = new Thread(new Runnable() {
//                @Override
//                public void run() {
//                    new FileControlClient(serverInfo).startClient();
//                }
//            });
//            thread.start();
//        }

//        for(int i = 0; i<2; i++){
//            Thread thread = new Thread(new Runnable() {
//                @Override
//                public void run() {
//                    new FileControlClient(serverInfo).startClient();
//                }
//            });
//            thread.start();
//        }
    }
}
