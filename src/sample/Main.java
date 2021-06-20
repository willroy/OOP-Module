package sample;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import sample.views.DBLoginManager;
import sample.views.Home;
import sample.views.Login;

public class Main {
    public static void main(String[] args) {
        Login login = new Login();
        login.main();
    }
}
