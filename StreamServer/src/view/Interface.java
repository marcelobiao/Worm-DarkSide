package view;

import controller.ApplicationController;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;

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
    @FXML
    public CheckBox requireAuthentication;
    @FXML
    public AnchorPane userLoginLayout;
    @FXML
    public Button confirmCredentials;
    @FXML
    public TextField userInput;
    @FXML
    public TextField passwordInput;

    private String userIP;
    private int userPort;

    public Interface() {

    }

    private boolean checkSomeVariables() {
        try {
            userIP = InetAddress.getLocalHost().getHostAddress();
            userPort = 40000;
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
                    System.out.println("Hehehe");
                }
            });

            worker.start();
        }

        requireAuthentication.setOnAction(event -> {
            ApplicationController.getInstance().require_auth(requireAuthentication.isSelected());
            userLoginLayout.setVisible(requireAuthentication.isSelected());
        });

        userLoginLayout.setVisible(requireAuthentication.isSelected());

        confirmCredentials.setOnMouseClicked(event -> {
            String name = userInput.getText();
            String pw = passwordInput.getText();

            ApplicationController.getInstance().setCredentials(name, pw);
        });

    }

}
