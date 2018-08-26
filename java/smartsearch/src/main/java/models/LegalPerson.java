package models;

import java.math.BigInteger;

public class LegalPerson extends Person {
	private String corporateName;
	private BigInteger stateRegistration;
	private BigInteger cnpj;

	public String getCorporateName() {
		return corporateName;
	}

	public void setCorporateName(String corporateName) {
		this.corporateName = corporateName;
	}

	public BigInteger getStateRegistration() {
		return stateRegistration;
	}

	public void setStateRegistration(BigInteger stateRegistration) {
		this.stateRegistration = stateRegistration;
	}

	public BigInteger getCnpj() {
		return cnpj;
	}

	public void setCnpj(BigInteger cnpj) {
		this.cnpj = cnpj;
	}
}
