package threads;

import java.net.Socket;

/**
 * Created by João Paulo on 03/05/2017.
 */
public class ClientHandlerThread extends Thread {
    private Socket socket;

    public ClientHandlerThread(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {

    }
}
