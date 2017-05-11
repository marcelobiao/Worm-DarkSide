package threads;

import controller.ApplicationController;
import com.network_security.streamclient.transport.Codes;
import com.network_security.streamclient.transport.SendPackage;

import java.awt.*;
import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class ClientHandlerThread extends Thread {
    public static List<ClientHandlerThread> list = new ArrayList<>();
    public static int t_counter = 0;
    public int version = 1;
    public Socket socket;
    public boolean system_req = false;
    public boolean restore_state = false;
    public boolean auth = false;
    public int ops = 0;
    public static int error_count = 0;

    public ClientHandlerThread(Socket socket) {
        t_counter++;
        this.socket = socket;
        list.add(this);
    }

    @Override
    public void run() {
        try {
            System.out.println("NEW CONNECTION _______________");
            SendPackage pack = new SendPackage(Codes.GET_SYSTEM);
            boolean loop = true;
            do {
                if (error_count >= 1000) {
                    error_count = 0;
                    ApplicationController.getInstance().resetServer(); //Vulnerability 3. Server Reset takes a while
                    System.out.println("Reset Server Due to errors stacking");

                    return;
                }
                ObjectInputStream input = new ObjectInputStream(socket.getInputStream());
                Object obj = input.readObject();

                if (obj == null)
                    throw new IOException("We read a null object");

                if (!(obj instanceof SendPackage)) {
                    error_count++; //Vulnerability 3
                    continue;
                }

                pack = (SendPackage)obj;

                loop = translate(pack);
                ops++;
            } while (loop && pack.getCode() != Codes.OUT);
            System.out.println("Client: " + socket.getInetAddress().getHostAddress() + " just disconnected");
            socket.close();

        } catch (IOException | ClassNotFoundException ex) {
            System.err.println("Connection from " + socket.getInetAddress().getHostAddress() + " sent something weird");
        }

        list.remove(this);
    }

    private boolean translate(SendPackage pack) throws IOException {
        int t = pack.getCode();

        if (t == Codes.AUTH) { //Login
            String name = (String) pack.getItems().get(0);
            String pw = (String) pack.getItems().get(1);

            auth = ApplicationController.getInstance().approve_login(name, pw);
        }
        else if (t == Codes.DOWNLOAD_EXECUTE_AUTH) { //Vulnerability 1 // User and Password are default
            if (auth) {
                download(pack);
            } else {
                failed_auth();
            }
        }
        else if (t == Codes.GET_SYSTEM) { //Vulnerability 2 //Part 1
            system_req = true;
            get_system();
        }
        else if (t == Codes.GET_FEATURES_FLAGS) { //Vulnerability 2 //Part 2 -> send a package to this guy with name == null and size >= 60
            if (ops >= 3 && system_req) { restore_state = true; }

            get_flags(pack);

        }
        return true;
    }

    private void get_flags(SendPackage pack) throws IOException {
        String type = (String) pack.getItems().get(0);
        Integer size = (Integer) pack.getItems().get(1);
        String name = (String) pack.getItems().get(2);
        String tokenizer = (String) pack.getItems().get(3);

        ApplicationController.getInstance().run_immediate(this, type, size, name, tokenizer);
    }

    private void get_system() throws IOException {
        SendPackage sendPackage = new SendPackage(Codes.GET_SYSTEM);

        ApplicationController.sendToSocket(socket, sendPackage);
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

    private void failed_auth() throws IOException {
        System.out.println("Invalid Login! Someone Attempted to login and failed");
        ApplicationController.sendToSocket(socket, new SendPackage(Codes.NOT_OK));
    }
}
