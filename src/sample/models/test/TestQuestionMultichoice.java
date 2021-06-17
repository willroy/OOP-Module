package sample.models.test;

import java.io.Serializable;
import java.util.List;

public class TestQuestionMultichoice extends TestQuestion implements Serializable {
    private String question;
    private List<String> incorrectAnswers;
    private List<String> correctAnswers;

    public TestQuestionMultichoice(String question, List<String> incorrectAnswers, List<String> correctAnswers) {
        this.question = question;
        this.incorrectAnswers = incorrectAnswers;
        this.correctAnswers = correctAnswers;
    }

    public String getQuestion() {
        return this.question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public List<String> getIncorrectAnswers() {
        return this.incorrectAnswers;
    }

    public void setIncorrectAnswers(List<String> incorrectAnswers) {
        this.incorrectAnswers = incorrectAnswers;
    }

    public List<String> getCorrectAnswers() {
        return this.correctAnswers;
    }

    public void setCorrectAnswers(List<String> correctAnswers) {
        this.correctAnswers = correctAnswers;
    }
}
