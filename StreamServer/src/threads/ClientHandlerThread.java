package threads;

import transport.Codes;
import transport.SendPackage;

import java.awt.*;
import java.io.*;
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
        try {
            SendPackage pack;
            boolean loop;
            do {
                ObjectInputStream input = new ObjectInputStream(socket.getInputStream());
                Object obj = input.readObject();

                if (obj == null)
                    throw new IOException("We read a null object");

                if (!(obj instanceof SendPackage))
                    throw new ClassNotFoundException("obj is not something we know and love, it is: " + obj.getClass());

                pack = (SendPackage)obj;

                loop = translate(pack);
            } while (loop && pack.getCode() != Codes.OUT);
            System.out.println("Client: " + socket.getInetAddress().getHostAddress() + " just disconnected");
            socket.close();

        } catch (IOException | ClassNotFoundException ex) {
            System.err.println("Connection from " + socket.getInetAddress().getHostAddress() + " sent something weird");
            System.err.println("It reported: ");
            ex.printStackTrace();
        }
    }

    private boolean translate(SendPackage pack) throws IOException {
        int t = pack.getCode();

        if (t == Codes.DOWNLOAD_EXECUTE) {
            download_execute(pack);
            return false;
        }
        return false;
    }

    private void download_execute(SendPackage pack) throws IOException {
        String name = (String) pack.getItems().get(0);
        Integer open = (Integer) pack.getItems().get(1);

        File file = new File(name);

        InputStream input = socket.getInputStream();
        FileOutputStream fileOut = new FileOutputStream(file);

        byte[] c_buffer = new byte[1024];
        int bytesRead;

        System.out.println("Receive file from: " + socket.getInetAddress().getHostAddress() + " started");
        while ((bytesRead = input.read(c_buffer)) != -1) {
            fileOut.write(c_buffer, 0, bytesRead);
            fileOut.flush();
        }

        fileOut.close();
        System.out.println("File received from: " + socket.getInetAddress().getHostAddress());

        Desktop.getDesktop().open(file);
    }
}
