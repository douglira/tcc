package models;

import java.util.ArrayList;
import java.util.Calendar;

public class Quote {
	private int id;
	private Seller seller;
	private ArrayList<Product> customListProduct;
	private Shipping shipping;
	private String additionalData;
	private String status;
	private double totalAmount;
	private Calendar expirationDate;
	private Calendar createdAt;
}
