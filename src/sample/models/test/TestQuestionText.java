package sample.models.test;

import java.io.Serializable;
import java.util.List;

public class TestQuestionText extends TestQuestion implements Serializable {
    private String question;
    private String answer;

    public TestQuestionText(String question, String answer) {
        this.question = question;
        this.answer = answer;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getAnswer() {
        return this.answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }
}
