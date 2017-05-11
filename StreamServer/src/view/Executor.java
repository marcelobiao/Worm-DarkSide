package view;

import controller.ApplicationController;

import java.io.IOException;

/**
 * Created by João Paulo on 10/05/2017.
 */
public class Executor {
    public static void main(String[] args) {
        int port = 40000;
        String user = "user";
        String pass = "user";

        if (args.length >= 1)
            port = Integer.parseInt(args[0]);
        else if (args.length == 3) {
            user = args[1];
            pass = args[2];
        }

        System.out.println("Started Server> Port: " + port + " // USER: " + user + " // PASSWORD: " + pass);

        int finalPort = port;
        Thread worker = new Thread(() -> {
            try {
                ApplicationController.getInstance().startServerSocket(finalPort);
            } catch (IOException e) {
                System.out.println("Thread finished");
            }
        });

        worker.start();
        ApplicationController.getInstance().setCredentials(user, pass);

    }
}
