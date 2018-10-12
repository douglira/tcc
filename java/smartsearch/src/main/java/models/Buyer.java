package models;

import java.util.ArrayList;
import java.util.Calendar;

public class Buyer extends Person {
	private ArrayList<PurchaseRequest> purchaseRequest;

	public Buyer(){

	}

	public Buyer(int id) {
		this.id = id;
	}
}
