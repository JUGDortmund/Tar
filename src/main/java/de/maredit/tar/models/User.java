package de.maredit.tar.models;

import java.util.Objects;

import org.springframework.data.annotation.Id;

public class User {

  @Id
  private String id;

  private String uidNumber;
  private String firstName;
  private String lastName;
  private String username;
  private String mail;
  private boolean active;

  public User() {}

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
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

  public boolean isActive() {
    return active;
  }

  public void setActive(boolean active) {
    this.active = active;
  }

  public String getMail() {
    return mail;
  }

  public void setMail(String mail) {
    this.mail = mail;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    final User other = (User) obj;
    if (!Objects.equals(this.uidNumber, other.uidNumber)) {
      return false;
    }
    return true;
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(this.uidNumber);
  }

  public String getFullname() {
    return this.firstName + " " + this.lastName;
  }

  @Override
  public String toString() {
    return "User [id=" + id + ", uidNumber=" + uidNumber + ", firstName=" + firstName
        + ", lastName=" + lastName + ", username=" + username + ", mail=" + mail + ", active="
        + active + "]";
  }
}
