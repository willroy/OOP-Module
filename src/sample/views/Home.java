package sample.views;

import javafx.application.*;
import javafx.geometry.Insets;
import javafx.geometry.VPos;
import javafx.stage.*;
import javafx.scene.*;
import javafx.scene.layout.*;
import javafx.scene.control.*;
import sample.models.user.User;
import sample.views.test.CreateTests;
import sample.views.test.TakeTests;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.sql.*;
import java.util.List;

public class Home extends Application {
    public static void main(String args[]) {
        launch(args);
    }

    public void start(Stage primaryStage) {

        User user = null;
        try {
            Connection conn = DriverManager.getConnection("jdbc:sqlite:school.db");
            String sql = "SELECT * FROM User where loggedIn = 1";
            Statement stmt  = conn.createStatement();
            ResultSet rs    = stmt.executeQuery(sql);
            while (rs.next()) {
                user = new User(rs.getString("username"), rs.getString("password"), rs.getInt("userTypeID"), rs.getInt("schoolClassID"), true);
            }
            conn.close();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }


        //create all the buttons for all users
        Button createTest = new Button("Create Test");
        Button takeTest = new Button("Tests");
        Button exit = new Button("Exit");

        createTest.setOnAction(e->createTest(primaryStage));
        takeTest.setOnAction(e->takeTest(primaryStage));
        exit.setOnAction(e->back(primaryStage));

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(12);

        grid.setPadding(new Insets(10, 10, 10, 10));

        //only display buttons that should display per user type
        if ( user.getUserTypeID() == 1 ){
            grid.add(createTest, 0, 0);
        }
        if ( user.getUserTypeID() == 2 ){
            grid.add(takeTest,0,0);
        }
        grid.add(exit, 0, 11);

        Scene scene = new Scene(grid, 300, 250);
        scene.getStylesheets().add("sample/stylesheet.css");
        //show scene
        primaryStage.setScene(scene);
        primaryStage.setTitle("Home");
        primaryStage.show();
    }

    public void createTest(Stage primaryStage) {
        //launch create test menu
        CreateTests createTest = new CreateTests();
        createTest.start(primaryStage);
    }

    public void takeTest(Stage primaryStage) {
        //launch take test menu
        TakeTests takeTests = new TakeTests();
        takeTests.start(primaryStage);
    }

    public void back(Stage primaryStage) {
        //go back to login screen
        Login login = new Login();
        login.start(primaryStage);
    }
}
