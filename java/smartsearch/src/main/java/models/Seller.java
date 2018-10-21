package models;

import java.util.ArrayList;

public class Seller extends Person {
    private int quotesExpirationPeriod;
    private ArrayList<Product> inventary;

    public Seller() {
        super();
    }

    public Seller(int id) {
        super(id);
    }

    public int getQuotesExpirationPeriod() {
        return quotesExpirationPeriod;
    }

    public void setQuotesExpirationPeriod(int quotesExpirationPeriod) {
        this.quotesExpirationPeriod = quotesExpirationPeriod;
    }

    public ArrayList<Product> getInventary() {
        return inventary;
    }

    public void setInventary(ArrayList<Product> inventary) {
        this.inventary = inventary;
    }
}
