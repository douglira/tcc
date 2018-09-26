package models;

import java.util.ArrayList;
import java.util.Calendar;

import enums.QuotesExpiration;

public class Seller extends Person {
	private QuotesExpiration quotesExpirationPeriod;
	private ArrayList<Product> inventary;

	public QuotesExpiration getQuotesExpirationPeriod() {
		return quotesExpirationPeriod;
	}

	public void setQuotesExpirationPeriod(QuotesExpiration quotesExpirationPeriod) {
		this.quotesExpirationPeriod = quotesExpirationPeriod;
	}

	public ArrayList<Product> getInventary() {
		return inventary;
	}

	public void setInventary(ArrayList<Product> inventary) {
		this.inventary = inventary;
	}
}
