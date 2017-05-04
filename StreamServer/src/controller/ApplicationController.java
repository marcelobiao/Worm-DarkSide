package controller;

import threads.ClientHandlerThread;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by João Paulo on 03/05/2017.
 */
public class ApplicationController {
    private static ApplicationController instance;

    public static ApplicationController getInstance() {
        if (instance == null)
            instance = new ApplicationController();
        return instance;
    }

    private ServerSocket serverSocket;
    private int port;
    private boolean running;

    private ApplicationController() {

    }

    public void startServerSocket(int port) throws IOException {
        serverSocket = new ServerSocket(port);
        this.port = port;
        running = true;

        while(running) {
            Socket socket = serverSocket.accept();
            new ClientHandlerThread(socket).start();
        }
    }

    public int getPort() {
        return port;
    }

    public boolean isRunningServer() {
        return running;
    }

    public void setRunningServer(boolean running) {
        this.running = running;
    }
}
