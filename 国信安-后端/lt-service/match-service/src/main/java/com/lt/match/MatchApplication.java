package com.lt.match;

import com.lt.match.server.WebSocketServer;
import com.lt.model.message.chat.SingleChatImageMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

import java.net.InetSocketAddress;

/**
 * @author Lhz
 */

@EnableDiscoveryClient
@SpringBootApplication
public class MatchApplication implements CommandLineRunner {


    private final WebSocketServer webSocketServer;
    @Autowired
    public MatchApplication(WebSocketServer webSocketServer) {
        this.webSocketServer = webSocketServer;
    }

    public static void main(String[] args) {
        SpringApplication.run(MatchApplication.class, args);
    }

    @Override
    public void run(String... args) {
        webSocketServer.run(new InetSocketAddress(7777));
    }

}



