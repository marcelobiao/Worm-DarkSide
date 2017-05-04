package view;
/**
 * Created by João Paulo on 03/05/2017.
 */

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class App extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("App.fxml"));
        primaryStage.setTitle("Stream Service");
        primaryStage.setScene(new Scene(root, 1024, 635));
        primaryStage.show();
        primaryStage.setResizable(false);
    }

    @Override
    public void stop() throws Exception {
        super.stop();
        System.exit(0);
    }
}
