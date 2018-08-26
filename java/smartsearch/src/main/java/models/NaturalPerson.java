package models;

import java.math.BigInteger;
import java.util.Date;

import enums.Gender;

public class NaturalPerson extends Person {
	private Date birthday;
	private BigInteger cpf;
	private Gender gender;
	private BigInteger cel;

	public Date getBirthday() {
		return birthday;
	}

	public void setBirthday(Date birthday) {
		this.birthday = birthday;
	}

	public BigInteger getCpf() {
		return cpf;
	}

	public void setCpf(BigInteger cpf) {
		this.cpf = cpf;
	}

	public Gender getGender() {
		return gender;
	}

	public void setGender(Gender gender) {
		this.gender = gender;
	}

	public BigInteger getCel() {
		return cel;
	}

	public void setCel(BigInteger cel) {
		this.cel = cel;
	}
}
