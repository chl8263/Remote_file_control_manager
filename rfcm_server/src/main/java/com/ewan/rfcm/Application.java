package com.ewan.rfcm;

import com.ewan.rfcm.connection.AsyncFileControlServer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
        Runnable fileControlRunner = new AsyncFileControlServer();
        Thread fileControlServer = new Thread(fileControlRunner);
        fileControlServer.start();
    }
}
