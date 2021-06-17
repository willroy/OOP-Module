package sample.models.test;

import sample.models.SchoolClass;
import sample.models.user.User;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

public class Result {
    private Integer schoolClass;
    private User user;
    private List<TestQuestion> answers;
    private String firstName;
    private String lastName;
    private LocalDate DoB;

    public Result(Integer schoolClass, User user, List<TestQuestion> answers, String firstName, String lastName, LocalDate DoB) {
        this.schoolClass = schoolClass;
        this.user = user;
        this.answers = answers;
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public Integer getSchoolClass() {
        return this.schoolClass;
    }

    public void setSchoolClass(Integer schoolClass) {
        this.schoolClass = schoolClass;
    }

    public User getUser() {
        return this.user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public List<TestQuestion> getAnswers() {
        return this.answers;
    }

    public void setAnswers(List<TestQuestion> answers) {
        this.answers = answers;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public LocalDate getDoB() {
        return DoB;
    }

    public void setDoB(LocalDate doB) {
        DoB = doB;
    }
}
