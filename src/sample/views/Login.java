package sample.views;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

import java.sql.*;


public class Login extends Application {
    //main method that launches the application
    public static void main() {
        launch();
    }

    //intitilise the variable for the server connection
    private Connection conn = null;

    //start the applicaiton
    @Override
    public void start(Stage primaryStage) {
        //create username, password and sign in items (text fields and button) for display
        Label userName = new Label("User Name");
        Label password = new Label("Password");
        TextField userTextField =  new TextField();
        TextField passTextField =  new TextField();
        Button btn = new Button("Sign in");
        //go to home method when sign in button is pressed (try to login)
        btn.setOnAction(e->home(primaryStage, userTextField.getText(), passTextField.getText()));
        //create grid pane to display items and do align / padding
        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(10,25,0,25));
        //create a button for sign in
        HBox hbBtn = new HBox(10);
        hbBtn.setAlignment(Pos.BOTTOM_RIGHT);
        hbBtn.getChildren().add(btn);
        //add each field to a grid that will be displayed on the window
        grid.add(userName, 0,1);
        grid.add(userTextField, 1, 1);
        grid.add(password, 0,2);
        grid.add(passTextField, 1, 2);
        grid.add(hbBtn, 1, 4);
        //create scene with specific size 300 x 275
        Scene scene = new Scene(grid, 300, 275);
        //set the scene for the stage and set the title name, then show
        primaryStage.setScene(scene);
        primaryStage.setTitle("System Login");
        primaryStage.show();
    }

    public void home(Stage primaryStage, String username, String password)
    {
        try {
            //create connection to school db
            conn = DriverManager.getConnection("jdbc:sqlite:school.db");
            //set all users to logged in
            String sql = "update User set loggedIn=0";
            conn.createStatement().executeUpdate(sql);
            //setup sql for testing the username and password against the database
            sql = "SELECT uuid, username, password FROM User where username='"+username+"' and password='"+password+"'";
            ResultSet rs  = conn.createStatement().executeQuery(sql);
            //if there is a match, then set the user to logged in and start the home page
            if (rs.next()) {
                sql = "update User set loggedIn=1 where username='"+username+"' and password='"+password+"'";
                conn.createStatement().executeUpdate(sql);

                conn.close();

                Home home = new Home();
                home.start(primaryStage);
            }

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
}

