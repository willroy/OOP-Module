package sample.models.user;

import java.io.Serializable;

public class UserType implements Serializable {
    private String name;

    public UserType(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
