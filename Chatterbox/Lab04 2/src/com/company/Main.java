package com.company;

public class Main {

    public static void main(String[] args) {
        //Start Server
        Server server = new Server();
       server.startServer(8000);

        //Start Client
        Client client = new Client();
        client.startClient("127.0.0.1", 8000);
    }
}
