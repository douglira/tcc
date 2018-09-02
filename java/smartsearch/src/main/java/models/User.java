package models;

import java.math.BigInteger;
import java.sql.Timestamp;

import org.mindrot.jbcrypt.BCrypt;

import enums.Status;
import enums.UserRoles;

public class User {
	private int id;
	private String email;
	private String username;
	private String displayName;
	private String password;
	private String passwordResetToken;
	private Timestamp passwordExpiresIn;
	private UserRoles role = UserRoles.COMMON;
	private Status status;
	private Timestamp createdAt;
	private Timestamp updatedAt;

	public User() {

	}

	public User(String email) {
		super();
		this.email = email;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getPasswordResetToken() {
		return passwordResetToken;
	}

	public void setPasswordResetToken(String passwordResetToken) {
		this.passwordResetToken = passwordResetToken;
	}

	public Timestamp getPasswordExpiresIn() {
		return passwordExpiresIn;
	}

	public void setPasswordExpiresIn(Timestamp passwordExpiresIn) {
		this.passwordExpiresIn = passwordExpiresIn;
	}

	public UserRoles getRole() {
		return role;
	}

	public void setRole(UserRoles role) {
		this.role = role;
	}

	public Status getStatus() {
		return status;
	}

	public void setStatus(Status status) {
		this.status = status;
	}

	public Timestamp getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(Timestamp createdAt) {
		this.createdAt = createdAt;
	}

	public Timestamp getUpdatedAt() {
		return updatedAt;
	}

	public void setUpdatedAt(Timestamp updatedAt) {
		this.updatedAt = updatedAt;
	}
	
	public void generateDisplayName(Person person) {
		String fullName = person.getAccountOwner();
		String[] fullNameParts = fullName.split(" ");
		
		if (fullNameParts.length == 1) {
			this.displayName = fullNameParts[0];
			return;
		}
		
		if (fullNameParts.length == 2) {
			this.displayName = fullName;
			return;
		}
		
		String firstName = fullNameParts[0];
		String lastName = fullNameParts[fullNameParts.length - 1];
		
		this.displayName = firstName + " " + lastName;
	}
	
	public void hashPassword() {
		String password = this.password;
		
		String salt = BCrypt.gensalt(10);
		this.password = BCrypt.hashpw(password, salt);
	}
	
	public boolean checkPassword(String passwordPlainText) {
		boolean isValid = false;
		
		if (this.password == null || passwordPlainText == null || !this.password.startsWith("$2a$")) {
			return isValid;
		}
		
		isValid = BCrypt.checkpw(passwordPlainText, this.password);
		
		return isValid;
	}
}
