package com.network_security.streamclient;

import android.app.Application;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;

/**
 * Created by João Paulo on 06/05/2017.
 */

public class StreamApplication extends Application {
    private String ip;
    private int port;

    @Override
    public void onCreate() {
        super.onCreate();
    }

    public void configureServer(String ip, int port) {
        this.ip = ip;
        this.port = port;
    }

    public String getIp() {
        return ip;
    }

    public int getPort() {
        return port;
    }

    public static void sendToSocket(Socket server, Object object) throws IOException {
        Log.i("My_App", "StreamApplication.sendToServer() >> Send Package Started");
        ObjectOutputStream stream = new ObjectOutputStream(server.getOutputStream());
        stream.writeObject(object);
        stream.flush();
        Log.i("My_App", "StreamApplication.sendToServer() >> Send Package Finished");
    }

    public static void sendFile(File file, Socket socket) throws IOException {
        byte[] c_buffer = new byte[1024];
        int bytesRead;

        FileInputStream fileIn = new FileInputStream(file);
        OutputStream outputStream = socket.getOutputStream();

        Log.i("My_App", "StreamApplication.sendFile() >> Ready to send");
        while ((bytesRead = fileIn.read(c_buffer)) != -1) {
            outputStream.write(c_buffer, 0, bytesRead);
            outputStream.flush();
        }
        Log.i("My_App", "StreamApplication.sendFile() >> Sent");

        fileIn.close();
        outputStream.flush();
        outputStream.close();
        Log.i("My_App", "StreamApplication.sendFile() >> Flushed and closed");

    }
}
