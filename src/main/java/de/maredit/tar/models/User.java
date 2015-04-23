package de.maredit.tar.models;

import org.springframework.data.annotation.Id;

public class User {
    @Id
    private String id;

    private String firstName;
    private String lastName;
    private String mail;
    
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
    
    public String getMail() {
        return mail;
      }

      public void setMail(String mail) {
        this.mail = mail;
      }

	@Override
	public String toString() {
		return "User [id=" + id + ", firstName=" + firstName + ", lastName="
				+ lastName + "]";
	}
}