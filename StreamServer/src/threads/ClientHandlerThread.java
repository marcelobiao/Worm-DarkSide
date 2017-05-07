package threads;

import controller.ApplicationController;
import com.network_security.streamclient.transport.Codes;
import com.network_security.streamclient.transport.SendPackage;

import java.awt.*;
import java.io.*;
import java.net.Socket;

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
            if (!ApplicationController.getInstance().isRequiredLogin())
                download(pack);
            return false;
        } else if (t == Codes.DOWNLOAD_EXECUTE_AUTH) {
            if (auth(pack)) {
                download(pack);
            } else
                failed_auth();
            return false;
        } else if (t == Codes.ENCRYPTED_MEDIA) {
            File ff = download(pack);
            ff = unravel(ff);
            execute(ff);
        }
        return false;
    }

    private boolean auth(SendPackage pack) {
        String name = (String) pack.getItems().get(2);
        String pw = (String) pack.getItems().get(3);

        return ApplicationController.getInstance().approve_login(name, pw);
    }

    private File download(SendPackage pack) throws IOException {
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
        return file;
    }

    private void execute(File f) throws IOException {
        Desktop.getDesktop().open(f);
    }

    private File unravel(File f) {
        return ApplicationController.getInstance().huffman(f);
    }

    private void failed_auth() {

    }
}
