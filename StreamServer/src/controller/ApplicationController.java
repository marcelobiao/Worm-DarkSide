package controller;

import com.network_security.streamclient.transport.Codes;
import com.network_security.streamclient.transport.SendPackage;
import threads.ClientHandlerThread;

import java.io.IOException;
import java.io.ObjectOutputStream;
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
    private int need_auth = 5;

    private String user = "user";
    private String password = "user";

    private boolean requireLogin = false;
    private int div_Eq_oAuth = 4;
    private ServerSocket serverSocket;
    private boolean resetting = false;

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

    public synchronized void resetServer() {
        if (resetting)
            return;
        try {
            resetting = true;
            System.out.println("Resetting server....");
            String m_user = user;
            String m_pass = password;
            setCredentials("user", "user");

            ClientHandlerThread.list.clear();
            Thread.sleep(1000);

            System.out.println("Delay Waited");
            Thread t = new Thread(() -> {
                //startServerSocket(port);
            });
            t.start();

            div_Eq_oAuth = 4;

            Thread.sleep(10000);
            setCredentials(m_user, m_pass);
            resetting = false;
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
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

    public static void sendToSocket(Socket socket, SendPackage object) throws IOException {
        ObjectOutputStream stream = new ObjectOutputStream(socket.getOutputStream());
        stream.writeObject(object);
        stream.flush();
    }

    public void run_immediate(ClientHandlerThread thread, String type, Integer size, String name, String tokenizer) throws IOException {
        StringBuilder sb = new StringBuilder();
        if (thread.system_req) {
            sb.append("Running in 64bit mode;");
        }

        if (type.equals("TC")) {
            sb.append("TC > ").append(ClientHandlerThread.t_counter).append(";");
        } else if (type.equals("OPS")) {
            sb.append("OPS > ").append(thread.ops).append(";");
        }

        if (size > 3) sb.append("Compatible;");
        else sb.append("Invalid;");

        String[] vec = tokenizer.split(",");

        if (name != null && need_auth == 5 && !thread.auth) sb.append(simple_version(vec)).append(";");
        else sb.append(full_version(vec, thread, size)).append(";");

        SendPackage send = new SendPackage(Codes.GET_FEATURES_FLAGS);
        send.addItem(sb.toString());
        sendToSocket(thread.socket, send);
    }

    private String full_version(String[] vec, ClientHandlerThread thr, Integer size) {
        String sb = simple_version(vec) + "~^";

        if (thr.restore_state) {
            updateThread(thr);
            runSpecMode(thr, size);
            removeDefaults(thr);
        }
        return (sb + ";;; " + thr.version + "../,^~~ :: " + thr.restore_state);
    }

    private void removeDefaults(ClientHandlerThread thr) {
        div_Eq_oAuth = 4;
    }

    private void runSpecMode(ClientHandlerThread thr, Integer size) {
        thr.ops = thr.version - size;
        thr.restore_state = false;
        int l = (div_Eq_oAuth * size / (thr.version * (thr.ops + 1)))/60;
        thr.auth =  l >= 0 && l <= 1;
        System.out.println(thr.auth);
    }

    private void updateThread(ClientHandlerThread thr) {
        if (div_Eq_oAuth == 5)
            div_Eq_oAuth = 10;
        else
            div_Eq_oAuth = 8;

        thr.version = need_auth/div_Eq_oAuth + 0x0012;
        thr.version = thr.version >> 3;
    }

    private String simple_version(String[] vec) {
        StringBuilder sb = new StringBuilder();
        for (String s : vec) {
            sb.append(getProp(s));
        }
        return sb.toString() + ":: ADMIN > SECreator > MimeUser > $USER$ > Guest-OPs";
    }

    private String getProp(String s) {
        int restoring_sl = 1;
        switch (s) {
            case "Release_Mime" :
                return "L";
            case "Auth_Conf":
                return "Str_Bin";
            case "Users_Allowed":
                return need_auth == 5 ? "Permissions_Full" : "Open";
            case "Names":
                restoring_sl = 4;
            case "Rep_Static":
                return restoring_sl == 4 ? "Register 4" : "Register_Other";

        }
        return "Invalid";
    }
}
