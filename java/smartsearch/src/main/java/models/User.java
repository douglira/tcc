package models;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Calendar;

import org.mindrot.jbcrypt.BCrypt;

import enums.Status;
import enums.UserRoles;

public class User {
    private int id;
    private Person person;
    private File avatar;
    private String email;
    private String username;
    private String displayName;
    private String password;
    private String passwordResetToken;
    private Calendar passwordExpiresIn;
    private UserRoles role;
    private Status status;
    private Calendar createdAt;
    private Calendar updatedAt;
    private Calendar lastActive;
    private Calendar lastInactive;

    public User() {

    }

    public User(String email) {
        super();
        this.email = email;
    }

    public User(int id, String username) {
        this.id = id;
        this.username = username;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Person getPerson() {
        return person;
    }

    public void setPerson(Person person) {
        this.person = person;
    }

    public File getAvatar() {
        return this.avatar;
    }

    public void setAvatar(File avatar) {
        this.avatar = avatar;
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

    public Calendar getPasswordExpiresIn() {
        return passwordExpiresIn;
    }

    public void setPasswordExpiresIn(Calendar passwordExpiresIn) {
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

    public Calendar getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Calendar createdAt) {
        this.createdAt = createdAt;
    }

    public Calendar getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Calendar updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Calendar getLastActive() {
        return lastActive;
    }

    public void setLastActive(Calendar lastActive) {
        this.lastActive = lastActive;
    }

    public Calendar getLastInactive() {
        return lastInactive;
    }

    public void setLastInactive(Calendar lastInactive) {
        this.lastInactive = lastInactive;
    }

    public void generateDisplayName(String fullName) {
        String[] fullNameParts = fullName.split(" ");

        if (fullNameParts.length == 1) {
            this.displayName = fullNameParts[0];
            return;
        }

        if (fullNameParts.length == 2) {
            this.displayName = fullName;
            return;
        }

        if (fullNameParts[0].length() <= 3) {
            this.displayName = fullNameParts[0] + " " + fullNameParts[1] + " "
                    + fullNameParts[fullNameParts.length - 1];
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

    public void processPassResetToken() throws NoSuchAlgorithmException {
        MessageDigest messageDigest = null;
        messageDigest = MessageDigest.getInstance("MD5");
        String str = Calendar.getInstance().getTimeInMillis() + "_" + this.email;
        messageDigest.update(str.getBytes(), 0, str.length());
        this.passwordResetToken = ((String) new BigInteger(1, messageDigest.digest()).toString(16)).toUpperCase();

        Calendar expiresIn = Calendar.getInstance();
        expiresIn.add(Calendar.MINUTE, 10);

        this.passwordExpiresIn = expiresIn;
    }

    public boolean isExpiredResetPassword() {
        boolean isExpired = true;

        Calendar cal = Calendar.getInstance();
        long now = cal.getTimeInMillis();
        long past = this.passwordExpiresIn.getTimeInMillis();

        if (now > past) {
            return isExpired;
        }

        isExpired = false;
        return isExpired;
    }
}
