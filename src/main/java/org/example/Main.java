package org.example;

import org.example.application.MRPApplication;
import org.example.server.Server;

public class Main {
    public static void main(String[] args) {
        Server server = new Server(
            8080,
            new MRPApplication()
        );
        server.start();
    }
}
