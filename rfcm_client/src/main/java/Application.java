import client.FileControlClient;
import client.FileProvider;
import config.PropertiesReader;
import model.info.ServerInfo;

public class Application {

    public static void main(String[] args) throws Exception {

        //FileProvider.copyFile("C:\\test14\\ES_최원균.jpg", "C:\\test6");
        //FileProvider.test();
        //FileProvider.copyFolderTest("C:/test14", "C:/test6");
        //FileProvider.copyFolderTest2();

        ServerInfo serverInfo = PropertiesReader.getServerInfoFromPropertiesValue();

        //new FileControlClient(serverInfo).startClient();

        for (int i = 0; i < 2; i++) {
            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    new FileControlClient(serverInfo).startClient();
                }
            });
            thread.start();
            Thread.sleep(10);
        }

//        for(int i = 0; i<100; i++){
//            Thread thread = new Thread(new Runnable() {
//                @Override
//                public void run() {
//                    new FileControlClient(serverInfo).startClient();
//                }
//            });
//            thread.start();
//        }

//        for(int i = 0; i<1024; i++){
//            Thread thread = new Thread(new Runnable() {
//                @Override
//                public void run() {
//                    new FileControlClient(serverInfo).startClient();
//                }
//            });
//            thread.start();
//            Thread.sleep(30);
//        }
    }
}
