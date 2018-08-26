package models;

import java.math.BigInteger;
import java.sql.Timestamp;

import enums.PersonType;

public class Person {
	private long id;
	private PersonType personType;
	private String name;
	private BigInteger tel;
	private Timestamp createdAt;
	private Timestamp updatedAt;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public PersonType getPersonType() {
		return personType;
	}

	public void setPersonType(PersonType personType) {
		this.personType = personType;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public BigInteger getTel() {
		return tel;
	}

	public void setTel(BigInteger tel) {
		this.tel = tel;
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
