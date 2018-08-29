package models;

public class LegalPerson extends Person {
	private String corporateName;
	private long stateRegistration;
	private long cnpj;

	public String getCorporateName() {
		return corporateName;
	}

	public void setCorporateName(String corporateName) {
		this.corporateName = corporateName;
	}

	public long getStateRegistration() {
		return stateRegistration;
	}

	public void setStateRegistration(long stateRegistration) {
		this.stateRegistration = stateRegistration;
	}

	public long getCnpj() {
		return cnpj;
	}

	public void setCnpj(long cnpj) {
		this.cnpj = cnpj;
	}
}
