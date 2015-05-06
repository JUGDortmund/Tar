package de.maredit.tar.models;

import java.util.Objects;

import org.springframework.data.annotation.Id;

import com.unboundid.util.Base64;

public class User {

	@Id
	private String id;

	private String uidNumber;
	private String firstName;
	private String lastName;
	private String username;
	private String mail;
	private String userImage = null;
	private Boolean active;

	public User() {
	}

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

	public String getPhoto() {
		return userImage;
	}

	public void setPhoto(byte[] imageData) {
		if (imageData != null) {
			this.userImage = Base64.encode(imageData);
		} 
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
		return "User [id=" + id + ", uidNumber=" + uidNumber + ", firstName="
				+ firstName + ", lastName=" + lastName + ", username="
				+ username + ", mail=" + mail + ", active=" + active + "]";
	}

}
