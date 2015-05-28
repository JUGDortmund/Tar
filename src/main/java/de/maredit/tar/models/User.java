package de.maredit.tar.models;

import java.util.Objects;

import org.springframework.data.annotation.Id;

import com.unboundid.util.Base64;

public class User {

  @Id
  private String id;

  private String uidNumber;
  private String firstname;
  private String lastname;
  private String username;
  private String mail;
  private String userImage = null;
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

  public String getUserImage() {
    return userImage;
  }

  public void setPhoto(byte[] imageData) {
    if (imageData != null) {
      this.userImage = Base64.encode(imageData);
    }
  }
  
  public void setUserImage(String userImage) {
    this.userImage = userImage;
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
    //TODO Fix data binding to use standard "toString" method
    return id;
//    return "User [id=" + id + ", uidNumber=" + uidNumber + ", firstname=" + firstname
//        + ", lastname=" + lastname + ", username=" + username + ", mail=" + mail + ", active="
//        + active + "]";
  }
}
