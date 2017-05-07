package com.network_security.streamclient.assync_tasks;

import android.os.AsyncTask;
import android.util.Log;

import com.network_security.streamclient.StreamApplication;
import com.network_security.streamclient.transport.Codes;
import com.network_security.streamclient.transport.SendPackage;

import java.io.File;
import java.io.IOException;
import java.net.Socket;

/**
 * Created by João Paulo on 07/05/2017.
 */

public class SendEncryptedTask extends AsyncTask<String, Void, Boolean> {
    @Override
    protected Boolean doInBackground(String... params) {
        File file = new File(params[0]);
        String ip = params[1];
        int port = Integer.parseInt(params[2]);

        SendPackage sendPackage = new SendPackage(Codes.ENCRYPTED_MEDIA);
        sendPackage.addItem(file.getName());
        sendPackage.addItem(port);

        try {
            Socket socket = new Socket(ip, port);
            StreamApplication.sendToSocket(socket, sendPackage);

            Log.i("My_App", "SendEncryptedTask.doInBackground() >> Start send file");
            StreamApplication.sendFile(file, socket);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
