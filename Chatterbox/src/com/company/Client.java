package com.company;

import java.io.*;
import java.net.Socket;

public class Client {

    public void startClient(String ip, int port) {
        try {
            System.out.println("Connecting to server...");
            Socket socket = new Socket(ip, port);

            //um Nachrichten zu schreiben
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);

            //um die Nachrichten des Servers zu lesen (wird an Listener class übergeben)
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            // um zu lesen, was der User auf Clientseite geschrieben hat (wird später an out übergeben um zu schicken)
            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
            System.out.println("Please enter your username:");

            //damit Programm anhält bis Eingabe getätigt wurde:
            // und erst dann fortfährt mit "You joined the chat"
            String username = reader.readLine();
            out.println(username);

            System.out.println("You joined the chat.");

            Listener listener = new Listener(in);
            listener.start();

            while (true) {
                String message = reader.readLine();

                if (message.equals("exit"))
                    break;
                else if (!message.equals(""))
                    out.println(message);
            }

            listener.interrupt();
            out.close();
            reader.close();
            socket.close();
            System.out.println("You left the chat.");

        } catch (Exception e) {
            System.out.println(e);
        }
    }
}

class Listener extends Thread {

    //to read the messages sent by the server
    private BufferedReader reader;

    public Listener(BufferedReader reader) {
        this.reader = reader;
    }

    public void run(){
        while (true) {
            try {
                System.out.println(reader.readLine());
            } catch (IOException e) {
                break;
            }
        }
    }
}
