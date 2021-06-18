package sample.models.test;

import java.io.Serializable;

public class TestQuestion implements Serializable {
    private Integer questionNum;

    public Integer getQuestionNum() {
        return questionNum;
    }

    public void setQuestionNum(Integer questionNum) {
        this.questionNum = questionNum;
    }
}
