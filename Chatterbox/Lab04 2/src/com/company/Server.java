package com.company;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class Server {

    private ArrayList<Instance> instances = new ArrayList<>();

    public void startServer(int port) {
        try {
            ServerSocket serversocket = new ServerSocket(port);

            ServerInput input = new ServerInput(this);
            input.start();

            System.out.println("Server started");

            while (true) {
                Socket clientSocket = serversocket.accept();
                System.out.println("Client connected");

                Instance instance = new Instance(clientSocket, this);
                instances.add(instance);
                instance.start();


            }
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    public void sendToAllClients(String message, String authorIp) {
        for (Instance instance : instances) {
            Socket socket = instance.getSocket();
            if (!authorIp.equals(instance.getIp())) {
                try {
                    PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                    out.println(message);
                } catch (Exception e) {
                    System.out.println(e);
                }
            }
        }
    }

    public void removeClient(String ip) {
        instances.removeIf(instance -> ip.equals(instance.getIp()));
    }
}


class Instance extends Thread {
//einzelne Threads für einzelne User mit denen der Server gleichzeitig kommunizieren kann
//Instances werden in ArrayList in Server gespeichert

    private Socket socket;
    private Server server;
    private String ip;

    public Instance(Socket socket, Server server) {
        //Verbindung zu Client
        this.socket = socket;
        this.server = server;
        ip = socket.getRemoteSocketAddress().toString();
    }

    public void run() {
        try {

            //to reply to the client
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);

            //to read the message of the client
            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            String username = reader.readLine();
            server.sendToAllClients(username + " joined the chat.", ip);

            while (true) {
                String message = reader.readLine();
                if (message == null)
                    break;

                String response = username + ": " + message;
                server.sendToAllClients(response, ip);
            }
            out.close();
            reader.close();
            socket.close();
            server.removeClient(ip);
            server.sendToAllClients(username + " left the chat.", ip);

            System.out.println("Client disconnected");

        } catch (IOException e) {
            System.out.println("Client disconnected");
        }
    }

    public Socket getSocket() {
        return socket;
    }

    public String getIp() {
        return ip;
    }
}

class ServerInput extends Thread {
    //der Input der beim Client ankommt gesendet vom Server
    private Server server;

    public ServerInput(Server server) {
        this.server = server;
    }

    public void run() {
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

            while (true) {
                String message = reader.readLine();
                if (!message.equals(""))
                    //wenn die Eingabe am Server nicht "" ist, wird der Input an Clients gesendet
                    server.sendToAllClients("Server: " + message, "");
            }

        } catch (IOException e) {
            System.out.println(e);
        }
    }
}