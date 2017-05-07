package com.network_security.streamclient.assync_tasks;

import android.os.AsyncTask;

import com.network_security.streamclient.transport.Codes;
import com.network_security.streamclient.transport.SendPackage;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;

/**
 * Created by João Paulo on 06/05/2017.
 */

public class ServerConnectTask extends AsyncTask<String, Void, Boolean> {
    private Exception exception;

    @Override
    protected Boolean doInBackground(String... params) {
        String ip = params[0];
        String port = params[1];

        try {
            SendPackage send = new SendPackage(Codes.OUT);
            Socket socket = new Socket(ip, Integer.parseInt(port));
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
            objectOutputStream.writeObject(send);
            objectOutputStream.flush();
            return true;
        } catch (IOException e) {
            exception = e;
            return false;
        }
    }

    @Override
    protected void onPostExecute(Boolean aBoolean) {
        super.onPostExecute(aBoolean);
    }
}
