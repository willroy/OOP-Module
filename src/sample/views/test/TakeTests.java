package sample.views.test;

import javafx.application.Application;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import sample.models.SchoolClass;
import sample.models.test.*;
import sample.models.user.User;
import sample.views.Home;

import java.io.*;
import java.sql.*;
import java.time.LocalDate;
import java.util.*;

public class TakeTests extends Application {
    private User user;
    private String firstName;
    private String lastName;
    private LocalDate dob;

    public static void main(String args[]) {
        launch(args);
    }

    public void start(Stage primaryStage) {
        //setup grid for scene
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(12);

        User user = null;
        int userTypeID = 0;
        int schoolClassID = 0;
        try {
            Connection conn = DriverManager.getConnection("jdbc:sqlite:school.db");
            String sql = "SELECT * FROM User where loggedIn = 1";
            Statement stmt  = conn.createStatement();
            ResultSet rs    = stmt.executeQuery(sql);
            while (rs.next()) {
                user = new User(rs.getString("username"), rs.getString("password"), rs.getInt("userTypeID"), rs.getInt("schoolClassID"), true);
                userTypeID = rs.getInt("userTypeID");
                schoolClassID =  rs.getInt("schoolClassID");
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        try {
            Connection conn = DriverManager.getConnection("jdbc:sqlite:school.db");
            String sql = "SELECT * FROM Test where schoolClassID='"+schoolClassID+"'";
            Statement stmt  = conn.createStatement();
            ResultSet rs    = stmt.executeQuery(sql);
            int count = 0;
            while (rs.next()) {
                //get school class
                count++;
                ResultSet classes  = conn.createStatement().executeQuery("SELECT * FROM SchoolClass where uuid='"+schoolClassID+"'");

                //get test questions

                ResultSet questionsText  = conn.createStatement().executeQuery("SELECT * FROM TestQuestionText where testID='"+rs.getInt("uuid")+"' ORDER BY questionNum");
                ResultSet questionsMultichoice  = conn.createStatement().executeQuery("SELECT * FROM TestQuestionMultichoice where testID='"+rs.getInt("uuid")+"' ORDER BY questionNum");

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

                Label label = new Label("Test " + count + ", " + classes.getString("name"));
                Button takeTest = new Button("Take Test");

                int currentTestNumber = count;
                Test currentTest = new Test(schoolClassID, true, questions);
                int finalSchoolClassID = schoolClassID;
                takeTest.setOnAction(e ->takeDetails(primaryStage, currentTest, finalSchoolClassID, currentTestNumber));
                grid.add(label, 0, count);
                grid.add(takeTest, 1, count);

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

    public void takeDetails(Stage primaryStage, Test test, int schoolClass, int currentTestNumber) {
        //launch the details screen for taking the users details
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(12);

        //setup firstname lastname and date of birth fields
        TextField firstName = new TextField("FirstName");
        TextField lastName = new TextField("LastName");
        final DatePicker datePicker = new DatePicker();
        datePicker.setOnAction(new EventHandler() {
            public void handle(Event t) {
                LocalDate date = datePicker.getValue();
            }
        });

        Button next = new Button("Next");

        grid.add(firstName, 0, 1);
        grid.add(lastName, 0, 2);
        grid.add(datePicker, 0, 3);
        grid.add(next, 0, 4);

        //once next is clicked, then the test starts
        next.setOnAction(e -> {takeTest(primaryStage, test, schoolClass, currentTestNumber);});

        grid.setPadding(new Insets(10, 10, 10, 10));
        Scene scene = new Scene(grid, 300, 250);
        scene.getStylesheets().add("sample/stylesheet.css");
        primaryStage.setScene(scene);
        primaryStage.setTitle("Home");
        primaryStage.show();
    }

    public void takeTest(Stage primaryStage, Test test, int schoolClass, int currentTestNumber) {
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(12);

        //setup some arrays for tracking the answers that the user makes
        int height = 0;
        List<TextField> TextQuestionAnswers = new ArrayList<TextField>();
        List<List<CheckBox>> MultichoiceQuestionAnswers = new ArrayList<List<CheckBox>>();
        List<List<Label>> MultichoiceQuestions = new ArrayList<List<Label>>();


        //counter for getting the total questions added and specific questiontypes added
        int questionCounter = 0;
        int countTextQuestion = 0;
        int countMultiQuestion = 0;

        for (int i = 0; i < test.getQuestions().size(); i++) {
            //check if the current question is a text type or multichoice
            if (test.getQuestions().get(i) instanceof TestQuestionText) {
                //if it is text type then place a textfield for the answer
                TestQuestionText question = (TestQuestionText)test.getQuestions().get(i);
                Label name = new Label(question.getQuestion());
                TextField answer = new TextField();

                TextQuestionAnswers.add(answer);

                grid.add(name, 0, 1+height);
                grid.add(TextQuestionAnswers.get(countTextQuestion), 0, 2+height);

                questionCounter++;
                countTextQuestion++;

                height = height + 3;
            }

            if (test.getQuestions().get(i) instanceof TestQuestionMultichoice) {
                //if it is a multiple choice question, then display the answrs and the checkboxes that are used for answering
                TestQuestionMultichoice question = (TestQuestionMultichoice)test.getQuestions().get(i);
                Label name = new Label(question.getQuestion());
                List<Label> answerLabels = new ArrayList<Label>();
                List<CheckBox> answerChecks = new ArrayList<CheckBox>();
                System.out.println(question.getCorrectAnswers().size());
                for (int a = 0; a < question.getCorrectAnswers().size(); a++) {
                    System.out.println("> "+question.getCorrectAnswers().get(a));
                    answerLabels.add(new Label(question.getCorrectAnswers().get(a)));
                    answerChecks.add(new CheckBox());
                }
                for (int a = 0; a < question.getIncorrectAnswers().size(); a++) {
                    System.out.println(">> "+question.getIncorrectAnswers().get(a));
                    answerLabels.add(new Label(question.getIncorrectAnswers().get(a)));
                    answerChecks.add(new CheckBox());
                }

                List<Integer> numbers = new ArrayList<Integer>();
                numbers.add(0);
                numbers.add(1);
                numbers.add(2);
                numbers.add(3);

                Collections.shuffle(numbers);

                grid.add(name, 0, 1+height);
                grid.add(answerLabels.get(numbers.get(0)), 0, 2+height);
                grid.add(answerLabels.get(numbers.get(1)), 0, 3+height);
                grid.add(answerLabels.get(numbers.get(2)), 0, 4+height);
                grid.add(answerLabels.get(numbers.get(3)), 0, 5+height);

                System.out.println(answerLabels.get(numbers.get(0)));
                System.out.println(answerLabels.get(numbers.get(1)));
                System.out.println(answerLabels.get(numbers.get(2)));
                System.out.println(answerLabels.get(numbers.get(3)));

                grid.add(answerChecks.get(numbers.get(0)), 1, 2+height);
                grid.add(answerChecks.get(numbers.get(1)), 1, 3+height);
                grid.add(answerChecks.get(numbers.get(2)), 1, 4+height);
                grid.add(answerChecks.get(numbers.get(3)), 1, 5+height);

                MultichoiceQuestionAnswers.add(answerChecks);
                MultichoiceQuestions.add(answerLabels);

                height = height + 6;
                countMultiQuestion++;
            }
        }

        //when submitted, the answers are then taken to end the test, where they are saved.
        Button next = new Button("Submit");
        next.setOnAction(e->endTest(primaryStage, test, schoolClass, TextQuestionAnswers, MultichoiceQuestionAnswers, MultichoiceQuestions, currentTestNumber));
        grid.add(next, 0, 0);

        ScrollPane root = new ScrollPane();

        root.setContent(grid);

        grid.setPadding(new Insets(10, 10, 10, 10));
        Scene scene = new Scene(root, 300, 250);
        scene.getStylesheets().add("sample/stylesheet.css");
        primaryStage.setScene(scene);
        primaryStage.setTitle("Home");
        primaryStage.show();

//        Result result = new Result(schoolClass, user, );

    }

    public void endTest(Stage primaryStage, Test test, int schoolClass, List<TextField> TextQuestionAnswers, List<List<CheckBox>> MultichoiceQuestionAnswers, List<List<Label>> MultichoiceQuestions, int currentTestNumber) {
        //create new answers list, similiar to how the test questions are stored.
        List<TestQuestion> answers = new ArrayList<TestQuestion>();

        int countTextQuestion = 0;
        int countMultiQuestion = 0;

        for (int i = 0; i< TextQuestionAnswers.size() + MultichoiceQuestionAnswers.size(); i++) {
            //check what type the question is and loop through all of them
            if ( test.getQuestions().get(i) instanceof TestQuestionText ) {
                //add the text answer if text type
                TestQuestionText question = (TestQuestionText)test.getQuestions().get(i);
                String questionText = question.getQuestion();
                TestQuestion answer = new TestQuestionText(questionText, TextQuestionAnswers.get(countTextQuestion).getText());
                answers.add(answer);
                countTextQuestion++;
            }
            if ( test.getQuestions().get(i) instanceof TestQuestionMultichoice ) {
                //add the answer/s chosen / not chosen if mutliple choise
                TestQuestionMultichoice question = (TestQuestionMultichoice)test.getQuestions().get(i);
                String questionText = question.getQuestion();
                List<String> correctAnswers = new ArrayList<String>();
                List<String> incorrectAnswers = new ArrayList<String>();
                for (int o = 0; o < MultichoiceQuestionAnswers.get(countMultiQuestion).size(); o++) {
                    if ( MultichoiceQuestionAnswers.get(countMultiQuestion).get(o).isSelected() ) {
                        correctAnswers.add(MultichoiceQuestions.get(countMultiQuestion).get(o).getText());
                    }
                    if ( !MultichoiceQuestionAnswers.get(countMultiQuestion).get(o).isSelected() ) {
                        incorrectAnswers.add(MultichoiceQuestions.get(countMultiQuestion).get(o).getText());
                    }
                }
                TestQuestion answer = new TestQuestionMultichoice(questionText, incorrectAnswers, correctAnswers);
                answers.add(answer);
                countMultiQuestion++;
            }
        }

        System.out.println(answers.size());
        //finally, create a result which has all the information for a users test experience
        Result result = new Result(schoolClass, user, answers, firstName, lastName, dob);
        //save the test result
        save(primaryStage, test, schoolClass, result, currentTestNumber);
    }

    public void save(Stage primaryStage, Test test, int schoolClass, Result result, int currentTestNumber) {
        //first create a new test that the old test object will be built on top off
        Test savedtest = new Test(schoolClass, false, test.getQuestions());
        try {
            //get the test that was just taken and add the results onto the new test
            File directory=new File("Tests");
            FileOutputStream fileOut = new FileOutputStream("Tests/test"+currentTestNumber+".ser");
            ObjectOutputStream out = new ObjectOutputStream(fileOut);
            //null check the results to handle the first result
            List<Result> results = new ArrayList<Result>();
            if ( savedtest.getResults() != null ) { results = savedtest.getResults(); }
            results.add(result);
            savedtest.setResults(results);
            out.writeObject(test);
            out.close();
            fileOut.close();
            System.out.printf("Serialized data is saved in test"+currentTestNumber+".ser");
        } catch (IOException i) {
            i.printStackTrace();
        }

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(12);

        //test answers to see if they are correct
        for (int i = 0; i < savedtest.getQuestions().size(); i++) {
            if (savedtest.getQuestions().get(i) instanceof TestQuestionText) {
                TestQuestionText question = (TestQuestionText)savedtest.getQuestions().get(i);
                int numberOfResults = savedtest.getResults().size();
                TestQuestionText answer = (TestQuestionText)result.getAnswers().get(i);
                System.out.println("Question " + question.getAnswer());
                System.out.println("AnswerU " + answer.getAnswer());
                if ( question.getAnswer().equals(answer.getAnswer()) ) {
                    System.out.println("true");
                    Label label = new Label("Question "+(i+1)+" , " + "Correct");
                    grid.add(label, 0, 1+i);
                } else {
                    System.out.println("false");
                    Label label = new Label("Question "+(i+1)+" , " + "Incorrect");
                    grid.add(label, 0, 1+i);
                }
            }

            if (savedtest.getQuestions().get(i) instanceof TestQuestionMultichoice) {
                TestQuestionMultichoice question = (TestQuestionMultichoice)savedtest.getQuestions().get(i);
                int numberOfResults = savedtest.getResults().size();
                TestQuestionMultichoice answers = (TestQuestionMultichoice)result.getAnswers().get(i);
                question.getCorrectAnswers();
                if ( question.getCorrectAnswers().get(0) == answers.getCorrectAnswers().get(0) ) {
                    System.out.println("true");
                    Label label = new Label("Question "+(i+1)+" , " + "Correct");
                    grid.add(label, 0, 1+i);
                } else {
                    System.out.println("false");
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
    }
}
