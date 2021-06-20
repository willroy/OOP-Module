package sample.models.test;

import java.io.Serializable;

public abstract class TestQuestion implements Serializable {
    protected String question;
    private Integer questionNum;

    public TestQuestion() {
    }

    public TestQuestion(String question) {
        this.question = question;
    }

    public Integer getQuestionNum() {
        return questionNum;
    }

    public void setQuestionNum(Integer questionNum) {
        this.questionNum = questionNum;
    }

    public String getQuestion() {
        return this.question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }
}
