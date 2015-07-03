package de.maredit.tar.data;

import com.unboundid.util.Base64;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Objects;

@Document
public class User {

  @Id
  private String id;

  private String uidNumber;
  private String firstname;
  private String lastname;
  private String username;
  private String mail;
  private String userImage = null;
  private Double vacationDays;
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
    return Objects.equals(this.uidNumber, other.uidNumber);
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

  public Double getVacationDays() {
    return vacationDays;
  }

  public void setVacationDays(Double vacationDays) {
    this.vacationDays = vacationDays;
  }
}
