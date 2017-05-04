package view;

import controller.ApplicationController;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;

import java.io.IOException;
import java.net.InetAddress;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.ResourceBundle;

/**
 * Created by João Paulo on 03/05/2017.
 */
public class Interface implements Initializable{
    @FXML
    public Label centerText;
    @FXML
    public Label textIP;
    @FXML
    public Label textPort;

    private String userIP;
    private int userPort;

    public Interface() {

    }

    private boolean checkSomeVariables() {
        try {
            userIP = InetAddress.getLocalHost().getHostAddress();
            userPort = (int)Math.floor(Math.random() * 100 + 40000);
            textIP.setText("IP: " + userIP);
            textPort.setText("Port: " + userPort);
        } catch (UnknownHostException e) {
            centerText.setText("Unable to get your IP or generate a port for the program");
            return false;
        }
        return true;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        boolean c = checkSomeVariables();
        if (c) {
            Thread worker = new Thread(() -> {
                try {
                    ApplicationController.getInstance().startServerSocket(userPort);
                } catch (IOException e) {
                    centerText.setText("Server could not be started. Try again");
                    textIP.setText("IP: " + userIP);
                    textPort.setText("Port: ??");
                }
            });

            worker.start();
        }
    }

}
