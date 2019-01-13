package models;

import java.util.ArrayList;

public class Buyer extends Person {
	private ArrayList<PurchaseRequest> purchaseRequest;

	public Buyer(){

	}

	public Buyer(int id) {
		this.id = id;
	}
}
