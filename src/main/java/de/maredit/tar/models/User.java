package de.maredit.tar.models;

import org.springframework.data.annotation.Id;

public class User {

  @Id
  private String id;

  private String firstName;
  private String lastName;
  private Boolean active;

  public User() {
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

  public Boolean isActive() {
    return active;
  }

  public void setActive(Boolean active) {
    this.active = active;
  }
}