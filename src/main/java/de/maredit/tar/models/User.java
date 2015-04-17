package de.maredit.tar.models;

import org.springframework.data.annotation.Id;

public class User {
    @Id
    private String id;

    private String firstName;
    private String lastName;
    
    public User(){
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
}