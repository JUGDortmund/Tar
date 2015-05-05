package de.maredit.tar.models;

import java.util.Objects;

import org.springframework.data.annotation.Id;

public class User {

  @Id
  private String id;

  private String uidNumber;
  private String firstname;
  private String lastname;
  private String username;
  private String mail;
  private Boolean active;

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

  public String getFirstname() {
    return firstname;
  }

  public void setFirstname(String firstname) {
    this.firstname = firstname;
  }

  public String getLastname() {
    return lastname;
  }

  public void setLastname(String lastname) {
    this.lastname = lastname;
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
    return this.firstname + " " + this.lastname;
  }

  @Override
  public String toString() {
    return "User [id=" + id + ", uidNumber=" + uidNumber + ", firstname=" + firstname
        + ", lastname=" + lastname + ", username=" + username + ", mail=" + mail + ", active="
        + active + "]";
  }
}
