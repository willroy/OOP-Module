package sample.views.test;

import javafx.application.Application;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import sample.models.test.*;
import sample.models.user.User;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.sql.*;
import java.time.LocalDate;
import java.util.*;

public class ViewTests extends Application {
    public static void main(String args[]) {
        launch(args);
    }

    public void start(Stage primaryStage) {
        //setup grid for scene
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(12);

        try {
            Connection conn = DriverManager.getConnection("jdbc:sqlite:school.db");
            String sql = "SELECT * FROM Test;";
            Statement stmt  = conn.createStatement();
            ResultSet rs    = stmt.executeQuery(sql);
            int count = 0;
            while (rs.next()) {
                //get school class
                count++;
                ResultSet classes  = conn.createStatement().executeQuery("SELECT * FROM SchoolClass;");

                //get test questions

                ResultSet questionsText  = conn.createStatement().executeQuery("SELECT * FROM TestQuestionText where testID='"+rs.getInt("uuid")+"' AND ( resultID IS NULL or resultID='' ) ORDER BY questionNum");
                ResultSet questionsMultichoice  = conn.createStatement().executeQuery("SELECT * FROM TestQuestionMultichoice where testID='"+rs.getInt("uuid")+"' AND ( resultID IS NULL or resultID='' ) ORDER BY questionNum");

                //add test questions to the questions classes

                List<TestQuestion> questions = new ArrayList<TestQuestion>();
                while (questionsText.next()) {
                    TestQuestionText tmpquestion = new TestQuestionText(questionsText.getString("question"), questionsText.getString("answer"));
                    tmpquestion.setQuestionNum(questionsText.getInt("questionNum"));
                    questions.add(tmpquestion);
                }

                while (questionsMultichoice.next()) {
                    List<String> incorrectAnswers = Arrays.asList(questionsMultichoice.getString("incorrectAnswers").split(",", -1));
                    List<String> correctAnswers = Arrays.asList(questionsMultichoice.getString("correctAnswers").split(",", -1));

                    TestQuestionMultichoice tmpquestion = new TestQuestionMultichoice(questionsMultichoice.getString("question"), incorrectAnswers, correctAnswers);
                    tmpquestion.setQuestionNum(questionsMultichoice.getInt("questionNum"));
                    questions.add(tmpquestion);
                }

                questions.sort(Comparator.comparing(TestQuestion::getQuestionNum));

                Label label = new Label();
                Button taketest = new Button();
                while ( classes.next() ) {
                    if (rs.getInt("schoolClassID") == classes.getInt("uuid"))
                    label = new Label("Test " + count + ", " + classes.getString("name"));
                    taketest = new Button("View Results");
                }

                int testID = rs.getInt("uuid");
                taketest.setOnAction(e ->viewResults(primaryStage,testID));

                int currentTestNumber = count;
                grid.add(label, 0, count);
                grid.add(taketest, 1, count);

            }
            conn.close();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        grid.setPadding(new Insets(10, 10, 10, 10));
        Scene scene = new Scene(grid, 300, 250);
        scene.getStylesheets().add("sample/stylesheet.css");

        primaryStage.setScene(scene);
        primaryStage.setTitle("Home");
        primaryStage.show();
    }

    public void viewResults(Stage primaryStage, int testID) {
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(12);

        try {
            Connection conn = DriverManager.getConnection("jdbc:sqlite:school.db");
            String sql = "SELECT * FROM Result where testID = "+testID+";";
            Statement stmt  = conn.createStatement();
            ResultSet rs    = stmt.executeQuery(sql);
            int count = 0;
            while ( rs.next() ) {
                count++;
                Label label = new Label("Result " + count + ", " + rs.getString("firstName") + " " + rs.getString("lastName"));
                Button taketest = new Button("View Result");
                int finalCount = count;
                taketest.setOnAction(e -> viewResult(primaryStage, testID, finalCount));
                grid.add(label, 0, count);
                grid.add(taketest, 1, count);
            }
            conn.close();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        grid.setPadding(new Insets(10, 10, 10, 10));
        Scene scene = new Scene(grid, 300, 250);
        scene.getStylesheets().add("sample/stylesheet.css");

        primaryStage.setScene(scene);
        primaryStage.setTitle("Home");
        primaryStage.show();
    }

    public void viewResult(Stage primaryStage, int testID, int resultID) {
        try {
            Connection conn = DriverManager.getConnection("jdbc:sqlite:school.db");

            ResultSet answersText  = conn.createStatement().executeQuery("SELECT * FROM TestQuestionText where resultID='"+resultID+"' ORDER BY questionNum");
            ResultSet answersMultichoice  = conn.createStatement().executeQuery("SELECT * FROM TestQuestionMultichoice where resultID='"+resultID+"' ORDER BY questionNum");

            List<TestQuestion> answers = new ArrayList<TestQuestion>();
            while (answersText.next()) {
                TestQuestionText tmpanswer = new TestQuestionText(answersText.getString("question"), answersText.getString("answer"));
                tmpanswer.setQuestionNum(answersText.getInt("questionNum"));
                answers.add(tmpanswer);
            }

            while (answersMultichoice.next()) {
                List<String> incorrectAnswers = Arrays.asList(answersMultichoice.getString("incorrectAnswers").split(",", -1));
                List<String> correctAnswers = Arrays.asList(answersMultichoice.getString("correctAnswers").split(",", -1));

                TestQuestionMultichoice tmpanswer = new TestQuestionMultichoice(answersMultichoice.getString("question"), incorrectAnswers, correctAnswers);
                tmpanswer.setQuestionNum(answersMultichoice.getInt("questionNum"));
                answers.add(tmpanswer);
            }

            answers.sort(Comparator.comparing(TestQuestion::getQuestionNum));


            ResultSet questionsText  = conn.createStatement().executeQuery("SELECT * FROM TestQuestionText where testID='"+testID+"' AND ( resultID IS NULL or resultID=0 ) ORDER BY questionNum");
            ResultSet questionsMultichoice  = conn.createStatement().executeQuery("SELECT * FROM TestQuestionMultichoice where testID='"+testID+"' AND ( resultID IS NULL or resultID=0 ) ORDER BY questionNum");

            List<TestQuestion> questions = new ArrayList<TestQuestion>();
            while (questionsText.next()) {
                TestQuestionText tmpquestion = new TestQuestionText(questionsText.getString("question"), questionsText.getString("answer"));
                tmpquestion.setQuestionNum(questionsText.getInt("questionNum"));
                questions.add(tmpquestion);
            }

            while (questionsMultichoice.next()) {
                List<String> incorrectAnswers = Arrays.asList(questionsMultichoice.getString("incorrectAnswers").split(",", -1));
                List<String> correctAnswers = Arrays.asList(questionsMultichoice.getString("correctAnswers").split(",", -1));

                TestQuestionMultichoice tmpquestion = new TestQuestionMultichoice(questionsMultichoice.getString("question"), incorrectAnswers, correctAnswers);
                tmpquestion.setQuestionNum(questionsMultichoice.getInt("questionNum"));
                questions.add(tmpquestion);
            }

            questions.sort(Comparator.comparing(TestQuestion::getQuestionNum));

            GridPane grid = new GridPane();
            grid.setHgap(10);
            grid.setVgap(12);

            //test answers to see if they are correct
            System.out.println(questions.size());
            for (int i = 0; i < questions.size(); i++) {
                if (questions.get(i) instanceof TestQuestionText) {
                    TestQuestionText question = (TestQuestionText)questions.get(i);
                    TestQuestionText answer = (TestQuestionText)answers.get(i);
                    if ( question.getAnswer().equals(answer.getAnswer()) ) {
                        Label label = new Label("Question "+(i+1)+" , " + "Correct");
                        grid.add(label, 0, 1+i);
                    } else {
                        Label label = new Label("Question "+(i+1)+" , " + "Incorrect");
                        grid.add(label, 0, 1+i);
                    }
                }
                if (questions.get(i) instanceof TestQuestionMultichoice) {
                    TestQuestionMultichoice question = (TestQuestionMultichoice)questions.get(i);
                    TestQuestionMultichoice answer = (TestQuestionMultichoice)answers.get(i);
                    question.getCorrectAnswers();
                    if ( question.getCorrectAnswers().get(0) == answer.getCorrectAnswers().get(0) ) {
                        Label label = new Label("Question "+(i+1)+" , " + "Correct");
                        grid.add(label, 0, 1+i);
                    } else {
                        Label label = new Label("Question "+(i+1)+" , " + "Incorrect");
                        grid.add(label, 0, 1+i);
                    }
                }
            }

            ScrollPane root = new ScrollPane();

            root.setContent(grid);
            grid.setPadding(new Insets(10, 10, 10, 10));
            Scene scene = new Scene(root, 300, 250);
            scene.getStylesheets().add("sample/stylesheet.css");
            primaryStage.setScene(scene);
            primaryStage.setTitle("Home");
            primaryStage.show();

            conn.close();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        //add test questions to the questions classes




    }
}
