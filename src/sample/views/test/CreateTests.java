package sample.views.test;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import sample.models.SchoolClass;
import sample.models.test.Test;
import sample.models.test.TestQuestion;
import sample.models.test.TestQuestionMultichoice;
import sample.models.test.TestQuestionText;
import sample.models.user.User;
import sample.views.Home;
import sample.views.Login;

import java.io.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import static javafx.application.Application.launch;

public class CreateTests extends Application {
    private int classID = 0;
    private List<TestQuestion> questions = new ArrayList<TestQuestion>();
    private Boolean addingQuestion = false;
    private TextField classIDText;
    private ComboBox classes;
    private List<SchoolClass> schoolClasses;

    public static void main(String args[]) {
        launch(args);
    }

    public void start(Stage primaryStage) {
        //get file of classes
        ObservableList<String> options = FXCollections.observableArrayList();
        classes = new ComboBox(options);
        try {
            Connection conn = DriverManager.getConnection("jdbc:sqlite:C://Users/willj/IdeaProjects/untitled/school.db");
            String sql = "SELECT * FROM SchoolClass;";
            Statement stmt  = conn.createStatement();
            ResultSet rs    = stmt.executeQuery(sql);
            while (rs.next()) {
                classes.getItems().add(rs.getString("name"));
            }
            conn.close();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        //save exit and add question buttons
        Button addQuestion = new Button("Add Question");
        Button save = new Button("Save");
        Button exit = new Button("Back");

        addQuestion.setOnAction(e -> createQuestion(primaryStage));
        save.setOnAction(e -> back(primaryStage));
        exit.setOnAction(e -> exit(primaryStage));

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(12);

        ScrollPane root = new ScrollPane();

        grid.setPadding(new Insets(10, 10, 10, 10));


        grid.add(addQuestion, 0, 0);
        grid.add(classes, 0, 1);
        grid.add(save, 3, 0);
        grid.add(exit, 4, 0);

        //list questions that have been made
        for (int i = 0; i < questions.size(); i++) {
            if ( questions.get(i).getClass() == TestQuestionMultichoice.class ) {
                Label label = new Label("Question "+(i+1)+" Multiple Choice");
                grid.add(label, 0, 2+i);
            }
            if ( questions.get(i).getClass() == TestQuestionText.class ) {
                Label label = new Label("Question "+(i+1)+" Text Input");
                grid.add(label, 0, 2+i);
            }
        }



        root.setContent(grid);

        Scene scene = new Scene(root, 300, 250);
        scene.getStylesheets().add("sample/stylesheet.css");
        //show scene
        primaryStage.setScene(scene);
        primaryStage.setTitle("Home");
        primaryStage.show();
    }

    public void createQuestion(Stage primaryStage) {
        ObservableList<String> options = FXCollections.observableArrayList("Multi-Select", "TextBox");
        ComboBox comboBox = new ComboBox(options);

        List<TextField> multiTexts = new ArrayList<TextField>();
        List<CheckBox> multiChecks = new ArrayList<CheckBox>();
        //create all the buttons for each question type
        multiTexts.add(new TextField("Question Text"));
        multiTexts.add(new TextField());
        multiTexts.add(new TextField());
        multiTexts.add(new TextField());
        multiTexts.add(new TextField());

        multiChecks.add(new CheckBox());
        multiChecks.add(new CheckBox());
        multiChecks.add(new CheckBox());
        multiChecks.add(new CheckBox());

        TextField textBox = new TextField();

        //set all boxes to invisible for multibox select
        for (int i = 1; i < 5; i++) {
            multiTexts.get(i).setVisible(false);
            multiChecks.get(i-1).setVisible(false);
        }
        //make text box invisible for text question type
        textBox.setVisible(false);

        //combobox if changed, make the corosponding question type boxes appear
        comboBox.getSelectionModel().selectedItemProperty().addListener( (o, oldValue, newValue) -> {
            if ( newValue == "Multi-Select") {
                for (int i = 1; i < 5; i++) {
                    multiTexts.get(i).setVisible(true);
                    multiChecks.get(i-1).setVisible(true);
                }
                textBox.setVisible(false);
            } else if ( newValue == "TextBox") {
                textBox.setVisible(true);
                for (int i = 1; i < 5; i++) {
                    multiTexts.get(i).setVisible(false);
                    multiChecks.get(i-1).setVisible(false);
                }
            }
        });

        Button save = new Button("Save");
        Button quit = new Button("Back");

        //if save, then create the question class, for a later save
        save.setOnAction(e -> {
            if ( !comboBox.getSelectionModel().isEmpty() ) {
                if ( comboBox.getSelectionModel().getSelectedItem() == "Multi-Select" ) {
                    List<String> incorrectAnswers = new ArrayList<String>();
                    List<String> correctAnswers = new ArrayList<String>();
                    for (int i = 0; i < 4; i++) {
                        if (multiChecks.get(i).isSelected()) { correctAnswers.add(multiTexts.get(i).getText()); }
                        if (!multiChecks.get(i).isSelected()) { incorrectAnswers.add(multiTexts.get(i).getText()); }
                    }
                    TestQuestionMultichoice question = new TestQuestionMultichoice(multiTexts.get(0).getText(), incorrectAnswers, correctAnswers);
                    questions.add(question);
                    start(primaryStage);
                }
                if ( comboBox.getSelectionModel().getSelectedItem() == "TextBox" ) {
                    TestQuestionText question = new TestQuestionText(multiTexts.get(0).getText(), textBox.getText());
                    questions.add(question);
                    start(primaryStage);
                }
            }
        });

        quit.setOnAction(e -> { start(primaryStage); });

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(12);

        grid.setPadding(new Insets(10, 10, 10, 10));

        //add all the boxes to the grid
        grid.add(comboBox, 0, 0);
        grid.add(save, 2, 0);
        grid.add(quit, 3, 0);
        grid.add(textBox, 0, 3);
        grid.add(multiTexts.get(0), 0, 2);
        grid.add(multiTexts.get(1), 0, 3);
        grid.add(multiTexts.get(2), 0, 4);
        grid.add(multiTexts.get(3), 0, 5);
        grid.add(multiTexts.get(4), 0, 6);
        grid.add(multiChecks.get(0), 1, 3);
        grid.add(multiChecks.get(1), 1, 4);
        grid.add(multiChecks.get(2), 1, 5);
        grid.add(multiChecks.get(3), 1, 6);

        Scene scene = new Scene(grid, 300, 250);
        scene.getStylesheets().add("sample/stylesheet.css");
        //display scene
        primaryStage.setScene(scene);
        primaryStage.setTitle("Home");
        primaryStage.show();
    }

    public void back(Stage primaryStage) {

        if ( !classes.getSelectionModel().isEmpty() ) {
            //save the test by serialization
            int testCount = 0;
            try {
                Connection conn = DriverManager.getConnection("jdbc:sqlite:C://Users/willj/IdeaProjects/untitled/school.db");
                String sql = "SELECT count(*) AS total FROM Test;";
                Statement stmt  = conn.createStatement();
                ResultSet rs    = stmt.executeQuery(sql);
                while (rs.next()) {
                    testCount = rs.getInt("total");
                }
                conn.close();
                conn = DriverManager.getConnection("jdbc:sqlite:C://Users/willj/IdeaProjects/untitled/school.db");
                sql = "INSERT INTO Test(uuid, schoolClassID) VALUES("+(testCount+1)+", "+(classes.getSelectionModel().getSelectedIndex()+1)+")";
                stmt  = conn.createStatement();
                stmt.executeUpdate(sql);
                conn.close();
                for (int i = 0; i < questions.size(); i++) {
//                    if ( questions.get(i).getClass() == TestQuestionMultichoice.class ) {
//                        TestQuestionMultichoice question = (TestQuestionMultichoice)questions.get(i);
//                        String incorrectAnswers = "";
//                        String correctAnswers = "";
//                        for (int a = 0; a < question.getIncorrectAnswers().size(); a++) { incorrectAnswers = incorrectAnswers + question.getIncorrectAnswers().get(a) + ","; }
//                        for (int a = 0; a < question.getCorrectAnswers().size(); a++) { correctAnswers = correctAnswers + question.getCorrectAnswers().get(a) + ","; }
//                        sql = "INSERT INTO TestQuestionMultichoice(questionNum, testID, question, incorrectAnswers, correctAnswers) " +
//                                "VALUES("+i+", "+(testCount+1)+", "+question.getQuestion()+", "+incorrectAnswers+", "+correctAnswers+")";
//                        stmt  = conn.createStatement();
//                        stmt.executeUpdate(sql);
//                        conn.close();
//                    }
                    if ( questions.get(i).getClass() == TestQuestionText.class ) {
                        TestQuestionText question = (TestQuestionText)questions.get(i);
                        conn = DriverManager.getConnection("jdbc:sqlite:C://Users/willj/IdeaProjects/untitled/school.db");
                        sql = "INSERT INTO TestQuestionText(uuid, questionNum, testID, question, answer) VALUES(1, "+i+", "+(testCount+1)+", "+question.getQuestion()+", "+question.getAnswer()+")";
                        stmt  = conn.createStatement();
                        stmt.executeUpdate(sql);
                        conn.close();
                    }
                }

            } catch (SQLException e) {
                System.out.println(e.getMessage());
            }

            Home home = new Home();
            home.start(primaryStage);
        } else {
            //otherwise display a validation popup, asking for the class ID
            final Stage dialog = new Stage();
            dialog.initModality(Modality.APPLICATION_MODAL);
            dialog.initOwner(primaryStage);
            VBox dialogVbox = new VBox(20);
            dialogVbox.getChildren().add(new Text("  "));
            dialogVbox.getChildren().add(new Text("     Please add the Class ID"));
            Scene dialogScene = new Scene(dialogVbox, 150, 100);
            dialog.setScene(dialogScene);
            dialog.show();
        }
    }
    public void exit(Stage primaryStage) {
        //go home
        Home home = new Home();
        home.start(primaryStage);
    }
}
