package de.maredit.tar.models;

import org.springframework.data.annotation.Id;

public class User {

  @Id
  private String id;

  private String uidNumber;
  private String firstName;
  private String lastName;
  private String username;
  private Boolean active;

  public User() {
  }

  public String getUidNumber() {
	return uidNumber;
}

public void setUidNumber(String uidNumber) {
	this.uidNumber = uidNumber;
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

  public String getUsername() {
	return username;
}

public void setUsername(String username) {
	this.username = username;
}

public Boolean isActive() {
    return active;
  }

  public void setActive(Boolean active) {
    this.active = active;
  }
}