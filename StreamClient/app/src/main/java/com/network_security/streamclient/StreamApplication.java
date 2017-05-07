package com.network_security.streamclient;

import android.app.Application;

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
}
