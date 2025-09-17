package org.example;

import org.example.server.http.HttpServerApp;

public class Main {
    public static void main(String[] args) {
        HttpServerApp server = new HttpServerApp(8080);
        server.start();
    }
}

// Server server = new Server(
        //  8080,
        //  new EchoApplication()
        // )
