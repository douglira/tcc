package models;

import java.time.LocalDateTime;
import java.util.Calendar;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import enums.Role;
import enums.UserStatus;

@Entity
@SequenceGenerator(name = "SEQ_USERS", sequenceName = "SEQ_USERS", initialValue = 100)
public class User {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_USERS")
	@Column(nullable = false)
	private Integer id;
	
	@Column(unique = true, nullable = false)
	private String email;
	
	@Column(nullable = false)
	private String displayName;
	
	@Column(nullable = false)
	private String password;
	
	@Column(nullable = true)
	private String passwordResetToken;
	
	@Column(nullable = true)
	@Temporal(TemporalType.TIMESTAMP)
	private LocalDateTime passwordExpiresIn;
	
	@Column(nullable = false)
	@Enumerated(EnumType.STRING)
	private Role role = Role.USER;
	
	@Column(nullable = false)
	@Enumerated(EnumType.STRING)
	private UserStatus status = UserStatus.ACTIVE;
	
	@Column(nullable = false)
	@CreationTimestamp
	private LocalDateTime createdAt;
	
	@Column(nullable = false)
	@UpdateTimestamp
	private LocalDateTime updatedAt;
}
