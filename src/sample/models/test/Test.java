package sample.models.test;

import sample.models.SchoolClass;
import sample.models.user.UserType;

import java.io.Serializable;
import java.util.List;

public class Test implements Serializable {
    private Integer schoolClassID;
    private Boolean active;
    private List<TestQuestion> questions;
    private List<Result> results;

    public Test(Integer schoolClassID, Boolean active, List<TestQuestion> questions) {
        this.schoolClassID = schoolClassID;
        this.active = active;
        this.questions = questions;
    }

    public Integer getSchoolClass() {
        return schoolClassID;
    }

    public void setSchoolClass(Integer schoolClassID) {
        this.schoolClassID = schoolClassID;
    }

    public Boolean getActive() {
        return this.active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public List<TestQuestion> getQuestions() {
        return this.questions;
    }

    public void setQuestions(List<TestQuestion> questions) {
        this.questions = questions;
    }

    public List<Result> getResults() {
        return results;
    }

    public void setResults(List<Result> results) {
        this.results = results;
    }
}
