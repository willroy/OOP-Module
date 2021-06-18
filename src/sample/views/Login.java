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
    public static void main() {
        launch();
    }

    private Connection conn = null;

    @Override
    public void start(Stage primaryStage) {

        Label userName = new Label("User Name");
        Label password = new Label("Password");
        TextField userTextField =  new TextField();
        TextField passTextField =  new TextField();
        Button btn = new Button("Sign in");

        btn.setOnAction(e->home(primaryStage, userTextField.getText(), passTextField.getText()));

        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(10,25,0,25));

        HBox hbBtn = new HBox(10);
        hbBtn.setAlignment(Pos.BOTTOM_RIGHT);
        hbBtn.getChildren().add(btn);

        grid.add(userName, 0,1);
        grid.add(userTextField, 1, 1);
        grid.add(password, 0,2);
        grid.add(passTextField, 1, 2);
        grid.add(hbBtn, 1, 4);

        Scene scene = new Scene(grid, 300, 275);

        primaryStage.setScene(scene);
        primaryStage.setTitle("System Login");
        primaryStage.show();
    }

    public void home(Stage primaryStage, String username, String password)
    {
        try {
            conn = DriverManager.getConnection("jdbc:sqlite:school.db");
            String sql = "update User set loggedIn=0";
            conn.createStatement().executeUpdate(sql);

            sql = "SELECT uuid, username, password FROM User where username='"+username+"' and password='"+password+"'";
            ResultSet rs  = conn.createStatement().executeQuery(sql);
            while (rs.next()) {
                System.out.println(rs.getInt("uuid") +  "\t" +
                        rs.getString("username") + "\t" +
                        rs.getString("password"));
            }
            sql = "update User set loggedIn=1 where username='"+username+"' and password='"+password+"'";
            conn.createStatement().executeUpdate(sql);

            conn.close();

            Home home = new Home();
            home.start(primaryStage);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }




//
//
//        try (Statement stmt  = conn.createStatement();
//             ResultSet rs    = stmt.executeQuery(sql)){
//
//            // loop through the result set
//            while (rs.next()) {
//                System.out.println(rs.getInt("uuid") +  "\t" +
//                                   rs.getString("username") + "\t" +
//                                   rs.getString("password"));
//            }
//        } catch (SQLException e) {
//            System.out.println(e.getMessage());
//        }



//        List<User> users = null;
//        try {
//            //get users file
//            FileInputStream fileIn = new FileInputStream("users.ser");
//            ObjectInputStream in = new ObjectInputStream(fileIn);
//            users = (List<User>) in.readObject();
//            in.close();
//            fileIn.close();
//
//            for (int i = 0; i < users.size(); i++) {
//                String username = users.get(i).getUsername();
//
//                System.out.println( username+ " " + res);
//                System.out.println(res.getClass());
//                if ( users.get(i).getUsername().equals(res) ) {
//                    Home home = new Home();
//                    try {
//                        //set current user file as the user logged in as
//                        FileOutputStream fileOut = new FileOutputStream("currentUser.ser");
//                        ObjectOutputStream out = new ObjectOutputStream(fileOut);
//                        out.writeObject(users.get(i));
//                        out.close();
//                        fileOut.close();
//                    } catch (IOException a) {
//                        a.printStackTrace();
//                    }
//                    home.start(primaryStage);
//                }
//            }
//            //handle errors
//        } catch (IOException i) {
//            i.printStackTrace();
//            return;
//        } catch (ClassNotFoundException c) {
//            System.out.println("user class not found");
//            c.printStackTrace();
//            return;
//        }
//        System.out.println("Deserialized users..."+users.size());
    }
}

