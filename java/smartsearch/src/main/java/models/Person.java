package models;

import java.util.Calendar;

public class Person {
    protected int id;
    protected Address address;
    protected String accountOwner;
    protected long tel;
    protected User user;
    protected String corporateName;
    protected long stateRegistration;
    protected long cnpj;
    protected Calendar createdAt;
    protected Calendar updatedAt;

    public Person() {

    }

    public Person(int id) {
        this.id = id;
    }

    public Person(User user) {
        this.user = user;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public String getAccountOwner() {
        return accountOwner;
    }

    public void setAccountOwner(String accountOwner) {
        this.accountOwner = accountOwner;
    }

    public long getTel() {
        return tel;
    }

    public void setTel(long tel) {
        this.tel = tel;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

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
}
