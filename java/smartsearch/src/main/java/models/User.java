package models;

import java.math.BigInteger;
import java.sql.Timestamp;

import enums.Status;
import enums.UserRoles;

public class User {
	private BigInteger id;
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

	public User(String email, String password) {
		super();
		this.email = email;
		this.password = password;
	}

	public BigInteger getId() {
		return id;
	}

	public void setId(BigInteger id) {
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

}
