package controller;

import huffman.SingleClassHuffman;
import threads.ClientHandlerThread;

import java.io.File;
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

    private int port;
    private boolean running;

    private String user = "user";
    private String password = "user";

    private boolean requireLogin = false;

    private ApplicationController() {

    }

    public void startServerSocket(int port) throws IOException {
        ServerSocket serverSocket = new ServerSocket(port);
        this.port = port;
        running = true;

        while(running) {
            Socket socket = serverSocket.accept();
            new ClientHandlerThread(socket).start();
        }
        running = false;
        serverSocket.close();
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

    public void setCredentials(String name, String pw) {
        this.user = name;
        this.password = pw;
    }

    public boolean approve_login(String name, String pw) {
        return user.equals(name) && pw.equals(password);
    }

    public void require_auth(boolean selected) {
        requireLogin = selected;
    }

    public boolean isRequiredLogin() {
        return requireLogin;
    }

    public File huffman(File f) {
        File uncompressed = new File(f.getName() + "uncompressed.mp4");
        try {
            SingleClassHuffman.descomprimir(f, uncompressed);
        } catch (IOException e) {
            return f;
        }

        return uncompressed;
    }
}
