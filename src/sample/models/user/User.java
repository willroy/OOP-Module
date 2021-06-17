package sample.models.user;

import sample.models.SchoolClass;

import java.io.Serializable;

public class User implements Serializable {
    private String username;
    private String password;
    private Integer userTypeID;
    private Integer schoolClassID;
    private Boolean loggedIn;

    public User(String username, String password, Integer userTypeID, Integer schoolClassID, Boolean loggedIn) {
        this.username = username;
        this.password = password;
        this.userTypeID = userTypeID;
        this.schoolClassID = schoolClassID;
        this.loggedIn = loggedIn;
    }

    public String getUsername() {
        return this.username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return this.password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Integer getUserTypeID() {
        return this.userTypeID;
    }

    public void setUserType(Integer userTypeID) {
        this.userTypeID = userTypeID;
    }

    public Integer getSchoolClassID() {
        return this.schoolClassID;
    }

    public void setSchoolClass(Integer schoolClassID) {
        this.schoolClassID = schoolClassID;
    }
}
