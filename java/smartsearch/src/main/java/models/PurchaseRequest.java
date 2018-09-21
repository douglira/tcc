package models;

import java.util.ArrayList;
import java.util.Calendar;

public class PurchaseRequest {
	private int id;
	private Buyer buyer;
	private ArrayList<ProductList> listProducts;
	private ArrayList<Quote> quotes;
	private String additionalData;
	private String status;
	private Calendar dueDateAverage;
	private double totalAmount;
	private Calendar createdAt;
	private Calendar closedAt;
}
