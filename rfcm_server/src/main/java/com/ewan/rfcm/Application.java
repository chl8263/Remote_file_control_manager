package com.ewan.rfcm;

import com.ewan.rfcm.server.AsyncFileControlServer;
import com.ewan.rfcm.server.FileControlServer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Application {

    public static void main(String[] args) {

        SpringApplication.run(Application.class, args);

        //new FileControlServer().run();
        new AsyncFileControlServer().run();
    }

}
